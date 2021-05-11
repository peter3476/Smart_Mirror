package com.example.smart_mirror.BOARD;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.smart_mirror.HOME.Board_Fragment;
import com.example.smart_mirror.MYPAGE.MyPage_Request;
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

// 게시판 글쓰기 Activity
public class BoardWrite_Activity extends AppCompatActivity {
    private static final String TAG = "SUNGJAE";

    private final String BUCKET = "";
    private final String KEY = "";
    private final String SECRET = "";

    private AmazonS3Client s3Client;
    private BasicAWSCredentials credentials;

    EditText et_Title, et_Content;

    Button completeBtn;

    ImageButton camera_btn;
    ImageView add_img;

    String Title, Content;

    private Boolean isPermission = true;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private File tempFile;

    String imageFileName, UserNum;

    Intent intent;
    String _id;

    int usernum;

    String storagePath;
    File storageDir;

    private Uri photoUri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeboard_write);

        intent = getIntent();
        _id = intent.getStringExtra("id");

        et_Title = (EditText) findViewById(R.id.et_Title);
        et_Content = (EditText) findViewById(R.id.et_Content);

        completeBtn = (Button) findViewById(R.id.completeBtn);
        add_img = (ImageView) findViewById(R.id.add_img);
        camera_btn = (ImageButton) findViewById(R.id.camera_btn);

        credentials = new BasicAWSCredentials(KEY, SECRET);
        s3Client = new AmazonS3Client(credentials);


//        tedPermission();

        // 사용자의 회원 번호 가져오기.
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        UserNum = jsonObject.getString("usernum");

                        usernum = Integer.valueOf(UserNum);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_Request myPage_request = new MyPage_Request(_id, responseListener);
        RequestQueue queue22 = Volley.newRequestQueue(BoardWrite_Activity.this);
        queue22.add(myPage_request);

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title = et_Title.getText().toString();
                Content = et_Content.getText().toString();

                if (Title.isEmpty() || Content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "제목 및 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (imageFileName != null) {
                    /**
                     * 업로드하는 이미지가 있다면, 제목 + 내용 + 이미지 파일명까지 DB에 담아준다.
                     */
                    // 작성된 글 서버를 통해 DB에 저장하기.
                    Response.Listener<String> responseListenerCompleteBtn = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    BoardWrite_Image_Request boardWrite_image_request = new BoardWrite_Image_Request(Title, Content, imageFileName, _id, responseListenerCompleteBtn);
                    RequestQueue queue = Volley.newRequestQueue(BoardWrite_Activity.this);
                    queue.add(boardWrite_image_request);

                    uploadFile();

                    Intent intent = new Intent(BoardWrite_Activity.this, Board_Fragment.class);

                    // 글쓰기 완료하고 글 목록 페이지에서 다시 글쓰기 버튼 누르면 이 전 글이 남아있어서 null로 설정해줌.
                    et_Title.setText(null);
                    et_Content.setText(null);
                    add_img.setImageDrawable(null);

                    startActivity(intent);

                } else {
                    /**
                     * 업로드하는 이미지가 없다면, 제목과 내용만 DB에 담아준다.
                     */
                    // 작성된 글 서버를 통해 DB에 저장하기.
                    Response.Listener<String> responseListenerCompleteBtn = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    BoardWrite_Request boardWirte_request = new BoardWrite_Request(Title, Content, _id, responseListenerCompleteBtn);
                    RequestQueue queue = Volley.newRequestQueue(BoardWrite_Activity.this);
                    queue.add(boardWirte_request);

//                    Intent intent = new Intent(BoardWrite_Activity.this, Board_Fragment.class);

                    // 글쓰기 완료하고 글 목록 페이지에서 다시 글쓰기 버튼 누르면 이 전 글이 남아있어서 null로 설정해줌.
                    et_Title.setText(null);
                    et_Content.setText(null);

//                    startActivity(intent);

                    finish();
                }
            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(BoardWrite_Activity.this);
                LayoutInflater dialogView = LayoutInflater.from(BoardWrite_Activity.this);

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
    }

    // 프레그 먼트로 이동
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_Fragment, fragment);
        fragmentTransaction.commit();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = UserNum + "_BoardWrite_" + timeStamp;

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

            // 이미지 파일 이름 ( namigation_{시간}_ )
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = UserNum + "_BoardWrite_" + timeStamp;

            add_img.setImageBitmap(bitmap);
            add_img.setScaleType(ImageView.ScaleType.FIT_XY);


        } else if (requestCode == PICK_FROM_CAMERA) {

            setImage();

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

//        originalBm = rotateImage(originalBm, 90);

        add_img.setImageBitmap(originalBm);
        add_img.setScaleType(ImageView.ScaleType.FIT_XY);


        /**
         *  tempFile 사용 후 null 처리를 해줘야한다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄진다.
         */
        tempFile = null;
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

    private void uploadFile() {

        if (photoUri != null) {

            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "/" + imageFileName);

            createFile(getApplicationContext(), photoUri, file);

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            // 업로드를 하기위해 Bucket에 접근. 이미지 확장자까지 붙이게 되어있넹..
            // BoardWirte_Image에 imageFileName.jpg의 이름으로 file 위치에 있는 이미지를 BUCKET에 넣어주겠다.
            TransferObserver uploadObserver =
                    transferUtility.upload(BUCKET, "BoardWrite_Image/" + UserNum + "/" + imageFileName + "." + getFileExtension(photoUri), file);

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

    /**
     * 파일 확장자 지정
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
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
