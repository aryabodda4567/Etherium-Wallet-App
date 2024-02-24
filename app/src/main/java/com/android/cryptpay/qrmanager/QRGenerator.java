package com.android.cryptpay.qrmanager;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRGenerator {

    public static Bitmap generateQRCode(String textToEncode, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(textToEncode, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}

/*Usage

        String textToEncode = "Your string here";
        int width = 500;
        int height = 500;
        Bitmap qrBitmap = QRGenerator.generateQRCode(textToEncode, width, height);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(qrBitmap);
 */