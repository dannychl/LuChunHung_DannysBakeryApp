package com.example.onlinebakeryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinebakeryapp.model.CustomerTransaction;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionHistoryDetails extends AppCompatActivity {

    private String displayUserOrderDetailsURL = "http://192.168.8.107";
    private TextView mTvPaymentCartView, mTvDateOrdered, mTvTimeOrdered, mTvOrderDescriptions, mTvOrderRm,
            mTvTotalPaymentOrderDetails, mTvTotalPaymentOrderDetailsAmt, mTvTotalPaymentDiscOrderDetails,
            mTvTotalPaymentDiscOrderDetailsAmt, mTvPaymentType;
    private ImageView mIvOrderDetailsBg, mIvDateOrdered, mIvTimeOrdered, mIvOrderDescriptionBg;
    private SharedPreferences prf;
    private String date, time;
    private RecyclerView rV;
    private PaymentPerTransactionAdapter adapter;
    private List<CustomerTransaction> mCustomerTransactionList;
    private double totalPrice = 0.0, discountRate = 0.06;
    private String dateString= "", timeString = "", hour = "", min = "", amPm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_details);

        getSupportActionBar().setTitle("My Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        mTvDateOrdered = findViewById(R.id.tvDateTransactionHistory);
        mTvTimeOrdered = findViewById(R.id.tvTimeTransactionHistory);
        mTvPaymentType = findViewById(R.id.tvPaymentTransactionHistory);
        mTvOrderDescriptions = findViewById(R.id.tvOrderDescriptionsTransactionHistory);
        mTvOrderRm = findViewById(R.id.tvOrderRMTransactionHistory);
        mTvTotalPaymentOrderDetails = findViewById(R.id.tvTotalPaymentODTransactionHistory);
        mTvTotalPaymentDiscOrderDetailsAmt = findViewById(R.id.tvTotalPaymentDiscODTransactionHistoryAmt);
        mTvTotalPaymentOrderDetailsAmt = findViewById(R.id.tvTotalPaymentODTransactionHistoryAmt);
        mTvTotalPaymentDiscOrderDetails = findViewById(R.id.tvTotalPaymentDiscODTransactionHistory);

        mIvOrderDescriptionBg = findViewById(R.id.ivOrderDescriptionBg);
        mIvDateOrdered = findViewById(R.id.ivDateOrdered);
        mIvTimeOrdered = findViewById(R.id.ivTimeOrdered);
        mIvOrderDetailsBg = findViewById(R.id.ivOrderDetailsBg);


        prf = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");


        rV = findViewById(R.id.recyclerView);
        rV.setHasFixedSize(true);
        rV.setLayoutManager(new LinearLayoutManager(this));
        rV.setAdapter(adapter);


        ViewCompat.setNestedScrollingEnabled(rV, false);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(displayUserOrderDetailsURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerTransaction>> displayUserTransactionHistory = jsonPlaceHolderApi
                .getUserTransactionHis("GET", prf.getString("Id", null), "\""+date+"\"", "\""+time+"\"");

        displayUserTransactionHistory.enqueue(new Callback<List<CustomerTransaction>>() {
            @Override
            public void onResponse(Call<List<CustomerTransaction>> call, Response<List<CustomerTransaction>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                mCustomerTransactionList = response.body();

                adapter = new PaymentPerTransactionAdapter(getApplicationContext(), mCustomerTransactionList);
                rV.setHasFixedSize(true);
                rV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                ViewCompat.setNestedScrollingEnabled(rV, false);

                for (int i = 0; i < mCustomerTransactionList.size(); i++) {
                    totalPrice += Double.parseDouble(mCustomerTransactionList.get(i).getPrice());
                }

                mTvPaymentType.setText("Payment Type: "+ mCustomerTransactionList.get(mCustomerTransactionList.size() - 1).getPaymentType());

                dateString = date;
                timeString = time;

                setDateTime();

                mTvTotalPaymentOrderDetailsAmt.setText(String.format("%.2f", totalPrice));
                mTvTotalPaymentDiscOrderDetailsAmt.setText(String.format("%.2f", totalPrice - (totalPrice * discountRate)));
            }

            @Override
            public void onFailure(Call<List<CustomerTransaction>> call, Throwable t) {

            }
        });
    }

    private void setDateTime() {

        hour = timeString.substring(0, 2);

        if (Integer.parseInt(hour) >= 12)
            amPm = "PM";
        else
            amPm = "AM";

        mTvDateOrdered.setText("Date Ordered: "+ dateString);
        mTvTimeOrdered.setText("Time Ordered: "+ timeString+" " +amPm);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}