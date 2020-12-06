package com.example.onlinebakeryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinebakeryapp.model.CustomerWishList;
import com.example.onlinebakeryapp.model.Product;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerWishListDetails extends AppCompatActivity {

    private String loadUserWishListURL = "http://192.168.8.107";
    private String loadProductDetailsURL = "http://192.168.8.107/cakeProduct/displayProductDetails.php/";
    private ImageView mIvProductWListImg;
    private TextView mTvProductWListName, mTextView;
    private SharedPreferences prf;
    private List<CustomerWishList> mCustomerWishLists;
    private List<Product> mProductList;
    private WishListAdapter adapter;
    private RecyclerView rV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_wish_list);

        getSupportActionBar().setTitle("My Wish List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        prf = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);

        mIvProductWListImg = findViewById(R.id.ivProductWListImg);
        mTvProductWListName = findViewById(R.id.tvProductWListName);

        mTextView = findViewById(R.id.tvWishListView);
        mTextView.setVisibility(View.GONE);

        rV = findViewById(R.id.recyclerView);
        rV.setHasFixedSize(true);
        rV.setLayoutManager(new LinearLayoutManager(this));
        rV.setAdapter(adapter);


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(loadUserWishListURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerWishList>> displayWishListProduct = jsonPlaceHolderApi.getUserWishList("GET", prf.getString("Id", null));

        displayWishListProduct.enqueue(new Callback<List<CustomerWishList>>() {
            @Override
            public void onResponse(Call<List<CustomerWishList>> call, Response<List<CustomerWishList>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                mCustomerWishLists = response.body();
                adapter = new WishListAdapter(getApplicationContext(),mCustomerWishLists);
                rV.setHasFixedSize(true);
                rV.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adapter.setOnItemClickListener(new WishListAdapter.OnItemClickListener() {
                   @Override
                   public void onItemClick(int position) {
                       Intent intent = new Intent(CustomerWishListDetails.this, ProductDetails.class);
                       String itemPosition = String.valueOf(position);
                       intent.putExtra("CustId", prf.getString("Id", null));
                       intent.putExtra("Position", itemPosition);
                       startActivity(intent);
                   }
               });

                if (mCustomerWishLists.size()<1){
                    mTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<CustomerWishList>> call, Throwable t) {

            }
        });
    }
}