package com.junior.autoshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        setTitle("Barcode Scan eRecycle");

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mScannerView = findViewById(R.id.zxscan);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause(){
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult){
        Log.v("Tag", rawResult.getText());
        Log.v("Tag", rawResult.getBarcodeFormat().toString());

        mScannerView.resumeCameraPreview(this);
        super.onPause();
        mScannerView.stopCamera();

        Intent intent = getIntent();
        intent.putExtra("SCAN_RESULT", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
    }
}
