package com.example.smart_mirror.BOARD;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.smart_mirror.CustomAnimationDialog;
import com.example.smart_mirror.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FreeBoard_Activity extends AppCompatActivity{

    private static String IP_ADDRESS = "";
    private static String TAG = "phptest";

    Button freeBoard_writeBtn;

    private ArrayList<Board> arrayList;
    private List<String> list;
    private FreeBoard_Adapter freeBoard_adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AutoCompleteTextView autoCompleteTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView search_Btn;

    private CustomAnimationDialog customAnimationDialog;

    private String mJsonString;
    private String auto_String;
    private String string_auto;

    Intent intent;
    String intent_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeboard_activity);

        intent = getIntent();
        intent_id = intent.getStringExtra("id");

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.tv_auto);
        list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_FreeBoard);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);     // LayoutManager를 방금 생성한 recyclerView에 설정해라.

        arrayList = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                freeBoard_adapter = new FreeBoard_Adapter(arrayList);

                recyclerView.setAdapter(freeBoard_adapter);

                arrayList.clear();
                freeBoard_adapter.notifyDataSetChanged();

                GetData task = new GetData();
                task.execute( "http://" + IP_ADDRESS + "/FreeBoardList.php", "");

                // 새로고침 완료
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        // 작성한 FreeBoard_Adapter를 가져와서 FreeBoard_Adapter에 arrayList를 담아줄 것이다.
        freeBoard_adapter = new FreeBoard_Adapter(arrayList);

        // 담아져 있는 mainAdapter를 recyclerView에 설정해주어라.
        recyclerView.setAdapter(freeBoard_adapter);

        arrayList.clear();
        freeBoard_adapter.notifyDataSetChanged();

        GetData task = new GetData();

        task.execute( "http://" + IP_ADDRESS + "/FreeBoardList.php", "");

        ArrayAdapter<String> searchList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        autoCompleteTextView.setAdapter(searchList);

        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    auto_String = autoCompleteTextView.getText().toString().trim().replace(" ", "");
                    string_auto = autoCompleteTextView.getText().toString();
                    auto_String.toLowerCase();

                    if (auto_String.length() == 0 || auto_String.equals(" ")) {
                        Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (!list.toString().trim().replace(" ", "").contains(auto_String)) {
                        Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        String UserId = jsonObject.getString("id");
                                        String Search_Title = jsonObject.getString("Title");
                                        String Search_Content = jsonObject.getString("Content");

                                        Intent intent = new Intent(FreeBoard_Activity.this, BoardRead_Activity.class);
                                        intent.putExtra("TITLE", Search_Title);
                                        intent.putExtra("CONTENT", Search_Content);
                                        intent.putExtra("id", intent_id);

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                        autoCompleteTextView.setText(null);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        FreeBoard_AutoComplete_Request freeBoard_autoComplete_request = new FreeBoard_AutoComplete_Request(string_auto, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(FreeBoard_Activity.this);
                        queue.add(freeBoard_autoComplete_request);
                    }
                    return true;
                }
                return false;
            }
        });

        search_Btn = (ImageView) findViewById(R.id.search_Btn);
        search_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_String = autoCompleteTextView.getText().toString().trim().replace(" ", "");
                string_auto = autoCompleteTextView.getText().toString();
                autoCompleteTextView.setText(null);

                if (auto_String.equals("") || auto_String.equals(" ")) {
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!list.toString().trim().replace(" ", "").contains(auto_String)) {
                    Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {
                                    String UserId = jsonObject.getString("id");
                                    String Search_Title = jsonObject.getString("Title");
                                    String Search_Content = jsonObject.getString("Content");

                                    Intent intent = new Intent(FreeBoard_Activity.this, BoardRead_Activity.class);
                                    intent.putExtra("TITLE", Search_Title);
                                    intent.putExtra("CONTENT", Search_Content);
                                    intent.putExtra("id", intent_id);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    FreeBoard_AutoComplete_Request freeBoard_autoComplete_request = new FreeBoard_AutoComplete_Request(string_auto, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FreeBoard_Activity.this);
                    queue.add(freeBoard_autoComplete_request);
                }
            }
        });


        freeBoard_writeBtn = (Button) findViewById(R.id.freeBoard_writeBtn);
        freeBoard_writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeBoard_Activity.this, BoardWrite_Activity.class);
                intent.putExtra("id", intent_id);
                startActivity(intent);
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            customAnimationDialog = new CustomAnimationDialog(FreeBoard_Activity.this);
            customAnimationDialog.show();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            customAnimationDialog.dismiss();
            mJsonString = result;
            showResult();
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="response";
        String TAG_ID = "id";
        String TAG_Title = "Title";
        String TAG_Content ="Content";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String Title = item.getString(TAG_Title);
                String Content = item.getString(TAG_Content);

                Board board = new Board();

                board.setTitle(Title);
                board.setContent(Content);

                list.add(Title);
                arrayList.add(board);
                freeBoard_adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
