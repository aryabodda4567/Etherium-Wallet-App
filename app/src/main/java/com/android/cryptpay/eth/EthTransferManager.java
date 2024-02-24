package com.android.cryptpay.eth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.cryptpay.R;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EthTransferManager {

    public static void transferEth(final Context context, final String fromAddress, final String toAddress, final double valueInEth, final String privateKey, final TransferListener listener) {

        final String infuraApi = context.getString(R.string.infura_url);


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Web3j web3 = Web3j.build(new HttpService(infuraApi)); // Replace with your Infura API key
                Credentials credentials = Credentials.create(privateKey);

                // Convert ETH value to Wei
                BigInteger valueInWei = Convert.toWei(BigDecimal.valueOf(valueInEth), Convert.Unit.ETHER).toBigInteger();

                // Send ETH transaction
                TransactionReceipt transactionReceipt = Transfer.sendFunds(web3, credentials, toAddress, BigDecimal.valueOf(valueInEth), Convert.Unit.ETHER).send();


                String transactionHash = transactionReceipt.getTransactionHash();
                notifyTransferSuccessful(listener, transactionHash);
            } catch (IOException | InterruptedException e) {
                notifyTransferFailed(listener, e.getMessage());
            } catch (TransactionException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void notifyTransferSuccessful(final TransferListener listener, final String transactionHash) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (listener != null) {
                listener.onTransferSuccessful(transactionHash);
            }
        });
    }

    private static void notifyTransferFailed(final TransferListener listener, final String errorMessage) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (listener != null) {
                listener.onTransferFailed(errorMessage);
            }
        });
    }

    public interface TransferListener {
        void onTransferSuccessful(String transactionHash);

        void onTransferFailed(String errorMessage);
    }

    /*
    //check balance before transfer

        EncryptionUtils.getKeyFromDatabase("Arya", getApplicationContext(), new EncryptionUtils.KeyFetchListener() {
            @Override
            public void onKeyFetched(String key) {

                if(key!=null){


                    Toast.makeText(MainActivity.this, key + " ", Toast.LENGTH_SHORT).show();
                    final String ethVal = getApplicationContext().getString(R.string.eth_address);
                    final String ethAddress = new SharedPreferenceManager(getApplicationContext()).getStringValue(ethVal, null);

                    EthTransferManager.transferEth(getApplicationContext(), ethAddress, "0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae", 0.0, key, new EthTransferManager.TransferListener() {
                        @Override
                        public void onTransferSuccessful(String transactionHash) {
                            // Handle successful transfer
                           System.out.println("EthTransfer Transfer successful. Transaction hash: " + transactionHash);
                        }

                        @Override
                        public void onTransferFailed(String errorMessage) {
                            // Handle transfer failure
                            System.out.println( "EthTransfer Transfer failed. Error: " + errorMessage);
                        }
                    });

                }
                else{
                    Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            }
        });






     */
}
