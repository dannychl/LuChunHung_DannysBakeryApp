package com.example.onlinebakeryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class TransactionHistoryActivity extends AppCompatActivity {

    private String displayUserCartURL = "http://192.168.8.107";
    private SharedPreferences prf;
    private TransactionHistoryAdapter adapter;
    private RecyclerView rV;
    private List<CustomerTransaction> mCustomerTransactionList;
    private String previousDate, previousTime;
    private String [] date = {""}, time = {""}, price = {""};
    public static final int ACTIVITY_3 = 1003;
    private int countDate = 0, countPrice = 0, counter=0;
    private double totalPrice = 0.0;
    private TextView mTvTransactionHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        getSupportActionBar().setTitle("All Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        prf = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);


        mTvTransactionHistory = findViewById(R.id.tvTransactionHisView);
        mTvTransactionHistory.setVisibility(View.GONE);
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

        Call<List<CustomerTransaction>> displayUserCart = jsonPlaceHolderApi.getUserAllTransactionHis("GET", prf.getString("Id", null));

        displayUserCart.enqueue(new Callback<List<CustomerTransaction>>() {
            @Override
            public void onResponse(Call<List<CustomerTransaction>> call, Response<List<CustomerTransaction>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerTransactionList = response.body();

                if (mCustomerTransactionList.size()>0){
                    checkDuplicateDateTimeOrdered(mCustomerTransactionList);

                    adapter = new TransactionHistoryAdapter(getApplicationContext(), date, time, countDate+1, price);
                    rV.setHasFixedSize(true);
                    rV.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    adapter.setOnItemClickListener(new TransactionHistoryAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String date, String time) {
                            Intent intent = new Intent(TransactionHistoryActivity.this, TransactionHistoryDetails.class);
                            intent.putExtra("date", date);
                            intent.putExtra("time", time);
                            startActivity(intent);
                        }
                    });
                }else{
                    mTvTransactionHistory.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<CustomerTransaction>> call, Throwable t) {

            }
        });
    }


    private void checkDuplicateDateTimeOrdered(List<CustomerTransaction> customerTransactionList) {

        previousDate = customerTransactionList.get(0).getOrderDate();
        previousTime = customerTransactionList.get(0).getOrderTime();
        totalPrice = Double.parseDouble(customerTransactionList.get(0).getPrice());
        date = new String [mCustomerTransactionList.size()+1];
        time = new String [mCustomerTransactionList.size()+1];
        price = new String [mCustomerTransactionList.size()+1];

        date[countDate] = previousDate;
        time[countDate] = previousTime;

        for (int i=1; i<customerTransactionList.size(); i++){
            previousDate = customerTransactionList.get(i).getOrderDate();
            previousTime = customerTransactionList.get(i).getOrderTime();

            // enter when previous date not same with the current date get
            if (!previousDate.equals(date[countDate])){

                if (previousDate!=null){
                    countDate++;
                    date[countDate] = customerTransactionList.get(i).getOrderDate();
                    time[countDate] = customerTransactionList.get(i).getOrderTime();
                    price[countPrice] = String.format("%.2f",totalPrice);
                    countPrice++;
                    totalPrice= Double.parseDouble(customerTransactionList.get(i).getPrice());
                }

                // enter when previous date are same but previous time and current time get not same
            }else if (previousDate.equals(date[countDate]) && !previousTime.equals(time[countDate])){

                if (previousTime!=null){
                    countDate++;
                    date[countDate] = customerTransactionList.get(i).getOrderDate();
                    time[countDate] = customerTransactionList.get(i).getOrderTime();
                    price[countPrice] = String.format("%.2f",totalPrice);
                    countPrice++;
                    totalPrice= Double.parseDouble(customerTransactionList.get(i).getPrice());
                }

            }else{
                if (String.valueOf(totalPrice)!=null){
                    totalPrice += Double.parseDouble(customerTransactionList.get(i).getPrice());
                }
            }
        }
        price[countPrice] = String.format("%.2f",totalPrice);
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
        Intent intent = new Intent (TransactionHistoryActivity.this, MainActivity2.class);
        intent.putExtra("CallingActivity", TransactionHistoryActivity.ACTIVITY_3);
        startActivity(intent);
    }
}
