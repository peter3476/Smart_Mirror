package com.example.smart_mirror.BOARD;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// 게시판 글 쓰기 요청 ( insert into )

public class BoardWrite_Request extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "";
    private Map<String, String> map;

    public BoardWrite_Request(String Title, String Content, String userId, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();

        map.put("Title", Title);
        map.put("Content", Content);
        map.put("userId", userId);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
