package com.junior.autoshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class HomeAdminActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout llBookedService;
    LinearLayout llWorkingSpace;
    LinearLayout llPayment;
    LinearLayout llProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        toolbar = findViewById(R.id.toolbar);
        llBookedService = findViewById(R.id.ll_booked_service);
        llWorkingSpace = findViewById(R.id.ll_working_space);
        llPayment =findViewById(R.id.ll_payment);
        llProfile = findViewById(R.id.ll_profile);

        llBookedService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this, MainAdminActivity.class);
                intent.putExtra(MainAdminActivity.EXTRA_STATE, MainAdminActivity.STATE_BOOKED);
                startActivity(intent);
            }
        });

        llWorkingSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this, MainAdminActivity.class);
                intent.putExtra(MainAdminActivity.EXTRA_STATE, MainAdminActivity.STATE_WORKING_SPACE);
                intent.putExtra("namaItem", "junior");
                startActivity(intent);
            }
        });

        llPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this, MainAdminActivity.class);
                intent.putExtra(MainAdminActivity.EXTRA_STATE, MainAdminActivity.STATE_PAYMENT);
                startActivity(intent);
            }
        });

        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this, MainAdminActivity.class);
                intent.putExtra(MainAdminActivity.EXTRA_STATE, MainAdminActivity.STATE_PROFILE);
                startActivity(intent);
            }
        });

    }
}
