package com.example.smart_mirror.BOARD;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.smart_mirror.CustomAnimationDialog;
import com.example.smart_mirror.MYPAGE.MyPage_ImageDownload;
import com.example.smart_mirror.MYPAGE.MyPage_Request;
import com.example.smart_mirror.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class BoardRead_Activity extends AppCompatActivity {

    private Intent intent;
    private String intent_id;
    private String Title;
    private String Content;

    private TextView tv_BoardRead_Title;
    private TextView tv_BoardRead_Content;
    private TextView tv_BoardRead_userId;

    private ImageView iv_BoardRead_img, iv_BoardRead_ProfileImg;


    String id;
    String Real_Title;
    String Real_Content;
    String ImageFileName, ProfileImageFileName;
    String userId;

    String UserNum;
    String UserName;
    String UserId;
    String UserBirth;
    String UserGender;

    int usernum;

    private CustomAnimationDialog customAnimationDialog;

    private final String BUCKET = "";
    private final String KEY = "";
    private final String SECRET = "";

    private AmazonS3Client s3Client;
    private BasicAWSCredentials credentials;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boardread_activity);

        credentials = new BasicAWSCredentials(KEY, SECRET);
        s3Client    = new AmazonS3Client(credentials);

        intent = getIntent();

//        intent_id = intent.getStringExtra("id");
        Title   = intent.getStringExtra("TITLE");
        Content = intent.getStringExtra("CONTENT");

        tv_BoardRead_Title      = (TextView) findViewById(R.id.tv_BoardRead_Title);
        tv_BoardRead_Content    = (TextView) findViewById(R.id.tv_BoardRead_Content);
        tv_BoardRead_userId     = (TextView) findViewById(R.id.tv_BoardRead_userId);

        iv_BoardRead_img        = (ImageView) findViewById(R.id.iv_BoardRead_img);
        iv_BoardRead_ProfileImg = (ImageView) findViewById(R.id.iv_BoardRead_ProfileImg);

        iv_BoardRead_ProfileImg.setBackground(new ShapeDrawable(new OvalShape()));
        iv_BoardRead_ProfileImg.setClipToOutline(true);

        customAnimationDialog = new CustomAnimationDialog(BoardRead_Activity.this);
        customAnimationDialog.show();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        id              = jsonObject.getString("id");
                        Real_Title      = jsonObject.getString("Title");
                        Real_Content    = jsonObject.getString("Content");
                        ImageFileName   = jsonObject.getString("ImageFileName");
                        userId          = jsonObject.getString("userId");

                        tv_BoardRead_Title  .setText(Real_Title);
                        tv_BoardRead_Content.setText(Real_Content);
                        tv_BoardRead_userId .setText(userId);

                        /**
                         * UpLoad 되었던 데이터 베이스에 이미지 파일이 있다면, 다운로드 받아 게시글에 이미지 설정해준다.
                         */
                        if (ImageFileName != null) {
                            checkUserId();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // TODO : 게시 제목과 내용으로 올린 사람의 userId를 가져옴.
        BoardRead_Request boardRead_request = new BoardRead_Request(Title, Content, responseListener);
        RequestQueue queue = Volley.newRequestQueue(BoardRead_Activity.this);
        queue.add(boardRead_request);

    }

    // TODO : userId를 사용하고 있는 사용자가 설정한 프로필 이미지가 있는지 확인하고 있으면 다운로드 해야함.
    private void checkProfileImg() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    boolean success         = jsonObject.getBoolean("success");

                    if (success) {

                        ProfileImageFileName = jsonObject.getString("imageFileName");

                        downloadFile_Profile();

                    } else {
                        // Loading Delay Handler
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                customAnimationDialog.dismiss();
                            }
                        } , 2000);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_ImageDownload myPage_imageDownload = new MyPage_ImageDownload(usernum, responseListener);
        RequestQueue queue22 = Volley.newRequestQueue(BoardRead_Activity.this);
        queue22.add(myPage_imageDownload);
    }

    private void checkUserId() {
        /**
         * UserNum 을 필요로하는 downloadFile()을 위해 Response 받아온다.
         */
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        UserNum     = jsonObject.getString("usernum");
                        UserName    = jsonObject.getString("name");
                        UserId      = jsonObject.getString("id");
                        UserBirth   = jsonObject.getString("age");
                        UserGender  = jsonObject.getString("gender");

                        usernum     = Integer.valueOf(UserNum);

                        downloadFile();

                        /**
                         * 사용자 프로필 이미지 확인
                         */
                        checkProfileImg();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_Request myPage_request = new MyPage_Request(userId, responseListener);
        RequestQueue queue22 = Volley.newRequestQueue(BoardRead_Activity.this);
        queue22.add(myPage_request);

    }

    public static Bitmap rotateImage(Bitmap img_bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(img_bitmap, 0, 0, img_bitmap.getWidth(), img_bitmap.getHeight(), matrix, true);
    }
    private void downloadFile(){

        // TODO: 파일을 업로드 하지 않았어도 알아서 회원 번호의 최신 파일을 가져와서 뿌려줘야한다...!
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
                    transferUtility.download(BUCKET, "BoardWrite_Image/" + UserNum + "/" + ImageFileName + ".jpg", localFile);

            downloadObserver.setTransferListener(new TransferListener() {

                // 다운로드 성공.
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {

                        // 복호화하기
                        Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                        iv_BoardRead_img    .setImageBitmap(bmp);
                        iv_BoardRead_img    .setScaleType(ImageView.ScaleType.FIT_XY);

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

        }   catch (IOException e) {
            e.printStackTrace();
            }
    }


    // TODO : 게시글을 올린 사용자의 아이디 UserID로 UserNum을 가져온다. -> 해당 UserNum으로 MySql 데이터 베이스에서 ImageFileName을 가져온다. -> AWS S3 Bucket에서 UserNum + ImageFileName으로 최신 프로필 이미지를 가져온다. 실시
    private void downloadFile_Profile(){

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
                    transferUtility.download(BUCKET, "MyPage_Image/" + UserNum + "/" + ProfileImageFileName + ".jpg", localFile);

            downloadObserver.setTransferListener(new TransferListener() {

                // 다운로드 성공.
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {

                        // 복호화하기
                        Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                        bmp = rotateImage(bmp, 90);

                        iv_BoardRead_ProfileImg .setImageBitmap(bmp);
                        iv_BoardRead_ProfileImg .setScaleType(ImageView.ScaleType.FIT_XY);

                        customAnimationDialog   .dismiss();

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

        }   catch (IOException e) {
            e.printStackTrace();
        }
    }

}
