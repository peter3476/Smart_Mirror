package com.example.smart_mirror.HOME;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.MYPAGE.MyPage_Request;
import com.example.smart_mirror.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateQR extends AppCompatActivity {
    ImageView QR_Img;

    Button Cancel_Btn;

    Intent intent;
    String intent_id;
    String UserNum;
    String UserName;
    String UserId;
    String UserBirth;
    String UserGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.createqrcode);

        QR_Img      = (ImageView) findViewById(R.id.qrcode);
        Cancel_Btn  = (Button) findViewById(R.id.QR_Cancel_Btn);

        intent      = getIntent();
        intent_id   = intent.getStringExtra("id");

        // Activity 종료 버튼
        Cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 회원번호 가져오기
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

                        CreateQRcode(UserNum);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_Request myPage_request   = new MyPage_Request(intent_id, responseListener);
        RequestQueue queue              = Volley.newRequestQueue(CreateQR.this);
        queue.add(myPage_request);
    }

    // 회원번호의 정보가 담긴 QR 코드 생성 후 ImageView에 설정.
    public void CreateQRcode(String userNum) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix             = multiFormatWriter.encode(userNum, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder   = new BarcodeEncoder();
            Bitmap bitmap                   = barcodeEncoder.createBitmap(bitMatrix);
            QR_Img.setImageBitmap(bitmap);

        }catch (Exception e){

        }
    }
}
