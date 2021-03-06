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
                         * UpLoad ????????? ????????? ???????????? ????????? ????????? ?????????, ???????????? ?????? ???????????? ????????? ???????????????.
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

        // TODO : ?????? ????????? ???????????? ?????? ????????? userId??? ?????????.
        BoardRead_Request boardRead_request = new BoardRead_Request(Title, Content, responseListener);
        RequestQueue queue = Volley.newRequestQueue(BoardRead_Activity.this);
        queue.add(boardRead_request);

    }

    // TODO : userId??? ???????????? ?????? ???????????? ????????? ????????? ???????????? ????????? ???????????? ????????? ???????????? ?????????.
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
         * UserNum ??? ??????????????? downloadFile()??? ?????? Response ????????????.
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
                         * ????????? ????????? ????????? ??????
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

        // TODO: ????????? ????????? ?????? ???????????? ????????? ?????? ????????? ?????? ????????? ???????????? ??????????????????...!
        // fileUri??? ???????????? ??????, images??? ????????? ???????????? ?????? ????????? ???????????? localFile??? ????????????.
        final File localFile;
        try {
            localFile = File.createTempFile("images", ".jpg");

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            // ??????????????? ???????????? Bucket??? ??????. ????????? ??????????????? ????????? ????????????..
            TransferObserver downloadObserver =
                    transferUtility.download(BUCKET, "BoardWrite_Image/" + UserNum + "/" + ImageFileName + ".jpg", localFile);

            downloadObserver.setTransferListener(new TransferListener() {

                // ???????????? ??????.
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {

                        // ???????????????
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


    // TODO : ???????????? ?????? ???????????? ????????? UserID??? UserNum??? ????????????. -> ?????? UserNum?????? MySql ????????? ??????????????? ImageFileName??? ????????????. -> AWS S3 Bucket?????? UserNum + ImageFileName?????? ?????? ????????? ???????????? ????????????. ??????
    private void downloadFile_Profile(){

        // fileUri??? ???????????? ??????, images??? ????????? ???????????? ?????? ????????? ???????????? localFile??? ????????????.
        final File localFile;
        try {
            localFile = File.createTempFile("images", ".jpg");

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            // ??????????????? ???????????? Bucket??? ??????. ????????? ??????????????? ????????? ????????????..
            TransferObserver downloadObserver =
                    transferUtility.download(BUCKET, "MyPage_Image/" + UserNum + "/" + ProfileImageFileName + ".jpg", localFile);

            downloadObserver.setTransferListener(new TransferListener() {

                // ???????????? ??????.
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {

                        // ???????????????
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
