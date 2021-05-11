package com.example.smart_mirror.MYPAGE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.BOARD.Board;
import com.example.smart_mirror.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyPage_Activity extends AppCompatActivity {

    private static final String TAG = "SUNGJAE";
    private static String IP_ADDRESS = "";
    private String mJsonString;

    private List<String> list;

    TextView MyPage_ID;
    ImageView MyPage_Profile;

    int usernum;

    String UserNum;
    String UserName;
    String UserId;
    String UserBirth;
    String UserGender;

    String imageFileName;


    Intent intent;
    String intent_id;

    private final String BUCKET = "";
    private final String KEY = "";
    private final String SECRET = "";

    private AmazonS3Client s3Client;
    private BasicAWSCredentials credentials;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity);
        list = new ArrayList<>();

        credentials = new BasicAWSCredentials(KEY, SECRET);
        s3Client = new AmazonS3Client(credentials);

        progressDialog = ProgressDialog.show(MyPage_Activity.this,"잠시만 기다려주세요...", null, true, true);

        intent = getIntent();
        intent_id = intent.getStringExtra("id");

        MyPage_ID = (TextView) findViewById(R.id.mp_userID);
        MyPage_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPage_Activity.this, MyPage_Modify.class);
                intent.putExtra("id", intent_id);
                intent.putExtra("usernum", usernum);
                startActivity(intent);
            }
        });


        MyPage_Profile = (ImageView) findViewById(R.id.mp_Profile);
        MyPage_Profile.setBackground(new ShapeDrawable(new OvalShape()));
        MyPage_Profile.setClipToOutline(true);



        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        UserNum = jsonObject.getString("usernum");
                        UserName = jsonObject.getString("name");
                        UserId = jsonObject.getString("id");
                        UserBirth = jsonObject.getString("age");
                        UserGender = jsonObject.getString("gender");

                        usernum = Integer.valueOf(UserNum);

                        progressDialog.dismiss();
                        MyPage_ID.setText(UserId);

                        checkImageFile();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_Request myPage_request = new MyPage_Request(intent_id, responseListener);
        RequestQueue queue22 = Volley.newRequestQueue(MyPage_Activity.this);
        queue22.add(myPage_request);

    }

    // Activity가 다시 재개 되었을 때, 실행되는 메소드.
    // 마이페이지 수정에서 프로필을  변경하였으면 onResume 메소드를 통해 다시 이미지 체크를 하고 setBitmap을 해준다.
    @Override
    protected void onResume() {
        super.onResume();

        checkImageFile();
    }

    private void checkImageFile(){


        // TODO : MySql에서 최신 파일명을 가져와서 success == true이면 해당 파일명을 통해서
        //  AWS S3 BUCKET의 이미지를 가져오고 프로필 사진을 지정해준다.
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    // MySql에서 가져온 최신 파일명이 있다면 imageFileName으로 대체해주고 이를 통해 이미지를 가져온다.
                    // imageFileName이 MySql에서 가져와진 것이 있다면, 프로필로 미리 지정해준다.
                    if (success) {
                        imageFileName = jsonObject.getString("imageFileName");

                        downloadFile();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_ImageDownload myPage_imageDownload = new MyPage_ImageDownload(usernum, responseListener);
        RequestQueue queue11 = Volley.newRequestQueue(MyPage_Activity.this);
        queue11.add(myPage_imageDownload);
    }

    private void downloadFile() {
        // fileUri에 확장자가 붙고, images가 들어간 이미지를 임시 파일로 생성하여 localFile에 담아준다.
        final File localFile;
        try {
            localFile = File.createTempFile("images", ".jpg");

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            // 다운로드를 하기위해 Bucket에 접근. 이미지 확장자까지 붙이게 되어있넹..
            TransferObserver downloadObserver =
                    transferUtility.download(BUCKET, "MyPage_Image/" + usernum + "/" + imageFileName+ ".jpg", localFile);

            downloadObserver.setTransferListener(new TransferListener() {

                // 다운로드 성공.
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();

                        // 복호화하기
                        Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        MyPage_Profile.setImageBitmap(bmp);
                        MyPage_Profile.setBackground(new ShapeDrawable(new OvalShape()));
                        MyPage_Profile.setClipToOutline(true);
                        MyPage_Profile.setScaleType(ImageView.ScaleType.FIT_XY);

                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;

                }

                @Override
                public void onError(int id, Exception ex) {
                    ex.printStackTrace();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
