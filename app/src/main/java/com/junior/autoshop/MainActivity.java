package com.junior.autoshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_STATE = "EXTRA_STATE";
    public static final String STATE_BOOKING = "EXTRA_BOOKING";
    public static final String STATE_SOS = "EXTRA_SOS";
    public static final String STATE_ONGOING = "EXTRA_ONGOING";
    public static final String STATE_PROFILE = "EXTRA_PROFILE";
    public static final String STATE_HISTORY = "EXTRA_HISTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        String state = intent.getStringExtra(EXTRA_STATE);

        switch (state){
            case STATE_BOOKING:
                navView.setSelectedItemId(R.id.navigation_booking);
                break;
            case STATE_SOS:
                navView.setSelectedItemId(R.id.navigation_sos);
                break;
            case STATE_ONGOING:
                navView.setSelectedItemId(R.id.navigation_ongoing);
                break;
            case STATE_PROFILE:
                navView.setSelectedItemId(R.id.navigation_profile);
                break;
            case STATE_HISTORY:
                navView.setSelectedItemId(R.id.navigation_history);
                break;
            default:
                navView.setSelectedItemId(R.id.navigation_booking);
                break;
        }
      /*  if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_booking);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.navigation_booking:
                    fragment = new BookingFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .commit();
                    return true;
                case R.id.navigation_sos:
                    fragment = new SosFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .commit();
                    return true;
                case R.id.navigation_ongoing:
                    fragment = new OnGoingFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .commit();
                    Log.d("tag", "junnn");
                    return true;
                case R.id.navigation_history:
                    fragment = new HistoryFragment();
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
