package com.android.cryptpay.eth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.android.cryptpay.R;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

public class EthereumManager {

    private final Context context;
    private final String URL;
    private String publicKey;
    private String privateKey;
    private String ethAddress;

    public EthereumManager(Context context) {
        this.context = context;
        this.URL = context.getString(R.string.infura_url);
    }

    public void createAccountAsync(CreateAccountCallback callback) {
        new CreateAccountAsyncTask(callback).execute();
    }

    public void setupBouncyCastle() {
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) return;

        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            return;
        }

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public interface CreateAccountCallback {
        void onSuccess(String ethAddress);

        void onError(String errorMessage);
    }

    @SuppressLint("StaticFieldLeak")
    private class CreateAccountAsyncTask extends AsyncTask<Void, Void, String> {
        private final CreateAccountCallback callback;

        public CreateAccountAsyncTask(CreateAccountCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                setupBouncyCastle();
                // Generate a new Ethereum key pair
                ECKeyPair ecKeyPair = Keys.createEcKeyPair();

                // Get the private key (in hexadecimal format)
                BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();
                privateKey = privateKeyInDec.toString(16);

                // Get the Ethereum address
                publicKey = Keys.getAddress(ecKeyPair);
                ethAddress = Keys.toChecksumAddress(publicKey);

                // Create Credentials object
                Credentials credentials = Credentials.create(privateKey, publicKey);


                return ethAddress;
            } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                     NoSuchProviderException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {

                    callback.onError("Error creating Ethereum account");
                }
            }
        }
    }

}

/*
                 //Create a wallet
                    EthereumManager ethereumManager = new EthereumManager(context);
                    ethereumManager.createAccountAsync(new EthereumManager.CreateAccountCallback() {
                        @Override
                        public void onSuccess(String ethAddress) {
                            Toast.makeText(context, "Ethereum account created: " + ethAddress, Toast.LENGTH_LONG).show();

                        }
                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } );
 */
