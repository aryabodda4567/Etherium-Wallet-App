package com.android.cryptpay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.cryptpay.authantication.AuthenticationActivity;
import com.android.cryptpay.eth.ImportAccountTask;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsFragment extends Fragment {

    ImageButton imageButton;
    TextView textView , textViewAck;
    View view;
    EditText privateKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        view = inflater.inflate(R.layout.fragment_settings, container, false);
        privateKey = view.findViewById(R.id.editTextImportPrivateKey);
        textViewAck = view.findViewById(R.id.textViewAck);
        view.findViewById(R.id.logoutIB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        view.findViewById(R.id.logoutTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        privateKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_GO)) {

                     String privateKeyString = privateKey.getText().toString().trim();
                     if(privateKeyString.isEmpty()){
                         Toast.makeText(getContext(), "Enter Private Key", Toast.LENGTH_SHORT).show();
                     }else {
                         Toast.makeText(getContext(), privateKeyString, Toast.LENGTH_SHORT).show();
                         importAccount(privateKeyString);


                     }


                    }




                return false;
            }
        });


        return view;
    }


    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        new SharedPreferenceManager(getContext()).clearData();
        startActivity(new Intent(getContext(), AuthenticationActivity.class));
        requireActivity().finish();
        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
    }



    void importAccount(String privateKeyString){
        ImportAccountTask importTask = new ImportAccountTask(new ImportAccountTask.ImportAccountListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAccountImported(String address, String privateKey) {
                Toast.makeText(getContext(), "Found", Toast.LENGTH_SHORT).show();
                textViewAck.setTextColor(Color.GREEN);
                textViewAck.setText("Account Added Successfully");
                textViewAck.setVisibility(View.VISIBLE);

                System.out.println(address);
                System.out.println(privateKey);
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onImportFailed() {


                textViewAck.setTextColor(Color.RED);
                textViewAck.setText("Wrong Private Key");
               textViewAck.setVisibility(View.VISIBLE);
            }
        });
        importTask.execute(privateKeyString);

    }
}