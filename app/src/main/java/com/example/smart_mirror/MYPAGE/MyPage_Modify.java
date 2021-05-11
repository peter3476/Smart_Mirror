package com.example.smart_mirror.MYPAGE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.util.IOUtils;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.BOARD.FreeBoard_Activity;
import com.example.smart_mirror.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MyPage_Modify extends AppCompatActivity {

    private static final String TAG = "SUNGJAE";

    private final String BUCKET = "";
    private final String KEY = "";
    private final String SECRET = "";

    private AmazonS3Client s3Client;
    private BasicAWSCredentials credentials;

    private Uri photoUri;
    private Bitmap bitmap;

    TextView Modify_userNum;
    TextView Modify_name;
    TextView Modify_id;
    TextView Modify_birth;
    TextView Modify_gender;
    TextView Modify_FirstID;

    String UserNum;
    String UserName;
    String UserId;
    String UserBirth;
    String UserGender;

    String imageFileName;

    ImageView iv_Profile;

    Intent intent;

    String _id;
    int usernum;

    String storagePath;
    File storageDir;

    ProgressDialog progressDialog;

    private Boolean isPermission = true;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private File tempFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_modify);

        Modify_userNum = (TextView) findViewById(R.id.modify_usernum);
        Modify_name = (TextView) findViewById(R.id.modify_name);
        Modify_id = (TextView) findViewById(R.id.modify_id);
        Modify_birth = (TextView) findViewById(R.id.modify_birth);
        Modify_gender = (TextView) findViewById(R.id.modify_gender);
        Modify_FirstID = (TextView) findViewById(R.id.modify_FirstName);

        credentials = new BasicAWSCredentials(KEY, SECRET);
        s3Client = new AmazonS3Client(credentials);

        iv_Profile = (ImageView) findViewById(R.id.iv_Profile);

        intent = getIntent();
        _id = intent.getStringExtra("id");

        tedPermission();

        iv_Profile.setBackground(new ShapeDrawable(new OvalShape()));
        iv_Profile.setClipToOutline(true);


        // 프로필 수정 버튼 클릭 시
        iv_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MyPage_Modify.this);
                LayoutInflater dialogView = LayoutInflater.from(MyPage_Modify.this);

                final View view = dialogView.inflate(R.layout.alertdialog_activity, null);

                dlg.setView(view);

                Log.d("test", "잘 눌림!");

                dlg.setTitle("선택해주세요.\n")
                        .setIcon(R.drawable.camera_image)
                        .setMessage("카메라 / 갤러리")
                        .setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isPermission)
                                    goToAlbum();
                                else
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("카메라", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isPermission)
                                    takePhoto();
                                else
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_SHORT).show();
                            }
                        });

                dlg.show();
            }
        });

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

                        checkImageFile();

                        Modify_userNum  .setText(UserNum);
                        Modify_name     .setText(UserName);
                        Modify_id       .setText(UserId);
                        Modify_birth    .setText(UserBirth);
                        Modify_gender   .setText(UserGender);
                        Modify_FirstID  .setText(UserId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_Request myPage_request   = new MyPage_Request(_id, responseListener);
        RequestQueue queue22            = Volley.newRequestQueue(MyPage_Modify.this);
        queue22.add(myPage_request);

    }

    private void checkImageFile() {
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

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_ImageDownload myPage_imageDownload = new MyPage_ImageDownload(usernum, responseListener);
        RequestQueue queue11 = Volley.newRequestQueue(MyPage_Modify.this);
        queue11.add(myPage_imageDownload);
    }

    /**
     *  앨범에서 이미지 가져오기
     */
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            // 이미지 파일명이 담김
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            // 이미지 파일명이 존재한다면, uri를 생성한다.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                // authority : 콘텐츠 제공자에서 제공되는 데이터를 식별
                photoUri = FileProvider.getUriForFile(this,
                        "{com.example.smart_mirror}.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                photoUri = Uri.fromFile(tempFile);

                // MediaStore.EXTRA_OUTPUT으로 uri를 지정
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    /**
     *  폴더 및 파일 만들기
     */
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( namigation_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        imageFileName = UserNum + "_Mypage_" + timeStamp;

        // 이미지가 저장될 폴더 공간 ( namigation )
        // Android 10부터 /sdcard에 접근할 수 없게 되었다. Deprecated 되어서
        storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/namigation/");
        storagePath = String.valueOf(storageDir);

        if (!storageDir.exists())
        {
            storageDir.mkdirs();
        }

        // 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());

        return image;
    }


    /**
     *  upload / download 할 파일 형식으로 만들어줌.
     */
    private void createFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void uploadFile() {

        if (photoUri != null) {

            // 이미지 파일 이름 ( namigation_{시간}_ )
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = UserNum + "_Mypage_" + timeStamp;

            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "/" + imageFileName);

            createFile(getApplicationContext(), photoUri, file);

            /**
             * MySql에도 동시에 파일명 올리기
             */
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            // 성공하고 난 후 코드 작성이 필요하다면 작성.
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            MyPage_ImageUpload mypage_imageUpload = new MyPage_ImageUpload(usernum, imageFileName, responseListener);
            RequestQueue queue = Volley.newRequestQueue(MyPage_Modify.this);
            queue.add(mypage_imageUpload);


            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            // 업로드를 하기위해 Bucket에 접근. 이미지 확장자까지 붙이게 되어있넹..
            // MyPage_Image에 imageFileName.jpg의 이름으로 file 위치에 있는 이미지를 BUCKET에 넣어주겠다.
            TransferObserver uploadObserver =
                    transferUtility.upload(BUCKET, "MyPage_Image/" + UserNum + "/" + imageFileName + "." + getFileExtension(photoUri), file);

            uploadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "Upload Completed!", Toast.LENGTH_SHORT).show();

                        // 파일에 임시로 이름을 주어 설정한 것은 업로드할 때 잠시 사용하고 삭제한다.
                        file.delete();
                    } else if (TransferState.FAILED == state) {
                        file.delete();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    // 이미지 크기 지정??? 잘 모르겠음..
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;

                }

                @Override
                public void onError(int id, Exception ex) {
                    ex.printStackTrace();
                }

            });
        }
    }

    private void downloadFile() throws IOException {

        // fileUri가 비어있으면 안되기 때문에, 반드시 showChoosingFile()에서 fileUri를 채운 후에 접근할 수 있다.
        if (photoUri != null) {

            // TODO: 나는 Smart_Mirror에서 알아서 게시글 번호라던지, 회원 번호라던지 등등을 이용하여 이미지를 빼올 수 있어야한다. 또한, 여기서처럼 이미지를 업로드하는 절차가 없어도 가능해야한다.

            try {
                // fileUri에 확장자가 붙고, images가 들어간 이미지를 임시 파일로 생성하여 localFile에 담아준다.
                final File localFile = File.createTempFile("images", getFileExtension(photoUri));

                TransferUtility transferUtility =
                        TransferUtility.builder()
                                .context(getApplicationContext())
                                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                                .s3Client(s3Client)
                                .build();

                // 다운로드를 하기위해 Bucket에 접근. 이미지 확장자까지 붙이게 되어있넹..
                TransferObserver downloadObserver =
                        transferUtility.download(BUCKET, "MyPage_Image/" + UserNum + "/" + imageFileName+ "." + getFileExtension(photoUri), localFile);

                downloadObserver.setTransferListener(new TransferListener() {

                    // 다운로드 성공.
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            Toast.makeText(getApplicationContext(), "Download Completed!", Toast.LENGTH_SHORT).show();

                            // 복호화하기
                            Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            iv_Profile.setImageBitmap(bmp);
                            iv_Profile.setScaleType(ImageView.ScaleType.FIT_XY);


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
        } else {

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
                        transferUtility.download(BUCKET, "MyPage_Image/" + UserNum + "/" + imageFileName+ ".jpg", localFile);

                downloadObserver.setTransferListener(new TransferListener() {

                    // 다운로드 성공.
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();


                            // 복호화하기
                            Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            iv_Profile.setImageBitmap(bmp);
                            iv_Profile.setScaleType(ImageView.ScaleType.FIT_XY);

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



    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (bitmap != null) {
            bitmap.recycle();
        }

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            iv_Profile.setImageBitmap(bitmap);
            iv_Profile.setScaleType(ImageView.ScaleType.FIT_XY);

            uploadFile();

        } else if (requestCode == PICK_FROM_CAMERA) {

            setImage();

        }
    }


    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private void setImage() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        BitmapToString(originalBm);

        // TODO : takePhoto할 때 storagePath를 정해줄 것이 아니라

        storagePath = tempFile.getAbsolutePath();

        originalBm = rotateImage(originalBm, 90);

        iv_Profile.setImageBitmap(originalBm);
        iv_Profile.setScaleType(ImageView.ScaleType.FIT_XY);

        uploadFile();

        /**
         *  tempFile 사용 후 null 처리를 해줘야한다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄진다.
         */
        tempFile = null;
    }


    /**
     * Image를 Encoding 하는 코드
     */
    public void BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // bitmap compress
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] arr = baos.toByteArray();
        String image = Base64.encodeToString(arr, Base64.DEFAULT);
        String temp = "";

        try {
            // UTF-8로 한 번 더 인코딩해주기
            temp = "&imagedevice = " + URLEncoder.encode(image, "utf-8");

        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }


    /**
     * 파일 확장자 지정
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /**
     *  권한 설정
     */
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

}