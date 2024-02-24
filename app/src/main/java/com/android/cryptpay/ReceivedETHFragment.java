package com.android.cryptpay;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cryptpay.apihandler.TransactionFetcher;
import com.android.cryptpay.eth.CryptoTransaction;
import com.android.cryptpay.eth.CryptoTransactionAdapter;
import com.android.cryptpay.eth.ReceivedTransactionAdapter;
import com.android.cryptpay.eth.ReceivedTransactionModel;

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

public class ReceivedETHFragment extends Fragment {

    View view;
    TextView textView;
    ProgressBar progressBar;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_received_e_t_h, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.noTransactionFound);
        editText = view.findViewById(R.id.idSearch);
        fetchTransactions();


        return view;
    }

    private void fetchTransactions() {


        String ethval = getContext().getString(R.string.eth_address);

        String finalEthAddress = new SharedPreferenceManager(getContext()).getStringValue(ethval, null);
        TransactionFetcher transactionFetcher = new TransactionFetcher(getContext(), new TransactionFetcher.TransactionListener() {
            @Override
            public void onTransactionsFetched(JSONArray jsonResponse) {

                progressBar.setVisibility(View.INVISIBLE);
                RecyclerView recyclerView = view.findViewById(R.id.receivedHistoryRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                if (jsonResponse.length() == 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {


                    List<String> list = new ArrayList<>();

                    try {
                        List<ReceivedTransactionModel> receivedTransactionList = new ArrayList<>();

                        for (int i = 0; i < jsonResponse.length(); i++) {

                            if (!list.contains((jsonResponse.getJSONObject(i).getString("hash"))) &&
                                    !jsonResponse.getJSONObject(i).getString("from").equals(finalEthAddress)
                            ) {

                                list.add(jsonResponse.getJSONObject(i).getString("hash"));

                                String value = jsonResponse.getJSONObject(i).getString("value");
                                value = Convert.fromWei(value, Convert.Unit.ETHER).toPlainString() + " ETH";
                                String gasUsed = jsonResponse.getJSONObject(i).getString("gasUsed");
                                String toAddress = jsonResponse.getJSONObject(i).getString("from");
                                String time = jsonResponse.getJSONObject(i).getString("timeStamp");
                                time = formatTimestamp(Long.parseLong(time));

                                String blockNumber = jsonResponse.getJSONObject(i).getString("blockNumber");

                                receivedTransactionList.add(new ReceivedTransactionModel(value,gasUsed,toAddress,time,blockNumber));
                            }


                        }

                        if (!receivedTransactionList.isEmpty()) {
                            ReceivedTransactionAdapter adapter = new ReceivedTransactionAdapter(receivedTransactionList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            textView.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });


        transactionFetcher.execute(finalEthAddress);

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
}