package com.example.onlinebakeryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onlinebakeryapp.model.CustomerTransaction;
import com.example.onlinebakeryapp.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentPerTransactionAdapter extends RecyclerView.Adapter<PaymentPerTransactionAdapter.ViewHolder> {

    private List<CustomerTransaction> mCustomerTransactionList;
    Context context;
    LayoutInflater inflater;

    public PaymentPerTransactionAdapter(Context ctx, List<CustomerTransaction> mCustomerTransactionList){
        this.inflater = LayoutInflater.from(ctx);
        this.mCustomerTransactionList = mCustomerTransactionList;
        this.context = ctx;
    }

    @NonNull
    @Override
    public PaymentPerTransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetails_list,parent,false);
        return new PaymentPerTransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentPerTransactionAdapter.ViewHolder holder, int position) {

        String loadProductDetailsURL = "http://192.168.8.107/cakeProduct/displayProductDetails.php/";
        final CustomerTransaction mCustomerTransaction = mCustomerTransactionList.get(position);

        holder.mTvODAmt.setText(mCustomerTransaction.getPrice());
        holder.mTvODWeight.setText("Weight: "+ mCustomerTransaction.getProduct_kg() + "kg");

        if (mCustomerTransaction.getCakeOnMessage()!=null && !mCustomerTransaction.getCakeOnMessage().equals("")){
            holder.mTvODMessageOnCake.setText("Message: " + mCustomerTransaction.getCakeOnMessage());
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(loadProductDetailsURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<Product>> listProduct = jsonPlaceHolderApi.getProducts();
        listProduct.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Product> posts = response.body();
                holder.mTvODCakeName.setText(posts.get(Integer.parseInt(mCustomerTransaction.getProductId())-1).getTitle());
                Glide.with(context).load(posts.get(Integer.parseInt(mCustomerTransaction.getProductId())-1).getProductImage())
                        .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mIvOdCakeImg);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCustomerTransactionList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTvODCakeName, mTvODAmt, mTvODWeight, mTvODMessageOnCake;
        private ImageView mIvOdCakeImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvODCakeName = (TextView) itemView.findViewById(R.id.tvODCakeName);
            mTvODAmt = (TextView) itemView.findViewById(R.id.tvODAmt);
            mTvODWeight = (TextView) itemView.findViewById(R.id.tvODWeight);
            mTvODMessageOnCake = (TextView) itemView.findViewById(R.id.tvODMessageOnCake);
            mIvOdCakeImg = itemView.findViewById(R.id.ivOdCakeImg);


        }

    }
}
