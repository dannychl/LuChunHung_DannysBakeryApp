package com.example.onlinebakeryapp.ui;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinebakeryapp.CartAdapter;
import com.example.onlinebakeryapp.ConnectionClass;
import com.example.onlinebakeryapp.JsonPlaceHolder;
import com.example.onlinebakeryapp.OrderDetails;
import com.example.onlinebakeryapp.R;
import com.example.onlinebakeryapp.model.CustomerCart;

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

public class PaymentFragment extends Fragment {

    private String displayUserCartURL = "http://192.168.8.107";
    private RecyclerView rV;
    private TextView mTextView, mTvOrderSummary, mTvPaymentRm, mTvPaymentSubtotal, mTvSubtotalAmt,
            mTvPaymentDiscount, mTvSubtotalDiscountAmt, mTvTotalPayment, mTvTotalPaymentAmt;
    private ImageView mIvSubtotalBg, mIvOrderDetailsBg, mIvNextOrderDetails;
    private Button mPlaceOrder, mOrderDetails;
    private CartAdapter adapter;
    private List<CustomerCart> mCustomerCartList;
    private SharedPreferences prf;
    public static final int ACTIVITY_1 = 1000;
    private double totalPrice = 0.0, discountRate = 0.06;
    private ConnectionClass connectionClass;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getContext();

        prf = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);

        mTextView = view.findViewById(R.id.tvPaymentCartView);
        mTextView.setVisibility(View.GONE);
        mTvOrderSummary = view.findViewById(R.id.tvOrderSummary);
        mTvPaymentRm = view.findViewById(R.id.tvPaymentRM);
        mTvPaymentSubtotal = view.findViewById(R.id.tvPaymentSubtotal);
        mTvSubtotalAmt = view.findViewById(R.id.tvSubtotalAmt);
        mTvPaymentDiscount = view.findViewById(R.id.tvPaymentDiscount);
        mTvSubtotalDiscountAmt = view.findViewById(R.id.tvSubtotalDiscountAmt);
        mTvTotalPayment = view.findViewById(R.id.totalPayment);
        mTvTotalPaymentAmt = view.findViewById(R.id.tvTotalPaymentAmt);
        mIvSubtotalBg = view.findViewById(R.id.ivSubtotalBg);
        mPlaceOrder = view.findViewById(R.id.placeOrder);
        mOrderDetails = view.findViewById(R.id.orderDetails);
        mIvNextOrderDetails = view.findViewById(R.id.ivNextOrderDetails);
        mIvOrderDetailsBg = view.findViewById(R.id.ivOrderDetailsBg);

        mPlaceOrder.setVisibility(View.GONE);
        mIvNextOrderDetails.setVisibility(View.GONE);
        mIvOrderDetailsBg.setVisibility(View.GONE);
        mOrderDetails.setVisibility(View.GONE);

        connectionClass = new ConnectionClass();

        rV = view.findViewById(R.id.recyclerView);
        rV.setHasFixedSize(true);
        rV.setLayoutManager(new LinearLayoutManager(getActivity()));
        rV.setAdapter(adapter);

        validateUserOrderDetails();
        displayPaymentCart();

        mOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OrderDetails.class);
                startActivity(intent);
            }
        });

        mPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserCash();
            }
        });


        return view;
    }

    private void validateUserCash() {

        double userCash = Double.parseDouble(prf.getString("cash", null));
        double totalAmt = Double.parseDouble(mTvTotalPaymentAmt.getText().toString());

        if (String.valueOf(userCash) != null && totalAmt != 0.00 && userCash != 0.00) {
            if (userCash < totalAmt) {
                Toast.makeText(getActivity(), "Insufficient Balance, Please reload your wallet now", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                StringBuilder strBuilder = new StringBuilder();
                builder.setTitle("Press proceed to continue your order");

                builder.setMessage(strBuilder.toString());
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        insertOrderDetails();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        } else {
            Toast.makeText(getActivity(), "Insufficient Balance, Please reload your wallet now", Toast.LENGTH_SHORT).show();
        }

    }

    private void insertOrderDetails() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(displayUserCartURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerCart>> displayUserCart = jsonPlaceHolderApi.getUserOrderPlaced("GET", prf.getString("Id", null));

        displayUserCart.enqueue(new Callback<List<CustomerCart>>() {
            @Override
            public void onResponse(Call<List<CustomerCart>> call, Response<List<CustomerCart>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerCartList = response.body();

                for (int i =0; i<mCustomerCartList.size(); i++){
                    insertOrder(i, mCustomerCartList);
                }

            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });
    }

    private void insertOrder(int i, List<CustomerCart> mCustomerCartList) {

        String z = "";
        boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                //String query= "insert order_details SET status = '"+ "Confirm"+ "' where custId = " + prf.getString("Id",null) + " AND status = '"+ "Pending" +"';";

                String query= "insert into order_details(productId, price, status, custId, product_kg, cakeOnMessage) VALUES ("+ mCustomerCartList.get(i).getProductId()
                        +","+ mCustomerCartList.get(i).getPrice() +",'"+ "Confirm" +
                        "',"+prf.getString("Id", null)+","+mCustomerCartList.get(i).getProduct_kg()+",'"+ mCustomerCartList.get(i).getCakeOnMessage()+ "')";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(!isSuccess) {
            Toast.makeText(getActivity(), z, Toast.LENGTH_SHORT).show();
        }else{
            updateCartStatus();
        }
    }


    private void updateCartStatus() {
        String z = "";
        boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "update customer_cart SET status = '"+ "PendingConfirm"+ "' where custId = " + prf.getString("Id",null) + " AND status = '"+ "Pending" +"';";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(!isSuccess) {
            Toast.makeText(getActivity(), z, Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(getActivity(), OrderDetails.class);
            startActivity(intent);
        }

    }

    private void displayPaymentCart() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(displayUserCartURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerCart>> displayUserCart = jsonPlaceHolderApi.getUserCartPayment("GET", prf.getString("Id", null));

        displayUserCart.enqueue(new Callback<List<CustomerCart>>() {
            @Override
            public void onResponse(Call<List<CustomerCart>> call, Response<List<CustomerCart>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerCartList = response.body();
                adapter = new CartAdapter(getContext(), mCustomerCartList, ACTIVITY_1);
                rV.setHasFixedSize(true);
                rV.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                totalPrice = 0.0;
                for (int i = 0; i <mCustomerCartList.size(); i++){
                    totalPrice += Double.parseDouble(mCustomerCartList.get(i).getPrice());
                }
                mTvSubtotalAmt.setText(String.format("%.2f", totalPrice));
                mTvSubtotalDiscountAmt.setText(String.format("%.2f",totalPrice*discountRate));
                mTvTotalPaymentAmt.setText(String.format("%.2f", totalPrice-(totalPrice*discountRate)));


                if (mCustomerCartList.size()>0) {
                    mPlaceOrder.setVisibility(View.VISIBLE);
                }else{
                    mTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });
    }

    private void validateUserOrderDetails() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(displayUserCartURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerCart>> displayUserCart = jsonPlaceHolderApi.getUserOrderDetails("GET", prf.getString("Id", null));

        displayUserCart.enqueue(new Callback<List<CustomerCart>>() {
            @Override
            public void onResponse(Call<List<CustomerCart>> call, Response<List<CustomerCart>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code "+ response.code();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                mCustomerCartList = response.body();

                if (mCustomerCartList.size()>0){
                    mIvNextOrderDetails.setVisibility(View.VISIBLE);
                    mIvOrderDetailsBg.setVisibility(View.VISIBLE);
                    mOrderDetails.setVisibility(View.VISIBLE);
                }else{
                    mTextView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });

    }

}