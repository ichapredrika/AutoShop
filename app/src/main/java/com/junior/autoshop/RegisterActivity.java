package com.junior.autoshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.junior.autoshop.adapter.TabsAdapter;

public class RegisterActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_text_1)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_text_2)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        FragmentManager fragManager = getSupportFragmentManager();
        final TabsAdapter adapter = new TabsAdapter(this, fragManager, tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
