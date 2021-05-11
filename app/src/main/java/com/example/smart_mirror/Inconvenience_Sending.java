package com.example.smart_mirror;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_mirror.LOGIN.LoginActivity;

import java.io.IOException;
import java.util.ArrayList;

public class Inconvenience_Sending extends AppCompatActivity {

    ArrayAdapter inconveniece_adapter;

    String[] items;
    String incon_EditText;
    String Spinner_Text;

    Spinner inconvenience_Spinner;
    Button inconvenience_Btn;
    EditText inconvenience_Edtv;

    Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inconvenience_sms);

        inconvenience_Spinner   = (Spinner) findViewById(R.id.incon_spinner);
        inconvenience_Btn       = (Button) findViewById(R.id.Inconvenience_btn);
        inconvenience_Edtv      = (EditText) findViewById(R.id.Inconvenience_edtv);

        items = getResources().getStringArray(R.array.inconvenience_arr);

        inconveniece_adapter = new ArrayAdapter(Inconvenience_Sending.this, android.R.layout.simple_spinner_dropdown_item, items);

        inconvenience_Spinner.setAdapter(inconveniece_adapter);

        inconvenience_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner_Text = inconvenience_Spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LayoutInflater inflater = getLayoutInflater();
                View toastDesign    = inflater.inflate(R.layout.toast_design_text, (ViewGroup)findViewById(R.id.toast_design_root));

                TextView text       = toastDesign.findViewById(R.id.TextView_toast_design);

                text.setText("신고 종류를 선택해주세요.");
                toast               = new Toast(Inconvenience_Sending.this);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastDesign);
                toast.show();
            }
        });

        inconvenience_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incon_EditText = inconvenience_Edtv.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("01012345678",
                            null,
                            "* 불편 신고가 접수되었습니다 *\n\n" + "[ " + Spinner_Text + "] \n\n" + "\" " + incon_EditText + " \"",
                            null,
                            null);

                    Toast.makeText(Inconvenience_Sending.this, "", Toast.LENGTH_SHORT).show();
                    finish();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "전송 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    e   .printStackTrace();
                }
            }
        });
    }
}
