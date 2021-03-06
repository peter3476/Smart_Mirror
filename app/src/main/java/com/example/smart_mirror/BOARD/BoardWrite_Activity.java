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

// ????????? ????????? Activity
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

        // ???????????? ?????? ?????? ????????????.
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
                    Toast.makeText(getApplicationContext(), "?????? ??? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                } else if (imageFileName != null) {
                    /**
                     * ??????????????? ???????????? ?????????, ?????? + ?????? + ????????? ??????????????? DB??? ????????????.
                     */
                    // ????????? ??? ????????? ?????? DB??? ????????????.
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

                    // ????????? ???????????? ??? ?????? ??????????????? ?????? ????????? ?????? ????????? ??? ??? ?????? ??????????????? null??? ????????????.
                    et_Title.setText(null);
                    et_Content.setText(null);
                    add_img.setImageDrawable(null);

                    startActivity(intent);

                } else {
                    /**
                     * ??????????????? ???????????? ?????????, ????????? ????????? DB??? ????????????.
                     */
                    // ????????? ??? ????????? ?????? DB??? ????????????.
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

                    // ????????? ???????????? ??? ?????? ??????????????? ?????? ????????? ?????? ????????? ??? ??? ?????? ??????????????? null??? ????????????.
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

                Log.d("test", "??? ??????!");

                dlg.setTitle("??????????????????.\n")
                        .setIcon(R.drawable.camera_image)
                        .setMessage("????????? / ?????????")
                        .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isPermission)
                                    goToAlbum();
                                else
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
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

    // ????????? ????????? ??????
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_Fragment, fragment);
        fragmentTransaction.commit();
    }

    /**
     *  ???????????? ????????? ????????????
     */
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    /**
     *  ??????????????? ????????? ????????????
     */
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            // ????????? ???????????? ??????
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "????????? ?????? ??????! ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            // ????????? ???????????? ???????????????, uri??? ????????????.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                // authority : ????????? ??????????????? ???????????? ???????????? ??????
                photoUri = FileProvider.getUriForFile(this,
                        "{com.example.smart_mirror}.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                photoUri = Uri.fromFile(tempFile);

                // MediaStore.EXTRA_OUTPUT?????? uri??? ??????
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    /**
     *  ?????? ??? ?????? ?????????
     */
    private File createImageFile() throws IOException {

        // ????????? ?????? ?????? ( namigation_{??????}_ )
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = UserNum + "_BoardWrite_" + timeStamp;

        // ???????????? ????????? ?????? ?????? ( namigation )
        // Android 10?????? /sdcard??? ????????? ??? ?????? ?????????. Deprecated ?????????
        storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/namigation/");
        storagePath = String.valueOf(storageDir);

        if (!storageDir.exists())
        {
            storageDir.mkdirs();
        }

        // ?????? ??????
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
            Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();

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

            // ????????? ?????? ?????? ( namigation_{??????}_ )
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
     *  tempFile ??? bitmap ?????? ?????? ??? ImageView ??? ????????????.
     */
    private void setImage() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        BitmapToString(originalBm);

        // TODO : takePhoto??? ??? storagePath??? ????????? ?????? ?????????

        storagePath = tempFile.getAbsolutePath();

//        originalBm = rotateImage(originalBm, 90);

        add_img.setImageBitmap(originalBm);
        add_img.setScaleType(ImageView.ScaleType.FIT_XY);


        /**
         *  tempFile ?????? ??? null ????????? ???????????????.
         *  (resultCode != RESULT_OK) ??? ??? tempFile ??? ???????????? ?????????
         *  ????????? ???????????? ?????? ?????? ?????? ?????? ?????? ????????? ????????????.
         */
        tempFile = null;
    }

    /**
     *  upload / download ??? ?????? ???????????? ????????????.
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

            // ???????????? ???????????? Bucket??? ??????. ????????? ??????????????? ????????? ????????????..
            // BoardWirte_Image??? imageFileName.jpg??? ???????????? file ????????? ?????? ???????????? BUCKET??? ???????????????.
            TransferObserver uploadObserver =
                    transferUtility.upload(BUCKET, "BoardWrite_Image/" + UserNum + "/" + imageFileName + "." + getFileExtension(photoUri), file);

            uploadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "Upload Completed!", Toast.LENGTH_SHORT).show();

                        // ????????? ????????? ????????? ?????? ????????? ?????? ???????????? ??? ?????? ???????????? ????????????.
                        file.delete();
                    } else if (TransferState.FAILED == state) {
                        file.delete();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    // ????????? ?????? ????????? ??? ????????????..
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
     * ?????? ????????? ??????
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    /**
     * Image??? Encoding ?????? ??????
     */
    public void BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // bitmap compress
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] arr = baos.toByteArray();
        String image = Base64.encodeToString(arr, Base64.DEFAULT);
        String temp = "";

        try {
            // UTF-8??? ??? ??? ??? ??????????????????
            temp = "&imagedevice = " + URLEncoder.encode(image, "utf-8");

        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }


    /**
     *  ?????? ??????
     */
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // ?????? ?????? ??????
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ?????? ?????? ??????
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
