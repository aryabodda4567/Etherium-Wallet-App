package com.android.cryptpay.eth;

// ReceivedTransactionAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.cryptpay.R;
import java.util.List;

public class ReceivedTransactionAdapter extends RecyclerView.Adapter<ReceivedTransactionAdapter.TransactionViewHolder> {

    private final List<ReceivedTransactionModel> transactionList;

    public ReceivedTransactionAdapter(List<ReceivedTransactionModel> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_card, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        ReceivedTransactionModel transaction = transactionList.get(position);
        holder.textValue.setText(transaction.getValue());
        holder.textGasUsedValue.setText(transaction.getGasUsed());
        holder.textToAddressValue.setText(transaction.getToAddress());
        holder.textTimeValue.setText(transaction.getTime());
        holder.textBlockNumberValue.setText(transaction.getBlockNumber());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textValue, textGasUsedValue, textToAddressValue, textTimeValue, textBlockNumberValue;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textValue = itemView.findViewById(R.id.rtext_value);
            textGasUsedValue = itemView.findViewById(R.id.rtext_gas_used_value);
            textToAddressValue = itemView.findViewById(R.id.rtext_to_address_value);
            textTimeValue = itemView.findViewById(R.id.rtext_time_value);
            textBlockNumberValue = itemView.findViewById(R.id.rtext_block_number_value);
        }
    }
}
