package com.example.onlinebakeryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onlinebakeryapp.model.Product;

import java.util.List;

public class ProductAdapterRV extends RecyclerView.Adapter<ProductAdapterRV.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private List<Product> productList;
    Context context;
    LayoutInflater inflater;
    private OnItemClickListener mListener;

    public ProductAdapterRV (Context context, List<Product>productList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cake_list, parent,false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product mProduct = productList.get(position);
        holder.productTitle.setText(mProduct.getTitle());
        holder.productQuantity.setText("Quantity: "+ mProduct.getProductQuantity());
        holder.productPrice.setText("RM "+ mProduct.getPrice());
        Glide.with(context).load(mProduct.getProductImage()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.productImage);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView productTitle, productPrice, productQuantity;
        private ImageView productImage;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            productTitle = (TextView) itemView.findViewById(R.id.tvCakeName);
            productPrice = (TextView) itemView.findViewById(R.id.tvCakePrice);
            productQuantity = (TextView) itemView.findViewById(R.id.tvCakeQuantity);
            productImage = (ImageView) itemView.findViewById(R.id.imgCake);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
