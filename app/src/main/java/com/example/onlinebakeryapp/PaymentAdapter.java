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
import com.example.onlinebakeryapp.model.CustomerCart;
import com.example.onlinebakeryapp.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private List<CustomerCart> mCustomerCartList;
    Context context;
    LayoutInflater inflater;

    public PaymentAdapter(Context ctx, List<CustomerCart> mCustomerCartList){
        this.inflater = LayoutInflater.from(ctx);
        this.mCustomerCartList = mCustomerCartList;
        this.context = ctx;
    }

    @NonNull
    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetails_list,parent,false);
        return new PaymentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentAdapter.ViewHolder holder, int position) {

        String loadProductDetailsURL = "http://192.168.8.107/cakeProduct/displayProductDetails.php/";
        final CustomerCart mCustomerCart = mCustomerCartList.get(position);

        holder.mTvODAmt.setText(mCustomerCart.getPrice());
        holder.mTvODWeight.setText("Weight: "+ mCustomerCart.getProduct_kg() + "kg");

        if (mCustomerCart.getCakeOnMessage()!=null && !mCustomerCart.getCakeOnMessage().equals("")){
            holder.mTvODMessageOnCake.setText("Message: " + mCustomerCart.getCakeOnMessage());
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
                holder.mTvODCakeName.setText(posts.get(Integer.parseInt(mCustomerCart.getProductId())-1).getTitle());
                Glide.with(context).load(posts.get(Integer.parseInt(mCustomerCart.getProductId())-1).getProductImage())
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
        return mCustomerCartList.size();
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
