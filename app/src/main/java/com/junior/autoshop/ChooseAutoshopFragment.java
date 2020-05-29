package com.junior.autoshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.junior.autoshop.models.Service;

import java.util.ArrayList;


public class ChooseAutoshopFragment extends Fragment {
    private ArrayList<Service> listSelectedService;
    public static String EXTRA_SERVICE = "SERVICE";
    public ChooseAutoshopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_autoshop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnProceed = view.findViewById(R.id.btn_proceed);

        listSelectedService = (ArrayList<Service>)getArguments().getSerializable(EXTRA_SERVICE);
        Log.d("selected service", listSelectedService.toString());
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BookingDetailFragment bookingDetailFragment = new BookingDetailFragment();
              /*  Bundle mBundle = new Bundle();
                mBundle.putString(DetailCategoryFragment.EXTRA_NAME, "Lifestyle");
                String description = "Kategori ini akan berisi produk-produk lifestyle";
                mDetailCategoryFragment.setArguments(mBundle);
                mDetailCategoryFragment.setDescription(description);*/
                FragmentManager mFragmentManager = getFragmentManager();
                if (mFragmentManager!= null) {
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container_layout, bookingDetailFragment, BookingDetailFragment.class.getSimpleName());
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.commit();
                }
            }
        });
    }
}
