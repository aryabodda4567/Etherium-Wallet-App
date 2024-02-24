package com.android.cryptpay.eth;

// ReceivedTransactionModel.java

public class ReceivedTransactionModel {
    private String value;
    private String gasUsed;
    private String toAddress;
    private String time;
    private String blockNumber;

    public ReceivedTransactionModel(String value, String gasUsed, String toAddress, String time, String blockNumber) {
        this.value = value;
        this.gasUsed = gasUsed;
        this.toAddress = toAddress;
        this.time = time;
        this.blockNumber = blockNumber;
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
}
