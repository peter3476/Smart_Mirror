package com.example.smart_mirror.HOME;

import android.Manifest;
import android.app.Activity;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.example.smart_mirror.CustomAnimationDialog;
import com.example.smart_mirror.MYPAGE.MyPage_ImageDownload;
import com.example.smart_mirror.MYPAGE.MyPage_ImageUpload;
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

public class MyPage_Fragment extends Fragment {

    ViewGroup viewGroup;

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
    TextView Modify_FirstName;

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

    private CustomAnimationDialog customAnimationDialog;

    private Boolean isPermission = true;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private File tempFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.mypage_modify, container, false);

        Modify_userNum      = (TextView) viewGroup.findViewById(R.id.modify_usernum);
        Modify_name         = (TextView) viewGroup.findViewById(R.id.modify_name);
        Modify_id           = (TextView) viewGroup.findViewById(R.id.modify_id);
        Modify_birth        = (TextView) viewGroup.findViewById(R.id.modify_birth);
        Modify_gender       = (TextView) viewGroup.findViewById(R.id.modify_gender);
        Modify_FirstName    = (TextView) viewGroup.findViewById(R.id.modify_FirstName);

        credentials = new BasicAWSCredentials(KEY, SECRET);
        s3Client    = new AmazonS3Client(credentials);

        iv_Profile  = (ImageView) viewGroup.findViewById(R.id.iv_Profile);

        _id = getArguments().getString("id");

        tedPermission();


        customAnimationDialog = new CustomAnimationDialog(getActivity());
        customAnimationDialog.show();
//        progressDialog = ProgressDialog.show(getActivity(),"????????? ??????????????????...", null, true, true);

        iv_Profile.setBackground(new ShapeDrawable(new OvalShape()));
        iv_Profile.setClipToOutline(true);


        // ????????? ?????? ?????? ?????? ???
        iv_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg     = new AlertDialog.Builder(getActivity());
                LayoutInflater dialogView   = LayoutInflater.from(getActivity());

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
                                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isPermission)
                                    takePhoto();
                                else
                                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_SHORT).show();
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

                        Modify_userNum      .setText(UserNum);
                        Modify_name         .setText(UserName);
                        Modify_id           .setText(UserId);
                        Modify_birth        .setText(UserBirth);
                        Modify_gender       .setText(UserGender);
                        Modify_FirstName    .setText(UserName + " ???");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_Request myPage_request = new MyPage_Request(_id, responseListener);
        RequestQueue queue22 = Volley.newRequestQueue(getActivity());
        queue22.add(myPage_request);

        return viewGroup;
    }


    private void checkImageFile() {
        // TODO : MySql?????? ?????? ???????????? ???????????? success == true?????? ?????? ???????????? ?????????
        //  AWS S3 BUCKET??? ???????????? ???????????? ????????? ????????? ???????????????.
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");


                    // MySql?????? ????????? ?????? ???????????? ????????? imageFileName?????? ??????????????? ?????? ?????? ???????????? ????????????.
                    // imageFileName??? MySql?????? ???????????? ?????? ?????????, ???????????? ?????? ???????????????.
                    if (success) {
                        imageFileName = jsonObject.getString("imageFileName");

                        downloadFile();

                    } else {
                        // Loading Delay Handler
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                customAnimationDialog.dismiss();
                            }} , 2000);

                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_ImageDownload myPage_imageDownload = new MyPage_ImageDownload(usernum, responseListener);
        RequestQueue queue11 = Volley.newRequestQueue(getActivity());
        queue11.add(myPage_imageDownload);
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
            Toast.makeText(getActivity(), "????????? ?????? ??????! ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(MyPage_Fragment.this).commit();
            fragmentManager.popBackStack();
            e.printStackTrace();
        }
        if (tempFile != null) {

            // ????????? ???????????? ???????????????, uri??? ????????????.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                // authority : ????????? ??????????????? ???????????? ???????????? ??????
                photoUri = FileProvider.getUriForFile(getActivity(),
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
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        imageFileName = UserNum + "_Mypage_" + timeStamp;

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


    public static Bitmap rotateImage(Bitmap img_bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(img_bitmap, 0, 0, img_bitmap.getWidth(), img_bitmap.getHeight(), matrix, true);
    }


    private void uploadFile() {

        if (photoUri != null) {

            // ????????? ?????? ?????? ( namigation_{??????}_ )
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = UserNum + "_Mypage_" + timeStamp;

            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "/" + imageFileName);

            createFile(getActivity().getApplicationContext(), photoUri, file);

            /**
             * MySql?????? ????????? ????????? ?????????
             */
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            // ???????????? ??? ??? ?????? ????????? ??????????????? ??????.
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            MyPage_ImageUpload mypage_imageUpload = new MyPage_ImageUpload(usernum, imageFileName, responseListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(mypage_imageUpload);


            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getActivity().getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(s3Client)
                            .build();

            // ???????????? ???????????? Bucket??? ??????. ????????? ??????????????? ????????? ????????????..
            // MyPage_Image??? imageFileName.jpg??? ???????????? file ????????? ?????? ???????????? BUCKET??? ???????????????.
            TransferObserver uploadObserver =
                    transferUtility.upload(BUCKET, "MyPage_Image/" + UserNum + "/" + imageFileName + "." + getFileExtension(photoUri), file);

            uploadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getActivity().getApplicationContext(), "Upload Completed!", Toast.LENGTH_SHORT).show();

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

    private void downloadFile() throws IOException {

        // fileUri??? ??????????????? ????????? ?????????, ????????? showChoosingFile()?????? fileUri??? ?????? ?????? ????????? ??? ??????.
        if (photoUri != null) {

            // TODO: ?????? Smart_Mirror?????? ????????? ????????? ???????????????, ?????? ??????????????? ????????? ???????????? ???????????? ?????? ??? ???????????????. ??????, ??????????????? ???????????? ??????????????? ????????? ????????? ??????????????????.

            try {
                // fileUri??? ???????????? ??????, images??? ????????? ???????????? ?????? ????????? ???????????? localFile??? ????????????.
                final File localFile = File.createTempFile("images", getFileExtension(photoUri));

                TransferUtility transferUtility =
                        TransferUtility.builder()
                                .context(getActivity().getApplicationContext())
                                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                                .s3Client(s3Client)
                                .build();

                // ??????????????? ???????????? Bucket??? ??????. ????????? ??????????????? ????????? ????????????..
                TransferObserver downloadObserver =
                        transferUtility.download(BUCKET, "MyPage_Image/" + UserNum + "/" + imageFileName+ "." + getFileExtension(photoUri), localFile);

                downloadObserver.setTransferListener(new TransferListener() {

                    // ???????????? ??????.
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            Toast.makeText(getActivity().getApplicationContext(), "Download Completed!", Toast.LENGTH_SHORT).show();

                            // ???????????????
                            Bitmap bmp  = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                            bmp         = rotateImage(bmp, 90);

                            iv_Profile  .setImageBitmap(bmp);
                            iv_Profile  .setScaleType(ImageView.ScaleType.FIT_XY);


                            customAnimationDialog.dismiss();

                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef  = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone     = (int) percentDonef;

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

            // TODO: ????????? ????????? ?????? ???????????? ????????? ?????? ????????? ?????? ????????? ???????????? ??????????????????...!
            // fileUri??? ???????????? ??????, images??? ????????? ???????????? ?????? ????????? ???????????? localFile??? ????????????.
            final File localFile;
            try {
                localFile = File.createTempFile("images", ".jpg");

                TransferUtility transferUtility =
                        TransferUtility.builder()
                                .context(getActivity().getApplicationContext())
                                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                                .s3Client(s3Client)
                                .build();

                // ??????????????? ???????????? Bucket??? ??????. ????????? ??????????????? ????????? ????????????..
                TransferObserver downloadObserver =
                        transferUtility.download(BUCKET, "MyPage_Image/" + UserNum + "/" + imageFileName+ ".jpg", localFile);



                downloadObserver.setTransferListener(new TransferListener() {

                    // ???????????? ??????.
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            Toast.makeText(getActivity().getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();


                            // ???????????????
                            Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                            bmp = rotateImage(bmp, 90);

                            iv_Profile.setImageBitmap(bmp);
                            iv_Profile.setScaleType(ImageView.ScaleType.FIT_XY);

                            customAnimationDialog.dismiss();

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



    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (bitmap != null) {
            bitmap.recycle();
        }

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "?????? ???????????????.", Toast.LENGTH_SHORT).show();

            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            bitmap = rotateImage(bitmap, 90);

            iv_Profile.setImageBitmap(bitmap);
            iv_Profile.setScaleType(ImageView.ScaleType.FIT_XY);

            uploadFile();

        } else if (requestCode == PICK_FROM_CAMERA) {

            setImage();

        }
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

        originalBm = rotateImage(originalBm, 90);

        iv_Profile.setImageBitmap(originalBm);
        iv_Profile.setScaleType(ImageView.ScaleType.FIT_XY);

        uploadFile();

        /**
         *  tempFile ?????? ??? null ????????? ???????????????.
         *  (resultCode != RESULT_OK) ??? ??? tempFile ??? ???????????? ?????????
         *  ????????? ???????????? ?????? ?????? ?????? ?????? ?????? ????????? ????????????.
         */
        tempFile = null;
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
     * ?????? ????????? ??????
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
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

        TedPermission.with(getActivity())
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

}