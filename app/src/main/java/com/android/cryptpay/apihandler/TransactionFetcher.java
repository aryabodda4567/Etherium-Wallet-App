package com.android.cryptpay.apihandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.android.cryptpay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransactionFetcher extends AsyncTask<String, Void, JSONArray> {

    private static final String TAG = TransactionFetcher.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    final Context context;
    private final TransactionListener listener;

    public TransactionFetcher(Context context, TransactionListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected JSONArray doInBackground(String... addresses) {

        String address = addresses[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String transactionJsonString = null;


        try {
            // Construct the URL for the Etherscan API
            String baseUrl = "https://api.etherscan.io/api";
            String apiKey = context.getString(R.string.ether_scan_api);
            String endpoint = "/?module=account&action=txlist&startblock=0&endblock=99999999&address=" + address + "&sort=desc&apikey=" + apiKey;
            URL url = new URL(baseUrl + endpoint);

            // Connect to the URL
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            transactionJsonString = buffer.toString();
        } catch (IOException e) {
            System.out.println("Error  " + e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    System.out.println("Error closing stream" + e.getMessage());
                }
            }
        }

        try {
            // Parse JSON response
            JSONObject transactionJson = new JSONObject(transactionJsonString);
            return transactionJson.getJSONArray("result");
        } catch (JSONException e) {
            System.out.println("Error parsing JSON" + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONArray transactions) {
        if (transactions != null) {
            listener.onTransactionsFetched(transactions);
        } else {
            System.out.println("No transactions found.");
        }
    }

    public interface TransactionListener {
        void onTransactionsFetched(JSONArray transactions);
    }
}

/*

        TransactionFetcher transactionFetcher = new TransactionFetcher(getApplicationContext(),new TransactionFetcher.TransactionListener() {
            @Override
            public void onTransactionsFetched(JSONArray jsonResponse) {
                // Process the transactions here

                List<TransactionParser.Transaction> transactions = TransactionParser.parseTransactions(jsonResponse.toString());

                for (TransactionParser.Transaction transaction : transactions) {
                    System.out.println("Block Number: " + transaction.getBlockNumber());
                    System.out.println("Timestamp: " + transaction.getTimestamp());
                    System.out.println("Hash: " + transaction.getHash());
                    System.out.println("From: " + transaction.getFrom());
                    System.out.println("To: " + transaction.getTo());
                    System.out.println("Value: " + transaction.getValue());
                    System.out.println("Contract Address: " + transaction.getContractAddress());
                    System.out.println("Input: " + transaction.getInput());
                    System.out.println("Type: " + transaction.getType());
                    System.out.println("Gas: " + transaction.getGas());
                    System.out.println("Gas Used: " + transaction.getGasUsed());
                    System.out.println("Trace ID: " + transaction.getTraceId());
                    System.out.println("Is Error: " + transaction.getIsError());
                    System.out.println("Error Code: " + transaction.getErrCode());
                    System.out.println();
                }


            }
        });

          // Replace with the Ethereum address you want to query
        transactionFetcher.execute("0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae");






 */
