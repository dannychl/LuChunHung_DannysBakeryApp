package com.example.onlinebakeryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etPassword,etNewPass,etConfirm;
    private Button btnSubmit;
    private SharedPreferences prf;
    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        prf = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        connectionClass = new ConnectionClass();

        etPassword = findViewById(R.id.etCurrentPassword);
        etNewPass = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnSubmit = findViewById(R.id.tvAccPasswordSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(etPassword.getText().toString())){

                    Toast.makeText(ChangePasswordActivity.this, "Current Password cannot empty!", Toast.LENGTH_SHORT).show();

                }else  if (TextUtils.isEmpty(etNewPass.getText().toString())){

                    Toast.makeText(ChangePasswordActivity.this, "New Password cannot empty!", Toast.LENGTH_SHORT).show();

                }else  if (TextUtils.isEmpty(etConfirm.getText().toString())){

                    Toast.makeText(ChangePasswordActivity.this, "Confirm Password cannot empty!", Toast.LENGTH_SHORT).show();

                }else if (prf.getString("password",null).equals(etNewPass.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this, "Current Password and New Password are identical", Toast.LENGTH_SHORT).show();
                }
                else if (!(etNewPass.getText().toString().equals(etConfirm.getText().toString()))){
                    Toast.makeText(ChangePasswordActivity.this, "New Password and Re-Type password not match!", Toast.LENGTH_SHORT).show();

                }else if (!(prf.getString("password", null).equals(etPassword.getText().toString()))){

                    Toast.makeText(ChangePasswordActivity.this, "Wrong Current Password!", Toast.LENGTH_SHORT).show();

                }else {
                    updateUserPassword();
                }
            }
        });


    }

    private void updateUserPassword() {

        String z = "";
        boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "update customer_acc SET password = '"+ etNewPass.getText().toString()+ "' where id = " + prf.getString("Id",null)+";";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                z = "Password Update Successfully";
                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(isSuccess) {
            Toast.makeText(ChangePasswordActivity.this, z, Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = prf.edit();
            editor.putString("password", etNewPass.getText().toString());
            editor.commit();
            Intent intent = new Intent (ChangePasswordActivity.this, MainActivity2.class);
            intent.putExtra("CallingActivity", TransactionHistoryActivity.ACTIVITY_3);
            startActivity(intent);
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
        finish();
    }
}