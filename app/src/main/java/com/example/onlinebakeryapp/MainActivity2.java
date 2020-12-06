package com.example.onlinebakeryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.onlinebakeryapp.ui.PaymentFragment;
import com.example.onlinebakeryapp.ui.ProductFragment;
import com.example.onlinebakeryapp.ui.AccountFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ProductFragment mProductFragment;
    private AccountFragment mAccountFragment;
    private PaymentFragment mPaymentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        mProductFragment = new ProductFragment();
        mAccountFragment = new AccountFragment();
        mPaymentFragment = new PaymentFragment();

        BottomNavigationView btmView = findViewById(R.id.bottom_navigation);
        btmView.setOnNavigationItemSelectedListener(navListener);

        int callingActivity = getIntent().getIntExtra("CallingActivity", 0);

        if (callingActivity == CartActivity.ACTIVITY_1){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPaymentFragment).commit();
            btmView.getMenu().findItem(R.id.navigationPayment).setChecked(true);
        }
        else if (callingActivity == TransactionHistoryActivity.ACTIVITY_3){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mAccountFragment).commit();
            btmView.getMenu().findItem(R.id.navAccount).setChecked(true);
        }
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mProductFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navProduct:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mProductFragment).commit();
                    break;
                case R.id.navigationPayment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPaymentFragment).commit();
                    break;
                case R.id.navAccount:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mAccountFragment).commit();
                    break;
            }
            return true;
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder strBuilder = new StringBuilder();
        builder.setTitle("Exit now?");

        builder.setMessage(strBuilder.toString());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}