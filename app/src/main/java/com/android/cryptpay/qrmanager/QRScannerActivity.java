package com.android.cryptpay.qrmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.cryptpay.MainActivity;
import com.android.cryptpay.R;
import com.android.cryptpay.eth.TransactionActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startQRScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner();
            } else {
                Toast.makeText(this, "Camera permission required to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        // integrator.setCaptureActivity(CustomScannerActivity.class); // Use a custom scanner activity for square shape preview
        integrator.setPrompt("Please wait");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                // overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                finish();
            } else {


                Intent intent = new Intent(this, TransactionActivity.class);
                intent.putExtra("receiverAddress", result.getContents());
                startActivity(intent);
                //overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                finish();
                //Toast.makeText(this, "Scan Result: " + result.getContents(), Toast.LENGTH_SHORT).show();

            }


        } else {


            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
