package com.example.smart_mirror.REGISTER;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Register_Request_CKid extends StringRequest{
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "";
    private Map<String, String> map;


    public Register_Request_CKid(String id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();

        map.put("id",id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
