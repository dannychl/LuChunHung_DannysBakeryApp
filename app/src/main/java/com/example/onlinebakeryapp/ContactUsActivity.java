package com.example.onlinebakeryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ContactUsActivity extends AppCompatActivity {


    private ImageView mPhoneCall;
    private String phone = "0124109392";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));


        mPhoneCall = findViewById(R.id.ivPhoneClicked);

        mPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.isEmpty() || phone == null) {
                    Toast.makeText(ContactUsActivity.this, "Phone number is null", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactUsActivity.this);
                    StringBuilder strBuilder = new StringBuilder();
                    builder.setTitle("Make a phone call?");

                    builder.setMessage(strBuilder.toString());
                    builder.setPositiveButton("Call Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String s = "tel:" + phone;
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(s));
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(ContactUsActivity.this, "Call Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.show();
                }
            }
        });

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
        Intent intent = new Intent (ContactUsActivity.this, MainActivity2.class);
        intent.putExtra("CallingActivity", TransactionHistoryActivity.ACTIVITY_3);
        startActivity(intent);
    }
}