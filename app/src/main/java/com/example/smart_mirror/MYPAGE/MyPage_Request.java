package com.example.smart_mirror.MYPAGE;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MyPage_Request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "";
    private Map<String, String> map;


    public MyPage_Request(String _id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id", _id);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}