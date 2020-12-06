package com.example.onlinebakeryapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinebakeryapp.CartActivity;
import com.example.onlinebakeryapp.Converter;
import com.example.onlinebakeryapp.CustomerWishListDetails;
import com.example.onlinebakeryapp.model.Customer;
import com.example.onlinebakeryapp.model.CustomerCart;
import com.example.onlinebakeryapp.JsonPlaceHolder;
import com.example.onlinebakeryapp.model.CustomerWishList;
import com.example.onlinebakeryapp.model.Product;
import com.example.onlinebakeryapp.ProductAdapterRV;
import com.example.onlinebakeryapp.ProductDetails;
import com.example.onlinebakeryapp.R;
import com.example.onlinebakeryapp.ReloadActivity;

import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductFragment extends Fragment {

    private String loadCashURL = "http://192.168.8.107";
    private String loadUserWishListURL = "http://192.168.8.107";
    private String loadProductURL = "http://192.168.8.107/cakeProduct/loadProduct.php/";
    private String displayUserCartURL = "http://192.168.8.107";

    private RecyclerView rV;
    private List<Product> mProductList;
    private List<Customer> mCustomerList;
    private List<CustomerCart> mCustomerCartList;
    private List<CustomerWishList> mCustomerWishLists;
    ProductAdapterRV adapter;
    TextView mTvPrice;
    Button mReloadWallet;
    ImageView mHomeList, mHomeCart;
    private int LAUNCH_SECOND_ACTIVITY = 1;
    private static int cartCount = 0, wishListCount = 0;
    private String cashHolder;
    HashMap<String, String> ResultHash = new HashMap<>();
    private SharedPreferences prf;
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cakes, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getContext();

        rV = view.findViewById(R.id.recyclerView);
        mTvPrice = view.findViewById(R.id.tvPrice);
        mReloadWallet = view.findViewById(R.id.btnReload);
        mHomeList = view.findViewById(R.id.homeWishList);
        mHomeCart = view.findViewById(R.id.homeCart);

        prf = this.getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);


        cartCounter();
        wishListCounter();

        updateCash(prf.getString("Id", null));

        mReloadWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ReloadActivity.class);
                updateCash(prf.getString("Id", null));
                i.putExtra("cash", cashHolder);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });

        mHomeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CartActivity.class);
                startActivity(i);
            }
        });

        mHomeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (getActivity(), CustomerWishListDetails.class);
                startActivity(i);
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(loadProductURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<List<Product>> call = jsonPlaceHolder.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                mProductList = response.body();
                adapter = new ProductAdapterRV(getActivity(), mProductList);
                rV.setHasFixedSize(true);
                rV.setLayoutManager(new GridLayoutManager(getContext(), 2));
                rV.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                ViewCompat.setNestedScrollingEnabled(rV, false);

                adapter.setOnItemClickListener(new ProductAdapterRV.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), ProductDetails.class);
                        String itemPosition = String.valueOf(position);
                        intent.putExtra("CustId", prf.getString("Id", null));
                        intent.putExtra("Position", itemPosition);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                String error = t.getMessage();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void cartCounter() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(displayUserCartURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);
        Call<List<CustomerCart>> displayUserCart = jsonPlaceHolderApi.getUserCart("GET", prf.getString("Id", null));
        displayUserCart.enqueue(new Callback<List<CustomerCart>>() {

            @Override
            public void onResponse(Call<List<CustomerCart>> call, Response<List<CustomerCart>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code "+ response.code();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                mCustomerCartList = response.body();
                cartCount = mCustomerCartList.size();
                mHomeCart.setImageDrawable(Converter.convertLayoutToImage(getActivity(), cartCount, "Cart"));
            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });
    }

    private void wishListCounter(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(loadUserWishListURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerWishList>> displayUserCart = jsonPlaceHolderApi.getUserWishList("GET", prf.getString("Id", null));

        displayUserCart.enqueue(new Callback<List<CustomerWishList>>() {

            @Override
            public void onResponse(Call<List<CustomerWishList>> call, Response<List<CustomerWishList>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code "+ response.code();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                mCustomerWishLists = response.body();
                wishListCount = mCustomerWishLists.size();
                mHomeList.setImageDrawable(Converter.convertLayoutToImage(getContext(), wishListCount, "WishList"));
            }

            @Override
            public void onFailure(Call<List<CustomerWishList>> call, Throwable t) {

            }
        });
    }

    private void updateCash(String id) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(loadCashURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<Customer>> listCall = jsonPlaceHolderApi.getUserCash("GET", id);

        listCall.enqueue(new Callback<List<Customer>>() {

            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerList = response.body();
                if (!mCustomerList.isEmpty() && mCustomerList != null) {
                    cashHolder = mCustomerList.get(0).getCash();
                    mTvPrice.setText("RM " + cashHolder);
                    SharedPreferences.Editor editor = prf.edit();
                    editor.putString("cash", cashHolder);
                    editor.commit();
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                String currentString = mTvPrice.getText().toString();
                String[] separated = currentString.split(" ");
                double cash = Double.parseDouble(result) + Double.parseDouble(separated[1]);

                mTvPrice.setText("RM " + String.format("%.2f",cash));
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }
}