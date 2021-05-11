package com.example.smart_mirror.BOARD;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FreeBoard_AutoComplete_Request extends StringRequest
{

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "";
    private Map<String, String> map;


    public FreeBoard_AutoComplete_Request(String search_Title, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("search_Title", search_Title);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
