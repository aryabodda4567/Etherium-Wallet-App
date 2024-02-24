package com.android.cryptpay.eth;

import android.os.AsyncTask;
import android.util.Pair;
import android.util.Log;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import java.math.BigInteger;

public class ImportAccountTask extends AsyncTask<String, Void, Pair<String, String>> {

    private static final String TAG = ImportAccountTask.class.getSimpleName();

    private ImportAccountListener listener;

    public ImportAccountTask(ImportAccountListener listener) {
        this.listener = listener;
    }

    @Override
    protected Pair<String, String> doInBackground(String... params) {
        String privateKey = params[0].replaceFirst("0x", ""); // Remove the "0x" prefix

        try {
            // Validate the private key
            if (!isValidPrivateKey(privateKey)) {
                throw new IllegalArgumentException("Invalid private key");
            }

            // Create ECKeyPair from the provided private key
            ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));

            // Get the Ethereum address and private key
            String address = "0x" + Keys.getAddress(ecKeyPair.getPublicKey());
            String privateKeyString = privateKey;

            return Pair.create(address, privateKeyString);
        } catch (Exception e) {
            Log.e(TAG, "Error importing account from private key", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Pair<String, String> result) {
        if (result != null) {
            listener.onAccountImported(result.first, result.second);
        } else {
            listener.onImportFailed();
        }
    }

    public interface ImportAccountListener {
        void onAccountImported(String address, String privateKey);
        void onImportFailed();
    }

    private boolean isValidPrivateKey(String privateKey) {
        // Check if the private key is a valid hexadecimal string of the correct length
        return privateKey.matches("^([0-9A-Fa-f]{64})$");
    }
}
