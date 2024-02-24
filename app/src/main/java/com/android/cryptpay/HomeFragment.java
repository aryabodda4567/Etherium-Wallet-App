package com.android.cryptpay;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.cryptpay.eth.BalanceFetcher;
import com.android.cryptpay.eth.TransactionActivity;
import com.android.cryptpay.qrmanager.QRGenerator;
import com.android.cryptpay.qrmanager.QRScannerActivity;


public class HomeFragment extends Fragment {
    private static final int QR_SCANNER_REQUEST_CODE = 123;
    final double[] balance = new double[1];
    ImageView ivScanner, ivSend, ivReceived, ivUserScanner;
    TextView balanceTv, address, showHideBalance;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        ivScanner = view.findViewById(R.id.ivScanner);
        ivReceived = view.findViewById(R.id.ivReceived);
        ivSend = view.findViewById(R.id.ivPay);
        ivUserScanner = view.findViewById(R.id.ivUserScanner);
        balanceTv = view.findViewById(R.id.balanceTv);
        address = view.findViewById(R.id.tvAddress);
        showHideBalance = view.findViewById(R.id.balanceShowHide);


        //Display Qr and Address
        createQRAndDisplayAddress();

        //Display balance
        fetchBalance();

  /*
        showHideBalance.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                balance[0] = Double.parseDouble( balanceTv.getText().toString().trim());
                if(!balanceTv.getText().toString().trim().equals("******")){
                  //  balanceTv.setVisibility(View.INVISIBLE);
                    balanceTv.setText("******");
                    showHideBalance.setText("Show Balance");
                }else{
                  //  balanceTv.setVisibility(View.VISIBLE);
                    balanceTv.setText(String.valueOf(balance[0]));
                    showHideBalance.setText("Hide Balance");
                }
            }
        });
*/


        ivScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRScanner();
            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TransactionActivity.class));
                requireActivity().overridePendingTransition(R.anim.fade_out, R.anim.fade_in);

            }
        });

        ivReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceivedETHFragment receivedETHFragment = new ReceivedETHFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyReceived", true);
                receivedETHFragment.setArguments(bundle);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, receivedETHFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("UserEthAddress", address.getText().toString().trim());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Wallet Address is Copied", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void startQRScanner() {
        Intent intent = new Intent(getActivity(), QRScannerActivity.class);
        startActivityForResult(intent, QR_SCANNER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_SCANNER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle the result if needed
        }
    }

    @SuppressLint("SetTextI18n")
    private void createQRAndDisplayAddress() {
        String ethVal = requireContext().getString(R.string.eth_address);
        String ethAddress = new SharedPreferenceManager(getContext()).getStringValue(ethVal, null);
        if (ethAddress != null) {
            address.setText(ethAddress);
            //Create Qr and Display
            int width = 500;
            int height = 500;
            Bitmap qrBitmap = QRGenerator.generateQRCode(ethAddress, width, height);
            ivUserScanner.setImageBitmap(qrBitmap);
        }


    }

    private void fetchBalance() {
        try {
            BalanceFetcher.fetchBalanceAsync(requireContext(), new BalanceFetcher.BalanceFetchListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onBalanceFetched(String balance) {
                    // Handle the fetched balance here
                    if (balance != null) {
                        balanceTv.setText(balance + " ETH");
                        System.out.println(balance);
                    } else {

                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Handle error here

                    System.out.println(errorMessage);
                }
            });
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


}