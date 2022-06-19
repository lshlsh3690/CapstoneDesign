package org.techtown.cap3.ui.init;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.techtown.cap3.BluetoothActivity;
import org.techtown.cap3.R;
import org.techtown.cap3.databinding.FragmentInitBinding;
import org.techtown.cap3.databinding.FragmentRemoteBinding;

import java.util.ArrayList;

public class InitFragment extends Fragment {
    private FragmentInitBinding binding;
    private TextView textView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_init, container, false);

        //binding = FragmentInitBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();
        textView = rootview.findViewById(R.id.bluetooth);
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Intent intent = new Intent(getActivity(), BluetoothActivity.class);
                //startActivity(intent);
                try {
                    getContext().startActivity(new Intent(getContext(),BluetoothActivity.class));
                }catch (Exception e){
                    e.printStackTrace();
                }
                //getContext().startActivity(new Intent(getContext(),BluetoothActivity.class));
            }
        });

        return rootview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

