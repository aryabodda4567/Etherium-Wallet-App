package com.android.cryptpay.eth;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.cryptpay.EncryptionUtils;
import com.android.cryptpay.MainActivity;
import com.android.cryptpay.R;
import com.android.cryptpay.SharedPreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {


    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        TextView walletAddress = findViewById(R.id.tvWalletAddress);
        Button goButton = findViewById(R.id.btnSave);
        EditText passwordText = findViewById(R.id.ethPassword);


        EthereumManager ethereumManager = new EthereumManager(getApplicationContext());


        //create account

        ethereumManager.createAccountAsync(new EthereumManager.CreateAccountCallback() {
            @Override
            public void onSuccess(String ethAddress) {
                //Toast.makeText(getApplicationContext(), "Ethereum account created"  , Toast.LENGTH_LONG).show();
                walletAddress.setText(ethAddress);

            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


        walletAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Address", ethereumManager.getEthAddress());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "Wallet Address is Copied", Toast.LENGTH_SHORT).show();
            }
        });


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passwordText.getText().toString().trim().length() < 1) {
                    Toast.makeText(CreateAccountActivity.this, "Enter the password", Toast.LENGTH_SHORT).show();
                } else {

                    //Encrypt Password
                    String passwordString = passwordText.getText().toString();
                    String encryptedPassword = EncryptionUtils.encrypt(passwordString);

                    //Encrypt Private Key
                    String privateKey = ethereumManager.getPrivateKey();
                    ///   Toast.makeText(CreateAccountActivity.this, passwordString+"  "+ encryptedPassword, Toast.LENGTH_SHORT).show();
                    String encryptedPrivateKey = EncryptionUtils.encrypt(privateKey);
                    insertIntoDb(encryptedPassword, encryptedPrivateKey, ethereumManager.getEthAddress());

                }

            }
        });

    }

    private void insertIntoDb(String password, String privateKey, String ethAddress) {

        // Get UserId
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        String useridVal = getApplicationContext().getString(R.string.user_id);
        String userId = sharedPreferenceManager.getStringValue(useridVal, null);

        if (userId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Password", password);
            hashMap.put("Key", privateKey);
            hashMap.put("Address", ethAddress);

            databaseReference.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    String ethVal = getApplicationContext().getString(R.string.eth_address);

                    Toast.makeText(CreateAccountActivity.this, "Your Password Is Created", Toast.LENGTH_SHORT).show();
                    sharedPreferenceManager.putValue("isPasswordCreated", true);
                    sharedPreferenceManager.putValue(ethVal, ethAddress);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateAccountActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
