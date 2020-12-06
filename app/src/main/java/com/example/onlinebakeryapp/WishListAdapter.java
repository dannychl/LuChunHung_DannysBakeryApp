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
import com.example.onlinebakeryapp.model.CustomerWishList;
import com.example.onlinebakeryapp.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private List<CustomerWishList> mCustomerWishList;
    Context context;
    LayoutInflater inflater;
    private WishListAdapter.OnItemClickListener mListener;

    public WishListAdapter(Context ctx, List<CustomerWishList> mCustomerWishList){
        this.inflater = LayoutInflater.from(ctx);
        this.mCustomerWishList = mCustomerWishList;
        this.context = ctx;
    }

    public void setOnItemClickListener(WishListAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_list,parent,false);
        return new WishListAdapter.ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final WishListAdapter.ViewHolder holder, int position) {

        String loadProductDetailsURL = "http://192.168.8.107/cakeProduct/displayProductDetails.php/";
        final CustomerWishList mCustomerFavorite = mCustomerWishList.get(position);
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
                holder.productName.setText(posts.get(Integer.parseInt(mCustomerFavorite.getProductId())-1).getTitle());
                Glide.with(context).load(posts.get(Integer.parseInt(mCustomerFavorite.getProductId())-1).getProductImage())
                        .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.productImage);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCustomerWishList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private ImageView productImage;
        private CustomerWishList mCustomerFavorite;

        public ViewHolder(@NonNull View itemView, final WishListAdapter.OnItemClickListener listener) {
            super(itemView);

            productImage = (ImageView) itemView.findViewById(R.id.ivProductWListImg);
            productName = (TextView) itemView.findViewById(R.id.tvProductWListName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCustomerFavorite = mCustomerWishList.get(getAdapterPosition());
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(Integer.parseInt(mCustomerFavorite.getProductId())-1);
                        }
                    }
                }
            });
        }
    }

}
