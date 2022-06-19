package org.techtown.cap3.ui.remote;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.techtown.cap3.MainActivity;
import org.techtown.cap3.R;
import org.techtown.cap3.databinding.FragmentRemoteBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RemoteFragment<list> extends Fragment {

    private FragmentRemoteBinding binding;
    private Button btn;
    private ArrayList<String>itemlist;
    private ArrayAdapter<String> Adapter;
    private ListView listview;
    private WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    //출처: https://sky17777.tistory.com/46 [IT깡패's의 코딩스토리&게임&취미:티스토리]

    private Object date;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_remote, container, false);
        btn = v.findViewById(R.id.btn_remote);

        listview = (ListView)v.findViewById(R.id.history_listview);
        itemlist = new ArrayList<>();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            date = obj.get("moter");
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Start at " + date, Toast.LENGTH_SHORT).show();
                            itemlist.add(date.toString());
                            Adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, itemlist);
                            listview.setAdapter(Adapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                            String test = e.toString();
                            Toast.makeText(getActivity().getApplicationContext(), test, Toast.LENGTH_SHORT).show();

                        }
                    }
                };
                StringRequest moter=new Moter("on", listener);
                moter.setShouldCache(false);
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                requestQueue.add(moter);
            }
        });

        // 웹뷰 시작
        mWebView = (WebView) v.findViewById(R.id.webview);

        mWebView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(false); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부



        mWebView.loadUrl("211.37.54.49." +
                ":3000/MQTT.html"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작

        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
