package com.junior.autoshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainCustomerActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout llBookingService;
    LinearLayout llSos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer);

        toolbar = findViewById(R.id.toolbar);
        llBookingService = findViewById(R.id.ll_booking_service);
        llSos = findViewById(R.id.ll_sos);

        llBookingService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCustomerActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_STATE, MainActivity.STATE_BOOKING);
                startActivity(intent);
            }
        });

        llSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCustomerActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_STATE, MainActivity.STATE_SOS);
                startActivity(intent);
            }
        });

    }
}
