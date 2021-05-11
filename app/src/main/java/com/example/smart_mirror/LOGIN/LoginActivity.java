package com.example.smart_mirror.LOGIN;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.HOME.Home_Activity;
import com.example.smart_mirror.REGISTER.RegisterActivity1;
import com.example.smart_mirror.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private final static int DATABASE_VERSION = 1;

    EditText idEditText;
    EditText pwEditText;
    Button btnLogin;
    Button btnSign;

    String _id, _pw;

    Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        idEditText = (EditText) findViewById(R.id.Login_ID);
        pwEditText = (EditText) findViewById(R.id.Login_PW);

        btnLogin = (Button) findViewById(R.id.Login_Btn);
        btnSign = (Button) findViewById(R.id.Signin_Btn);


        // Password 입력 값 안보이게 숨기기.
        pwEditText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        pwEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        btnSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //회원가입 버튼 클릭
                Intent intent = new Intent(getApplicationContext(), RegisterActivity1.class);
                startActivity(intent);
                //finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _id = idEditText.getText().toString();
                _pw = pwEditText.getText().toString();
                System.out.println("YES");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) { // 로그인에 성공한 경우 -> php 에서 mysql에 유효한 값만 가져오다 보니 따로 검사를 할 필요가 없는 듯?
                                String Userid = jsonObject.getString("id");
                                String Userpw = jsonObject.getString("pw");
                                String Username = jsonObject.getString("name");

                                if (Userid.length() != 0 || Userpw.length() != 0) {
                                    Intent intent = new Intent(LoginActivity.this, Home_Activity.class);
                                    intent.putExtra("id", Userid);
                                    intent.putExtra("pw", Userpw);

                                    startActivity(intent);

                                    finish();
                                } else {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View toastDesign = inflater.inflate(R.layout.toast_design_text, (ViewGroup)findViewById(R.id.toast_design_root));

                                    TextView text = toastDesign.findViewById(R.id.TextView_toast_design);
                                    text.setText("아이디 및 비밀번호를 입력해주세요.");
                                    toast               = new Toast(LoginActivity.this);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(toastDesign);
                                    toast.show();
                                }

                            } else { // 로그인에 실패한 경우
                                Toast.makeText(getApplicationContext(), "아이디 및 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Login_Request loginRequest = new Login_Request(_id, _pw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });


    }
}
