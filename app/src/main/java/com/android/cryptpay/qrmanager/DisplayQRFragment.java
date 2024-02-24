package com.android.cryptpay.qrmanager;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.android.cryptpay.R;
import com.android.cryptpay.SharedPreferenceManager;

public class DisplayQRFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_q_r, container, false);


        String textToEncode = new SharedPreferenceManager(getContext())
                .getStringValue(requireContext().getString(R.string.eth_address), "NULL ");

        int width = 500;
        int height = 500;

        Bitmap qrBitmap = QRGenerator.generateQRCode(textToEncode, width, height);
        ImageView imageView = view.findViewById(R.id.qrImage);
        imageView.setImageBitmap(qrBitmap);
        return view;
    }
}