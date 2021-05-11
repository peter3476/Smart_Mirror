package com.example.smart_mirror.REGISTER;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class RegisterActivity1 extends AppCompatActivity {

    int userNum;
    Random random = new Random();
    int maxNum = 9999;
    int minNum = 1000;

    EditText nameEditText, idEditText, pwEditText, CKpwEditText;

    View dialogView;

    Button CKid_Btn, Register_Next_Btn;

    static String name, id, pw, CKpw, gender, age;


    //  php Query문에 필요한 것들
    private final static int DATABASE_VERSION = 1;

    private static String TAG = "RegisterPHP";


    private static final String TAG_JSON = "result_MYSQL";
    private static final String TAG_USERNUM = "usernum";
    private static final String TAG_ID = "id";
    private static final String TAG_PW = "pw";
    private static final String TAG_NAME = "name";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_AGE = "age";

    Boolean CKid_Success = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        nameEditText = (EditText) findViewById(R.id.Sign_name);
        idEditText = (EditText) findViewById(R.id.Sign_id);
        pwEditText = (EditText) findViewById(R.id.Sign_pw);
        CKpwEditText = (EditText) findViewById(R.id.Sign_CKpw);       //비밀번호 일치 확인


        // 비밀번호 안보이게 숨김
        pwEditText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        pwEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        CKpwEditText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        CKpwEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Register_Next_Btn = (Button) findViewById(R.id.Register_Next_Btn);
        CKid_Btn = (Button) findViewById(R.id.Sign_CKid);

        CKid_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListenerCKid = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            // success가 true로 반환되어 왔다는 것은 이미 아이디가 존재하고 있다는 말이다.
                            if (success) {
                                Toast.makeText(getApplicationContext(), "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                CKid_Success = true;
                                Toast.makeText(getApplicationContext(), "중복확인이 되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                id = idEditText.getText().toString();

                Register_Request_CKid registerRequest = new Register_Request_CKid(id, responseListenerCKid);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity1.this);
                queue.add(registerRequest);
            }
        });

        Register_Next_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                pw = pwEditText.getText().toString();
                CKpw = CKpwEditText.getText().toString();
                id = idEditText.getText().toString();

                if(id.equals("") || pw.equals("") || name.equals("") || CKpw.equals("")) {
                    dialogView = (View) View.inflate(RegisterActivity1.this, R.layout.alertdialog_activity, null);

                    AlertDialog.Builder dlg = new AlertDialog.Builder(RegisterActivity1.this);
                    dlg.setTitle("WARNING\n").setMessage("입력 칸을 모두 입력하세요!");
                    dlg.setView(dialogView);
                    dlg.setPositiveButton("확인", null);

                    AlertDialog alertDialog = dlg.create();
                    alertDialog.show();

                } else if (!pw.equals(CKpw)){
                    Toast.makeText(getApplicationContext(), "비밀번호를 동일하게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 모든 칸이 채워지고,
                else {
                    // 만약 중복 확인이 되었다면!
                    if (CKid_Success) {
                        Intent intent = new Intent(RegisterActivity1.this, RegisterActivity2.class);
                        intent.putExtra("name_Intent", name);
                        intent.putExtra("id_Intent", id);
                        intent.putExtra("pw_Intent", pw);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
