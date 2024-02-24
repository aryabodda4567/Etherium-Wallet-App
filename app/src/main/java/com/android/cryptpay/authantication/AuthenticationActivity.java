package com.android.cryptpay.authantication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.android.cryptpay.MainActivity;
import com.android.cryptpay.R;
import com.android.cryptpay.SharedPreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {
    private final int REQ_CODE = 4567;
    ImageView googleLogin;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        googleLogin = findViewById(R.id.imGoogleLogin);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_key))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, options);


        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, REQ_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    getUserDetailsAndStartMain();

                                } else {

                                    Toast.makeText(AuthenticationActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            finish();


        }
    }



    private void getUserDetailsAndStartMain() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String displayName = user.getDisplayName();
            String email = user.getEmail();
            String photoUrl = String.valueOf(user.getPhotoUrl());
            String uid = user.getUid();


            //  Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();

            UserRegistrationManager userRegistrationManager = new UserRegistrationManager(this);
            userRegistrationManager.registerUserIntoFirebase(uid, email, displayName, photoUrl);
            userRegistrationManager.registerUser(displayName, email, photoUrl, uid);
            Toast.makeText(this, "Logged in as " + displayName, Toast.LENGTH_SHORT).show();


            try {


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                String ethAddress = snapshot.child("Address").getValue(String.class);
                                String privateKey = snapshot.child("Key").getValue(String.class);


                                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
                                String ethVal = getApplicationContext().getString(R.string.eth_address);
                                if (ethAddress != null) {
                                    sharedPreferenceManager.putValue(ethVal, ethAddress);



                                    openMainActivity();
                                }


                            }
                        } else {

                            // Toast.makeText(getApplicationContext(), "No data found for this user.", Toast.LENGTH_SHORT).show();
                        }
                        openMainActivity();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }


    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        finish();
    }




}