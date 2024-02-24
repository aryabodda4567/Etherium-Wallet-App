package com.android.cryptpay;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cryptpay.apihandler.TransactionFetcher;
import com.android.cryptpay.eth.CryptoTransaction;
import com.android.cryptpay.eth.CryptoTransactionAdapter;

import org.json.JSONArray;
import org.web3j.utils.Convert;

import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HistoryFragment extends Fragment {


    View view;


    TextView textView;
    ProgressBar progressBar;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.noTransactionFound);
        editText = view.findViewById(R.id.idSearch);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    // Toast.makeText(getContext(), "Search", Toast.LENGTH_SHORT).show();
                    String ethAddress = editText.getText().toString().trim();
                    if (ethAddress.isEmpty()) {
                        Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_SHORT).show();
                    } else {

                        textView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        fetchTransactions(ethAddress);
                    }
                }
                return false;
            }
        });

        //Display Transactions
        fetchTransactions(null);


        return view;
    }


    private String formatTimestamp(long timestamp) {
        String formattedTimestamp;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant instant = Instant.ofEpochSecond(timestamp);
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a", Locale.getDefault());
            formattedTimestamp = zonedDateTime.format(formatter);
        } else {
            java.util.Date date = new java.util.Date(timestamp * 1000); // Convert seconds to milliseconds
            formattedTimestamp = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(date);
        }

        return formattedTimestamp;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchTransactions(String ethAddress) {


        TransactionFetcher transactionFetcher = new TransactionFetcher(getContext(), new TransactionFetcher.TransactionListener() {
            @Override
            public void onTransactionsFetched(JSONArray jsonResponse) {

                progressBar.setVisibility(View.INVISIBLE);
                RecyclerView recyclerView = view.findViewById(R.id.historyRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                if (jsonResponse.length() == 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {


                    List<String> list = new ArrayList<>();

                    try {
                        List<CryptoTransaction> transactionList = new ArrayList<>();

                        for (int i = 0; i < jsonResponse.length(); i++) {

                            if (!list.contains((jsonResponse.getJSONObject(i).getString("hash")))) {

                                list.add(jsonResponse.getJSONObject(i).getString("hash"));

                                String value = jsonResponse.getJSONObject(i).getString("value");
                                value = Convert.fromWei(value, Convert.Unit.ETHER).toPlainString() + " ETH";
                                String gasUsed = jsonResponse.getJSONObject(i).getString("gasUsed");
                                String toAddress = jsonResponse.getJSONObject(i).getString("to");
                                String time = jsonResponse.getJSONObject(i).getString("timeStamp");
                                String from = jsonResponse.getJSONObject(i).getString("from");
                                time = formatTimestamp(Long.parseLong(time));

                                String blockNumber = jsonResponse.getJSONObject(i).getString("blockNumber");

                                transactionList.add(new CryptoTransaction(value, gasUsed, toAddress, time, blockNumber , from));
                            }

                        }

                        CryptoTransactionAdapter adapter = new CryptoTransactionAdapter(transactionList );
                        recyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });


        if (ethAddress == null) {
            String ethval = getContext().getString(R.string.eth_address);
            ethAddress = new SharedPreferenceManager(getContext()).getStringValue(ethval, null);
        }

        transactionFetcher.execute(ethAddress);

    }

}