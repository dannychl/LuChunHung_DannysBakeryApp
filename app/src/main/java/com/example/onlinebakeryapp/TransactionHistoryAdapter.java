package com.example.onlinebakeryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionHistoryAdapter  extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(String date, String time);
    }

    private String transactionList;
    Context context;
    LayoutInflater inflater;
    private TransactionHistoryAdapter.OnItemClickListener mListener;
    private String[] date, time, price;
    private int size;

    public TransactionHistoryAdapter(Context ctx, String[] date, String[]time, int size, String[] price ){
        this.inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.date = new String [size];
        this.time = new String [size];
        this.price = new String [size+1];
        this.date = date;
        this.time = time;
        this.price = price;
        this.size = size;
    }

    public void setOnItemClickListener(TransactionHistoryAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public TransactionHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.transactionhistory_list,parent,false);
        return new TransactionHistoryAdapter.ViewHolder(view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull final TransactionHistoryAdapter.ViewHolder holder, final int position) {

        holder.mTvTransactionOrderTime.setText("Time Ordered: "+time[position]);
        holder.mTvTransactionOrderDate.setText("Date Ordered: "+date[position]);
        holder.mTvTransactionTotalAmount.setText("RM"+ price[position]);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTransactionOrderDate, mTvTransactionOrderTime, mTvTransactionTotal, mTvTransactionTotalAmount;

        public ViewHolder(@NonNull View itemView, final TransactionHistoryAdapter.OnItemClickListener listener) {
            super(itemView);

            mTvTransactionOrderDate = (TextView) itemView.findViewById(R.id.tvTransactionOrderDate);
            mTvTransactionOrderTime = (TextView) itemView.findViewById(R.id.tvTransactionOrderTime);
            mTvTransactionTotal = (TextView) itemView.findViewById(R.id.tvTransactionTotal);
            mTvTransactionTotalAmount = (TextView) itemView.findViewById(R.id.tvTransactionTotalAmount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(date[position], time[position]);
                        }
                    }
                }
            });
        }
    }
}
