package com.android.cryptpay.eth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.cryptpay.EncryptionUtils;
import com.android.cryptpay.R;
import com.android.cryptpay.SharedPreferenceManager;

public class TransactionActivity extends AppCompatActivity {
    Button button;
    TextView balanceText, lowBalanceText, wrongPasswordTv, senderAddressText;
    double ethBalance, sendValue;
    EditText valueText, passwordText, receiverAddressText;
    private String privateKey, senderAddress, receiverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        balanceText = findViewById(R.id.balanceTV);
        receiverAddressText = findViewById(R.id.editTextToAddress);
        valueText = findViewById(R.id.editTextValueInEth);
        senderAddressText = findViewById(R.id.senderAddressTV);
        lowBalanceText = findViewById(R.id.lowBalanceTv);
        wrongPasswordTv = findViewById(R.id.wrongPasswordTv);
        passwordText = findViewById(R.id.editTextTextPassword);
        button = findViewById(R.id.btnPay);


        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("receiverAddress") != null) {
                receiverAddressText.setText(intent.getStringExtra("receiverAddress"));
                receiverAddressText.setFocusable(false);
                receiverAddressText.setFocusableInTouchMode(false);
            }
        }

        //Display Sender Address
        displaySenderAddress();


        //Fetch and display balance
        fetchBalance();
        button.setOnClickListener(v -> {

            //Set wrong password and low Balance TV to invisible
            wrongPasswordTv.setVisibility(View.INVISIBLE);
            lowBalanceText.setVisibility(View.INVISIBLE);


            String value = valueText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            receiverAddress = receiverAddressText.getText().toString().trim();

            if (value.isEmpty()) {
                makeToast("Enter ETH value");
            } else if (password.isEmpty()) {
                makeToast("Enter Password");
            } else if (receiverAddress.isEmpty()) {
                makeToast("Enter Receiver Address");
            } else {
                if (ethBalance < Double.parseDouble(value) | ethBalance == 0) {
                    //Display Low balance
                    lowBalanceText.setVisibility(View.VISIBLE);

                    if (Double.parseDouble(value) <= 0) {
                        makeToast("Value should not be 0");
                    }

                } else {
                    //Check Password
                    fetchPrivateKey(password);
                }
            }
        });


    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private void fetchBalance() {
        try {
            BalanceFetcher.fetchBalanceAsync(getApplicationContext(), new BalanceFetcher.BalanceFetchListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onBalanceFetched(String balance) {
                    // Handle the fetched balance here
                    if (balance != null) {
                        ethBalance = Double.parseDouble(balance);
                        balanceText.setText(balance + " ETH");
                        System.out.println(balance);
                    } else {
                        makeToast("Some thing went Wrong");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Handle error here
                    makeToast("Some thing went wrong");
                    System.out.println(errorMessage);
                }
            });
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void fetchPrivateKey(String password) {
        EncryptionUtils.getKeyFromDatabase(password, getApplicationContext(), new EncryptionUtils.KeyFetchListener() {
            @Override
            public void onKeyFetched(String key) {

                if (key != null) {
                    privateKey = key;
                    performTransaction();

                } else {
                    wrongPasswordTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void performTransaction() {
        sendValue = Double.parseDouble(valueText.getText().toString().trim());

        EthTransferManager.transferEth(getApplicationContext(), senderAddress, receiverAddress, sendValue, privateKey, new EthTransferManager.TransferListener() {
            @Override
            public void onTransferSuccessful(String transactionHash) {
                // Handle successful transfer
                System.out.println("EthTransfer Transfer successful. Transaction hash: " + transactionHash);
            }

            @Override
            public void onTransferFailed(String errorMessage) {
                // Handle transfer failure
                System.out.println("EthTransfer Transfer failed. Error: " + errorMessage);
            }
        });
    }

    void displaySenderAddress() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        String userVal = getApplicationContext().getString(R.string.eth_address);
        senderAddress = sharedPreferenceManager.getStringValue(userVal, null);
        senderAddressText.setText(senderAddress);
    }


}