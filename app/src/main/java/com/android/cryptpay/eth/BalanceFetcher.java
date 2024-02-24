package com.android.cryptpay.eth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.cryptpay.R;
import com.android.cryptpay.SharedPreferenceManager;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BalanceFetcher {

    public static void fetchBalanceAsync(final Context context, final BalanceFetchListener listener) {
        final String infuraApi = context.getString(R.string.infura_url);
        final String ethVal = context.getString(R.string.eth_address);
        final String ethAddress = new SharedPreferenceManager(context).getStringValue(ethVal, null);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Web3j web3 = Web3j.build(new HttpService(infuraApi));

                if (ethAddress != null) {
                    EthGetBalance balanceResponse = web3.ethGetBalance(ethAddress, DefaultBlockParameterName.LATEST).send();
                    if (balanceResponse.hasError()) {
                        String errorMessage = "Error occurred: " + balanceResponse.getError().getMessage();
                        notifyError(listener, errorMessage);
                    } else {
                        BigInteger balanceWei = balanceResponse.getBalance();
                        String balanceEther = Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER).toPlainString();
                        notifyBalanceFetched(listener, balanceEther);
                    }
                } else {
                    notifyError(listener, "ETH Address is null");
                }
            } catch (IOException e) {
                notifyError(listener, e.getMessage());
            }
        });
    }

    private static void notifyBalanceFetched(final BalanceFetchListener listener, final String balance) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (listener != null) {
                listener.onBalanceFetched(balance);
            }
        });
    }

    private static void notifyError(final BalanceFetchListener listener, final String errorMessage) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (listener != null) {
                listener.onError(errorMessage);
            }
        });
    }

    public interface BalanceFetchListener {
        void onBalanceFetched(String balance);

        void onError(String errorMessage);
    }
}

/*
  try {


                BalanceFetcher.fetchBalanceAsync(getApplicationContext(), new BalanceFetcher.BalanceFetchListener() {
                    @Override
                    public void onBalanceFetched(String balance) {
                        // Handle the fetched balance here
                        System.out.println(balance);

                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Handle error here
                        System.out.println(errorMessage);
                    }
                });
            }catch (Exception e){
                System.out.println("Error: "+ e.getMessage());
            }
 */
