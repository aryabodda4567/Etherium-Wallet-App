package com.android.cryptpay.eth;

// CryptoTransaction.java
public class CryptoTransaction {
    private final String value;
    private final String gasUsed;
    private final String toAddress;
    private final String time;
    private final String blockNumber;
    private  final  String fromAddress;

    public CryptoTransaction(String value, String gasUsed, String toAddress, String time, String blockNumber, String fromAddress) {
        this.value = value;
        this.gasUsed = gasUsed;
        this.toAddress = toAddress;
        this.time = time;
        this.blockNumber = blockNumber;
        this.fromAddress = fromAddress;

    }

    public String getValue() {
        return value;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getTime() {
        return time;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public String getFromAddress(){return  fromAddress; }
}
