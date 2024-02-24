package com.android.cryptpay.eth;

// CryptoTransactionAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cryptpay.R;

import java.util.List;

public class CryptoTransactionAdapter extends RecyclerView.Adapter<CryptoTransactionAdapter.TransactionViewHolder> {

    private List<CryptoTransaction> transactionList;

    public CryptoTransactionAdapter(List<CryptoTransaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_model, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        CryptoTransaction transaction = transactionList.get(position);
        holder.textValue.setText(transaction.getValue());
        holder.textGasUsedValue.setText(transaction.getGasUsed());
        holder.textToAddressValue.setText(transaction.getToAddress());
        holder.textTimeValue.setText(transaction.getTime());
        holder.textBlockNumberValue.setText(transaction.getBlockNumber());
        holder.fromAddress.setText(transaction.getFromAddress());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textValue, textGasUsedValue, textToAddressValue, textTimeValue, textBlockNumberValue , fromAddress;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textValue = itemView.findViewById(R.id.text_value);
            textGasUsedValue = itemView.findViewById(R.id.text_gas_used_value);
            textToAddressValue = itemView.findViewById(R.id.text_to_address_value);
            textTimeValue = itemView.findViewById(R.id.text_time_value);
            textBlockNumberValue = itemView.findViewById(R.id.text_block_number_value);
            fromAddress = itemView.findViewById(R.id.text_from_address_value);
        }
    }
}
