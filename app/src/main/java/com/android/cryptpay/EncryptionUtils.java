package com.android.cryptpay;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {


    private static final byte[] SECRET_KEY = "MySecretKey12345".getBytes(); // 16 bytes for AES
    private static final byte[] INIT_VECTOR = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F}; // 16 bytes for AES


    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR);
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR);
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static void getKeyFromDatabase(String password, Context context, final KeyFetchListener listener) {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);
        String useridVal = context.getString(R.string.user_id);
        String userId = sharedPreferenceManager.getStringValue(useridVal, null);

        if (userId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

            // Encrypt the password before querying the database
            final String encryptedPassword = encrypt(password);

            // Query to search for the entry with the given password
            Query query = databaseReference.orderByChild("Password").equalTo(encryptedPassword);
            System.out.println(encryptedPassword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.child("Key").getValue(String.class);

                        listener.onKeyFetched(decrypt(key));
                        return;
                    }
                    // If no matching key is found, notify the listener with null
                    listener.onKeyFetched(null);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled event
                }
            });
        }
    }

    // Define a listener interface to handle key fetching results
    public interface KeyFetchListener {
        void onKeyFetched(String key);
    }


}

/*
          EncryptionUtils.getKeyFromDatabase("Arya", getApplicationContext(), new EncryptionUtils.KeyFetchListener() {
            @Override
            public void onKeyFetched(String key) {

                if(key!=null){


                    Toast.makeText(MainActivity.this, key + " ", Toast.LENGTH_SHORT).show();}
                else{
                    Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
 */