package com.example.onlinebakeryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onlinebakeryapp.model.CustomerCart;
import com.example.onlinebakeryapp.model.Product;
import com.example.onlinebakeryapp.ui.PaymentFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemDelete(int position);
    }

    private List<CustomerCart> mCustomerCartList;
    Context context;
    LayoutInflater inflater;
    private CartAdapter.OnItemClickListener mListener;
    private int callingActivity;

    public CartAdapter(Context ctx, List<CustomerCart> mCustomerCartList, int callingActivity){
        this.inflater = LayoutInflater.from(ctx);
        this.mCustomerCartList = mCustomerCartList;
        this.context = ctx;
        this.callingActivity = callingActivity;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list,parent,false);
        return new CartAdapter.ViewHolder(view, mListener);
    }

    public void setOnItemClickListener(CartAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.ViewHolder holder, int position) {

        String loadProductDetailsURL = "http://192.168.8.107/cakeProduct/displayProductDetails.php/";
        final CustomerCart mCustomerCart = mCustomerCartList.get(position);

        holder.productPrice.setText(mCustomerCart.getPrice());
        holder.productKg.setText("Weight: "+ mCustomerCart.getProduct_kg() + "kg");

        if (mCustomerCart.getCakeOnMessage()!=null && !mCustomerCart.getCakeOnMessage().equals("")){

            if (mCustomerCart.getCakeOnMessage().length()>14){
                holder.productMessage.setText("Message: " + mCustomerCart.getCakeOnMessage().substring(0, 13)+"...");
            }else{
                holder.productMessage.setText("Message: " + mCustomerCart.getCakeOnMessage());
            }
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
                holder.productName.setText(posts.get(Integer.parseInt(mCustomerCart.getProductId())-1).getTitle());
                Glide.with(context).load(posts.get(Integer.parseInt(mCustomerCart.getProductId())-1).getProductImage())
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
        return mCustomerCartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView productName, productPrice, productKg, productMessage;
        private ImageView productImage;
        private ImageButton mDeleteProduct;
        private CustomerCart mCustomerCart;

        public ViewHolder(@NonNull View itemView, final CartAdapter.OnItemClickListener listener) {
            super(itemView);

            productImage = (ImageView) itemView.findViewById(R.id.ivProductImg);
            productName = (TextView) itemView.findViewById(R.id.tvProductName);
            productPrice = (TextView) itemView.findViewById(R.id.tvDescriptionPrice);
            mDeleteProduct = (ImageButton) itemView.findViewById(R.id.deleteProduct);
            productMessage = itemView.findViewById(R.id.productMessage);
            productKg = itemView.findViewById(R.id.tvWeight);

            mDeleteProduct.setVisibility(View.GONE);

            if (callingActivity != PaymentFragment.ACTIVITY_1)
            {
                mDeleteProduct.setVisibility(View.VISIBLE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCustomerCart = mCustomerCartList.get(getAdapterPosition());
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            mDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCustomerCart = mCustomerCartList.get(getAdapterPosition());
                    if (listener!=null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemDelete(position);
                        }
                    }

                }
            });
        }

    }
}
