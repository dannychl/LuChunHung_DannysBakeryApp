package com.example.onlinebakeryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinebakeryapp.model.Customer;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class ReloadActivity extends AppCompatActivity {


    private TextView mTvWalletBalance, mTvWalletAmount;
    private EditText mEtAmount;
    private Button mReloadWallet;
    private SharedPreferences prf;
    private String cashHolder;
    private List<Customer>mCustomerList;
    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);

        getSupportActionBar().setTitle("Reload ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        mTvWalletAmount = findViewById(R.id.tvWalletAmount);
        mTvWalletBalance = findViewById(R.id.tvWalletBalance);
        mReloadWallet = findViewById(R.id.reloadWallet);
        mEtAmount = findViewById(R.id.etAmount);
        connectionClass = new ConnectionClass();

        prf = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        double cash  = Double.parseDouble(prf.getString("cash",null));
        mTvWalletAmount.setText("RM "+ String.format("%.2f", cash));

        mReloadWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEtAmount.getText().toString())) {
                    Toast.makeText(ReloadActivity.this, "Please enter amount!", Toast.LENGTH_SHORT).show();
                } else {
                    String currentCash = prf.getString("cash", null);

                    double cash = Double.parseDouble(mEtAmount.getText().toString()) + Double.parseDouble(currentCash);
                    cashHolder = String.valueOf(cash);

                    SharedPreferences.Editor editor = prf.edit();
                    editor.putString("cash", cashHolder);
                    editor.commit();

                    updateCash(prf.getString("Id", null));

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", mEtAmount.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private void updateCash(String id){

        String z = "";
        boolean isSuccess = false;
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query =  "UPDATE customer_acc SET cash = " + cashHolder + " WHERE id= " + id +";";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                z = "Cash Updated Successfully";
                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(isSuccess) {
            Toast.makeText(ReloadActivity.this, z, Toast.LENGTH_SHORT).show();
        }
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
        Intent intent = new Intent (ReloadActivity.this, MainActivity2.class);
        startActivity(intent);
    }

}