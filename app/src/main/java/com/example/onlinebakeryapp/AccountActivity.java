package com.example.onlinebakeryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinebakeryapp.model.Customer;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountActivity extends AppCompatActivity {

    private TextView mAccUsername;
    private ImageView mIvEditUsername;
    private EditText mEtAccUsername;
    private Button mBtnAccPassword, mBtnAccSave;
    private SharedPreferences prf;
    private String currentUsername, message = "", previousUsername;
    private ConnectionClass connectionClass;
    private List<Customer> getCustomerUsername;
    private String displayAllUsername= "http://192.168.8.107/cakeProduct/displayUsername.php/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        prf = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        connectionClass = new ConnectionClass();
        mAccUsername= findViewById(R.id.tvAccUsername);
        mIvEditUsername = findViewById(R.id.ivEditUsername);
        mEtAccUsername = findViewById(R.id.etAccUsername);
        mBtnAccPassword = findViewById(R.id.btnAccPassword);
        mBtnAccSave = findViewById(R.id.btnAccSave);


        mEtAccUsername.setText(prf.getString("username", null));
        mEtAccUsername.setEnabled(false);

        validateName();


        mIvEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEtAccUsername.setEnabled(true);
            }
        });

        mBtnAccPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (AccountActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        mBtnAccSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message = "";
                message = checkAllUsernameMatch(mEtAccUsername.getText().toString());
                previousUsername = mEtAccUsername.getText().toString();

                if (mEtAccUsername.getText().toString().equals("") || mEtAccUsername.getText().toString() == null) {
                    Toast.makeText(AccountActivity.this, "Username cannot be null", Toast.LENGTH_SHORT).show();
                }
                else if (message.equals("Username already used....")){
                    Toast.makeText(AccountActivity.this, "Username already used....", Toast.LENGTH_SHORT).show();
                }
                else if (mEtAccUsername.getText().toString().length()>12 || mEtAccUsername.getText().toString().contains(" ")) {
                        Toast.makeText(AccountActivity.this, "Username only allowed 12 characters & No Spaces Allowed", Toast.LENGTH_SHORT).show();
                }
                else if (mEtAccUsername.getText().toString().equals(prf.getString("username",null))){
                    Toast.makeText(AccountActivity.this, "Username Saved Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                else{
                    updateUsername();
                }
            }
        });


    }

    private String checkAllUsernameMatch(String updateName) {

        if (getCustomerUsername.size()>0){
            for (int i = 0; i<getCustomerUsername.size();i++){
                if (updateName.equals(getCustomerUsername.get(i).getUsername())){
                    message = "Username already used....";
                }
            }
        }
        return message;
    }

    private void validateName() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(displayAllUsername)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<Customer>> listCustomer = jsonPlaceHolderApi.getUsername();

        listCustomer.enqueue(new Callback<List<Customer>>() {

            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(AccountActivity.this, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                getCustomerUsername = response.body();
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {

            }

        });
    }

    private void updateUsername() {

        String z = "";
        boolean isSuccess = false;

            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {
                    String query = "update customer_acc SET username = '" + mEtAccUsername.getText().toString() + "' where id = " + prf.getString("Id", null) + ";";

                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);

                    z = "Username Saved Successfully";
                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions" + ex;
        }

        if(isSuccess) {
            Toast.makeText(AccountActivity.this, z, Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = prf.edit();
            editor.putString("username", mEtAccUsername.getText().toString());
            editor.commit();
            Intent intent = new Intent (AccountActivity.this, MainActivity2.class);
            intent.putExtra("CallingActivity", TransactionHistoryActivity.ACTIVITY_3);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent (AccountActivity.this, MainActivity2.class);
        intent.putExtra("CallingActivity", TransactionHistoryActivity.ACTIVITY_3);
        startActivity(intent);
    }
}