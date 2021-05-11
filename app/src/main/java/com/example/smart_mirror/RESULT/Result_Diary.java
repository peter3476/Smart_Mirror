package com.example.smart_mirror.RESULT;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.MYPAGE.MyPage_Request;
import com.example.smart_mirror.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Result_Diary extends AppCompatActivity {

    private static String IP_ADDRESS = "";
    private static String TAG = "phptest";

    private ArrayList<Result> arrayList;
    private List<String> list;
    private Result_Diary_Adapter result_diary_adapter;
    private RecyclerView recyclerView;
    private LinearLayout result_diary_linearlayout;
    private LinearLayoutManager linearLayoutManager;


    private String mJsonString;

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
        setContentView(R.layout.result_diary);

        intent = getIntent();
        intent_id = intent.getStringExtra("id");

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.result_diary_framelayout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_ResultDiary);
        result_diary_linearlayout = (LinearLayout) findViewById(R.id.result_diary_linearlayout);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        // view를 하나만 남겨두기 위하여 시작하면서 linearlayout은 지워둔다.
        frameLayout.removeView(result_diary_linearlayout);

        // LayoutManager를 방금 생성한 recyclerView에 설정해라.
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        result_diary_adapter = new Result_Diary_Adapter(arrayList);

        recyclerView.setAdapter(result_diary_adapter);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {

                        UserNum = jsonObject.getString("usernum");
                        UserName    = jsonObject.getString("name");
                        UserId      = jsonObject.getString("id");
                        UserBirth   = jsonObject.getString("age");
                        UserGender  = jsonObject.getString("gender");

                        Bring_Result();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyPage_Request  myPage_request   = new MyPage_Request(intent_id, responseListener);
        RequestQueue    queue            = Volley.newRequestQueue(Result_Diary.this);
        queue.add(myPage_request);

//
//        arrayList.clear();
//        result_diary_adapter.notifyDataSetChanged();


    }

    private void Bring_Result() {
        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response_Result) {

                String TAG_JSON         = "response";
                String TAG_High_Grade   = "high";       // 고위험
                String TAG_Middle_Grade = "middle";     // 위험
                String TAG_Low_Grade    = "low";        // 경고
                String TAG_Good_Grade   = "good";       // 좋음
                String TAG_Date         = "date";       // 진단 일시

                try {
                    JSONArray jsonArray = new JSONArray(response_Result);

                    // 사용자가 진단 기록이 없을 때, view 변경 (안내 문구)
                    if (jsonArray.length() == 0) {
                        changeView(0);
                    }

                    // 사용자가 진단한 기록이 있다면, recyclerview으로 변경
                    else {
                        changeView(1);
                    }


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject item = jsonArray.getJSONObject(i);

                        // DataBase에서 값을 받아옴
                        String High_Grade   = item.getString(TAG_High_Grade);
                        String Middle_Grade = item.getString(TAG_Middle_Grade);
                        String Low_Grade    = item.getString(TAG_Low_Grade);
                        String Good_Grade   = item.getString(TAG_Good_Grade);
                        String Check_Date   = item.getString(TAG_Date);

                        Result result = new Result();

                        // 받아온 값을 Result.java에 Setting 해줌
                        result.setHigh_Grade(High_Grade);
                        result.setMiddle_Grade(Middle_Grade);
                        result.setLow_Grade(Low_Grade);
                        result.setGood_Grade(Good_Grade);
                        result.setDate(Check_Date);

                        // 배열 리스트에 값들을 추가해줌.
                        arrayList.add(result);
                        result_diary_adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showResult : ", e);
                }
            }
        };
        Result_Hair_Request result_hair_request = new Result_Hair_Request(UserNum, responseListener2);
        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
        queue2.add(result_hair_request);
    }

    // 진단 기록 유&무를 통해 view 전환
    private void changeView(int result_num) {

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.result_diary_framelayout);

        // 위에 onCreate()에서 result_diary_linearlayout의 view를 지웠으므로 남아있는 view도 지우워서 아예 비워둔다.
        frameLayout.removeViewAt(0);

        switch (result_num) {

            case 0:     // 진단 기록이 없다면, 안내 문구 출력 layout으로 view 변경
                frameLayout.addView(result_diary_linearlayout);
                break;


            case 1:     // 진단 기록이 있다면, recyclerview로 변경하여 기록을 출력해준다.
                frameLayout.addView(recyclerView);
                break;
        }

    }
}

