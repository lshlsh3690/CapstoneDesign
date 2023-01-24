package org.techtown.cap3.ui.remote;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Moter extends StringRequest {
    //외부 ip로 접속 모뎀에서 dmz를 내부 ip로 설정한 후에 안드로이드에서 lte 망으로 외부 ip로 접속함
    //포트포워딩은 서비스포트 3000번에서 내부포트 3000번으로 설정
    final static private String URL = "http://211.37.54.49:3000/devices/moter";
    private Map<String, String> parameters;

    public Moter(String moter, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);
        parameters=new HashMap<>();
        parameters.put("flag", moter);
    }

    //hash map 에 있는 파라미터 값을 nodejs에 넘겨줄떄 사용
    @Override
    protected Map<String, String>getParams(){
        return parameters;
    }
}
