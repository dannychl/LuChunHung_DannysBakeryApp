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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.onlinebakeryapp.AccountActivity;
import com.example.onlinebakeryapp.ContactUsActivity;
import com.example.onlinebakeryapp.MainActivity;
import com.example.onlinebakeryapp.R;
import com.example.onlinebakeryapp.TransactionHistoryActivity;


public class AccountFragment extends Fragment{

    private TextView tvUser;
    private SharedPreferences prf;
    private String username;

    private ImageView mNextIconWlc, mNextIconContact, mNextIconTransHis, mNextIconLogOut;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);getContext();

        prf = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        tvUser = view.findViewById(R.id.tvUsername);

        username= prf.getString("username", null).toUpperCase();
        tvUser.setText(username);

        mNextIconWlc = view.findViewById(R.id.nextIconWlc);
        mNextIconContact= view.findViewById(R.id.nextIconContact);
        mNextIconTransHis= view.findViewById(R.id.nextIconTransHistory);
        mNextIconLogOut= view.findViewById(R.id.nextIconLogOut);

        mNextIconLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                StringBuilder strBuilder = new StringBuilder();
                builder.setTitle("Log Out?");

                builder.setMessage(strBuilder.toString());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE).edit().clear().apply();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
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
        });

        mNextIconWlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
            }
        });


        mNextIconContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(intent);
            }
        });

        mNextIconTransHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

}