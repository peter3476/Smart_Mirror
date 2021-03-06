package com.example.smart_mirror.REGISTER;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.LOGIN.LoginActivity;
import com.example.smart_mirror.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

public class RegisterActivity2 extends AppCompatActivity {

    Button Register_Success_Btn;

    RadioButton female, male;
    RadioGroup sexRG;

    DatePicker DP_age;

    View dialogView;

    private int Year, Month, Day;

    String name, id, pw, gender, age;

    int userNum, RB_selected;
    Random random = new Random();
    int maxNum = 9999;
    int minNum = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity2);

        female = (RadioButton) findViewById(R.id.femaleBtn);
        male = (RadioButton) findViewById(R.id.maleBtn);
        sexRG = (RadioGroup) findViewById(R.id.sexRG);

        Year = Calendar.getInstance().get(Calendar.YEAR);
        Month = Calendar.getInstance().get(Calendar.MONTH);
        Day= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        DP_age = (DatePicker) findViewById(R.id.DPage);
        DP_age.init(Year, Month, Day, new DatePicker.OnDateChangedListener(){
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                age = Integer.toString(year) + "-" + Integer.toString((monthOfYear + 1)) + "-" + Integer.toString(dayOfMonth);
            }
        });

        Register_Success_Btn = (Button) findViewById(R.id.Register_Success_Btn);

        // RegisterActivity1?????? ????????? ??? ????????????.
        name = getIntent().getStringExtra("name_Intent");
        id = getIntent().getStringExtra("id_Intent");
        pw = getIntent().getStringExtra("pw_Intent");



        Register_Success_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RB_selected = sexRG.getCheckedRadioButtonId();

                if (RB_selected == -1) {
                    Toast.makeText(getApplicationContext(), "????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                else {

                    final RadioButton rb = (RadioButton) findViewById(RB_selected);
                    gender = rb.getText().toString();

                    userNum = random.nextInt(maxNum - minNum + 1) + minNum;
                    Response.Listener<String> responseListenerSuccessBtn = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) { // ??????????????? ????????? ??????
                                    dialogView = (View) View.inflate(RegisterActivity2.this, R.layout.alertdialog_activity, null);

                                    AlertDialog.Builder dlg = new AlertDialog.Builder(RegisterActivity2.this);
                                    dlg.setTitle(name + "??? ???????????????.\n").setMessage("???????????? ??????????????? " + userNum + "?????????.");
                                    dlg.setView(dialogView);

                                    // alertDialog ?????? ?????? ????????? ?????? ????????? ??????
                                    dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(RegisterActivity2.this, "????????? ?????????????????????. ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity2.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    AlertDialog alertDialog = dlg.create();
                                    alertDialog.show();

                                } else { // ??????????????? ????????? ??????
                                    Toast.makeText(getApplicationContext(),"?????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Register_Request2 registerRequest2 = new Register_Request2(userNum, name, id, pw, gender, age, responseListenerSuccessBtn);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity2.this);
                    queue.add(registerRequest2);
                }
            }
        });
    }
}
