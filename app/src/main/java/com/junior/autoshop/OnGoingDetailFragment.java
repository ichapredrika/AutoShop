package com.junior.autoshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class OnGoingDetailFragment extends Fragment {
    public static String EXTRA_TRANS_ID = "TRANSACTION_ID";
    private String transId;
    public OnGoingDetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_on_going_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transId = getArguments().getString(EXTRA_TRANS_ID);
        Toast.makeText(getContext(), transId, Toast.LENGTH_SHORT).show();
    }
}
