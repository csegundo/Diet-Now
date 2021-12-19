package com.dietnow.app.ucm.fdi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

/*
*  Lector de codigo de barras
*  https://github.com/yuriy-budiyev/code-scanner
* */



public class activity_camera extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private int CAMERA_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setUpPermissions();
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity_camera.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    private void setUpPermissions(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest();
        }
        else{

        }
    }
    private void makeRequest(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults == null || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Se necesitan los permisos de la camara para poder leer un codigo de barras", Toast.LENGTH_SHORT);
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                /* Success */
            }
        }
    }
}