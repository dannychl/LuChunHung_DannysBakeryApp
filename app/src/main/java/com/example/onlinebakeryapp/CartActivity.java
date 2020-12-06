package com.example.onlinebakeryapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinebakeryapp.model.CustomerCart;
import com.example.onlinebakeryapp.model.Product;
import com.example.onlinebakeryapp.ui.AccountFragment;
import com.example.onlinebakeryapp.ui.PaymentFragment;
import com.example.onlinebakeryapp.ui.ProductFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity {

    private String displayUserCartURL = "http://192.168.8.107";
    private CartAdapter adapter;
    private RecyclerView rV;
    private List<Product> mProductList;
    private TextView mTextView, mTvSubtotalPrice;
    private Button mDeleteProduct;
    private SharedPreferences prf;
    private List<CustomerCart> mCustomerCartList;
    private ConnectionClass connectionClass;
    private ImageButton mImgButtonPayment;
    private double totalPrice = 0.0;
    private int LAUNCH_CART_DETAILS_ACTIVITY = 1;
    public static final int ACTIVITY_1 = 1001;
    private BottomNavigationView bottomNavigationView;
    private ProductFragment mProductFragment;
    private AccountFragment mAccountFragment;
    private PaymentFragment mPaymentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        mTextView = findViewById(R.id.tvCartView);
        mTextView.setVisibility(View.GONE);
        mDeleteProduct = findViewById(R.id.deleteProduct);
        connectionClass = new ConnectionClass();
        mTvSubtotalPrice = findViewById(R.id.tvSubtotalPrice);
        mImgButtonPayment = findViewById(R.id.imgButtonPayment);
        //bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        prf = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);

        rV = findViewById(R.id.recyclerView);
        rV.setHasFixedSize(true);
        rV.setLayoutManager(new LinearLayoutManager(this));
        rV.setAdapter(adapter);

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
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerCartList = response.body();
                adapter = new CartAdapter(getApplicationContext(),mCustomerCartList, 0);
                rV.setHasFixedSize(true);
                rV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                totalPrice = 0.0;
                for (int i = 0; i <mCustomerCartList.size(); i++){
                    totalPrice += Double.parseDouble(mCustomerCartList.get(i).getPrice());
                }
                mTvSubtotalPrice.setText("Subtotal: RM"+String.format("%.2f", totalPrice));

                ViewCompat.setNestedScrollingEnabled(rV, false);

                adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(CartActivity.this, ProductCartDetails.class);
                        String itemPosition = String.valueOf(position);
                        String productId = mCustomerCartList.get(position).getProductId();
                        intent.putExtra("CustId", prf.getString("Id", null));
                        intent.putExtra("Position", itemPosition);
                        intent.putExtra("ProductId", productId);
                        startActivityForResult(intent, LAUNCH_CART_DETAILS_ACTIVITY);
                    }

                    @Override
                    public void onItemDelete(final int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this, R.style.MyAlertDialogStyle);
                        StringBuilder strBuilder = new StringBuilder();
                        builder.setTitle("Are you sure want to remove this from cart?");

                        builder.setMessage(strBuilder.toString());
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCartItem(Integer.parseInt(mCustomerCartList.get(position).getCartId()));
                                refreshCart();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        builder.show();
                    }
                });

                if (mCustomerCartList.size()<1){
                    mTextView.setVisibility(View.VISIBLE);
                    mImgButtonPayment.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });

        mImgButtonPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this, R.style.MyAlertDialogStyle);
                StringBuilder strBuilder = new StringBuilder();
                builder.setTitle("Click \'OK\' to continue");

                builder.setMessage(strBuilder.toString());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateCartStatus();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CartActivity.this, "Call Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    private void updateCartStatus(){
        String z = "";
        boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "update customer_cart SET status = '"+ "Pending"+ "' where custId = " + prf.getString("Id",null) + " AND status = '"+ "NEW" +"';";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                z = "Click place order to proceed the payment";
                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(isSuccess) {
            Toast.makeText(CartActivity.this, z, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent (CartActivity.this, MainActivity2.class);
            intent.putExtra("CallingActivity", CartActivity.ACTIVITY_1);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        finish();
    }

    private void deleteCartItem(int cartId) {
        String z = "";
        boolean isSuccess = false;
        CartAdapter adapter;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "delete from customer_cart where cartId = " + cartId + ";";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                z = "Delete Item Successfully";
                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(isSuccess) {
            Toast.makeText(CartActivity.this, z, Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshCart() {

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
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerCartList = response.body();
                adapter = new CartAdapter(getApplicationContext(),mCustomerCartList, 0);
                rV.setHasFixedSize(true);
                rV.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                totalPrice = 0.0;
                for (int i = 0; i <mCustomerCartList.size(); i++){
                    totalPrice += Double.parseDouble(mCustomerCartList.get(i).getPrice());
                }
                mTvSubtotalPrice.setText("Subtotal: RM"+ String.format("%.2f", totalPrice));

                adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(CartActivity.this, ProductCartDetails.class);
                        String itemPosition = String.valueOf(position);
                        String productId = mCustomerCartList.get(position).getProductId();
                        intent.putExtra("CustId", prf.getString("Id", null));
                        intent.putExtra("Position", itemPosition);
                        intent.putExtra("ProductId", productId);
                        startActivityForResult(intent, LAUNCH_CART_DETAILS_ACTIVITY);
                    }

                    @Override
                    public void onItemDelete(final int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this, R.style.MyAlertDialogStyle);
                        StringBuilder strBuilder = new StringBuilder();
                        builder.setTitle("Are you sure want to remove this from cart?");

                        builder.setMessage(strBuilder.toString());
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCartItem(Integer.parseInt(mCustomerCartList.get(position).getCartId()));
                                refreshCart();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        builder.show();
                    }
                });

                if (mCustomerCartList.size()<1){
                    mTextView.setVisibility(View.VISIBLE);
                    mImgButtonPayment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_CART_DETAILS_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                refreshCart();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}