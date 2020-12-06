package com.example.onlinebakeryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onlinebakeryapp.ui.AccountFragment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



public class Login extends AppCompatActivity{

    public static final String SHARED_PRES = "username";

    AccountFragment mAccountFragment;
    EditText name,pass;
    Button login;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name= (EditText) findViewById(R.id.name);
        pass= (EditText) findViewById(R.id.pass);
        login= (Button) findViewById(R.id.login);

        pref = getSharedPreferences("user_details",MODE_PRIVATE);

        if (pref.getString("username",null) !=null && pref.getString("Id",null)!=null && pref.getString("password",null)!=null){
            Intent i = new Intent(Login.this, MainActivity2.class);
            startActivity(i);
        }

        connectionClass = new ConnectionClass();

        progressDialog=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("user_details", Context.MODE_PRIVATE).edit().clear().apply();
                Dologin dologin=new Dologin();
                dologin.execute();
            }
        });
    }

    private class Dologin extends AsyncTask<String,String,String> {

        String namestr=name.getText().toString();
        String passstr=pass.getText().toString();
        boolean isSuccess=false;
        int id;
        String nm,pw, z="";

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(namestr.trim().equals("") || passstr.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        String query=" select * from customer_acc where username = '"+namestr+"' and password = '"+passstr+"'";

                        Statement stmt = con.createStatement();
                        ResultSet rs=stmt.executeQuery(query);

                        while (rs.next())
                        {
                            id = rs.getInt(1);
                            nm = rs.getString(2);
                            pw = rs.getString(4);


                            if(nm.equals(namestr) && pw.equals(passstr))
                            {
                                isSuccess=true;
                                z = "Login successful";
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("username",namestr);
                                editor.putString("password",passstr);
                                editor.putString("Id",String.valueOf(id));
                                editor.commit();
                            }

                            else {
                                isSuccess = false;
                                z = "Invalid username or password";
                            }

                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions "+ex;
                }
            }
            return z;        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            if(isSuccess) {
                Intent i = new Intent(Login.this, MainActivity2.class);
                startActivity(i);
            }
            else{
                Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }

            progressDialog.hide();

        }
    }

    public void goto_sign(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}