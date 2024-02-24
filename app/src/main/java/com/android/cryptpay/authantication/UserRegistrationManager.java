package com.android.cryptpay.authantication;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.cryptpay.R;
import com.android.cryptpay.SharedPreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserRegistrationManager {

    private final DatabaseReference mDatabase;
    private final Context context;

    public UserRegistrationManager(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


    }

    public void registerUserIntoFirebase(String userId, String email, String name, String photoUrl) {
        // Check if the user already exists
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User does not exist, insert the data
                    HashMap<String, String> userMap = getMapData(userId, email, name, photoUrl);
                    mDatabase.child(userId).setValue(userMap);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private HashMap<String, String> getMapData(String userId, String email, String name, String photoUrl) {
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("UserName", name);
        userMap.put("Email", email);
        userMap.put("PhotoUrl", photoUrl);
        userMap.put("UserId", userId);
        return userMap;
    }

    public void registerUser(String name, String email, String photoUrl, String userId) {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);
        sharedPreferenceManager.putValue(context.getString(R.string.user_name), name);
        sharedPreferenceManager.putValue(context.getString(R.string.email), email);
        sharedPreferenceManager.putValue(context.getString(R.string.photo_url), photoUrl);
        sharedPreferenceManager.putValue(context.getString(R.string.user_id), userId);

    }


}

