package org.techtown.cap3.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.techtown.cap3.MainActivity;
import org.techtown.cap3.R;
import org.techtown.cap3.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private static final String TAG = "Fragment";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);



        //binding = FragmentHomeBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();


        return rootview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}