package com.junior.autoshop.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.junior.autoshop.RegisterAutoshopFragment;
import com.junior.autoshop.RegisterCustomerFragment;

public class TabsAdapter extends FragmentStatePagerAdapter {
    private Context context;
    int totalTabs;

    public TabsAdapter(Context context, @NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.context = context;
        this.totalTabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RegisterCustomerFragment registerCustomerFragment = new RegisterCustomerFragment();
                return registerCustomerFragment;
            case 1:
                RegisterAutoshopFragment registerAutoshopFragment = new RegisterAutoshopFragment();
                return registerAutoshopFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
