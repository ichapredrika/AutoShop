package com.junior.autoshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainAdminActivity extends AppCompatActivity {
    public static final String EXTRA_STATE = "EXTRA_STATE";
    public static final String STATE_BOOKED = "EXTRA_BOOKED";
    public static final String STATE_WORKING_SPACE = "EXTRA_WORKING_SPACE";
    public static final String STATE_PROFILE = "EXTRA_PROFILE";
    public static final String STATE_PAYMENT = "EXTRA_PAYMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        String state = intent.getStringExtra(EXTRA_STATE);

        switch (state){
            case STATE_BOOKED:
                navView.setSelectedItemId(R.id.navigation_booked);
                break;
            case STATE_WORKING_SPACE:
                navView.setSelectedItemId(R.id.navigation_working_space);
                break;
            case STATE_PROFILE:
                navView.setSelectedItemId(R.id.navigation_profile);
                break;
            case STATE_PAYMENT:
                navView.setSelectedItemId(R.id.navigation_payment);
                break;
            default:
                navView.setSelectedItemId(R.id.navigation_booked);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.navigation_booked:
                    fragment = new BookedFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .commit();
                    return true;
                case R.id.navigation_working_space:
                    fragment = new WorkingSpaceFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .commit();
                    Log.d("tag", "junnn");
                    return true;
                case R.id.navigation_payment:
                    fragment = new PaymentFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .commit();
                    return true;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .commit();
                    return true;
            }
            return false;
        }
    };

}
