package org.techtown.cap3.ui.remote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RemoteFragment extends Fragment {

    private FragmentRemoteBinding binding;
    private Button btn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_remote, container, false);
        btn = v.findViewById(R.id.btn_remote);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "moter가 켜짐:" + obj.get("moter"), Toast.LENGTH_SHORT).show();
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

        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
