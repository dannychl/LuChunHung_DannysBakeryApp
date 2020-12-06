package com.example.onlinebakeryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    EditText namestr, emailstr, passstr;
    Button register;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;
    private String displayAllUsername= "http://192.168.8.107/cakeProduct/displayUsername.php/";
    private List<Customer> getCustomerUsername;
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        namestr = (EditText) findViewById(R.id.username);
        emailstr = (EditText) findViewById(R.id.email);
        passstr = (EditText) findViewById(R.id.pass);
        register = (Button) findViewById(R.id.register);

        connectionClass = new ConnectionClass();

        validateName();

        progressDialog = new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Doregister doregister = new Doregister();
                        doregister.execute("");
            }
        });
    }


    public class Doregister extends AsyncTask<String, String, String> {

        String username = namestr.getText().toString();
        String email = emailstr.getText().toString();
        String password = passstr.getText().toString();
        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            final String blockCharacterSet = "~#^|$%&*!";

            message = "";
            message = checkAllUsernameMatch(username);

            if (username.trim().equals("") || email.trim().equals("") || password.trim().equals(""))
                z = "Please enter all fields....";
            else if (message.equals("Username already used....")) {
                z = "Username already used....";
            } else if (username.trim().length() > 9)
                z = "Username only 9 character allowed...";
            else if (username.contains(blockCharacterSet))
                z = "Special character (~#^|$%&*!) not allowed";
            else if (!email.matches(emailPattern)) {
                z = "Invalid email address...";
            }
            else if (emailstr.length() > 254) {
                z = "Email exceed maximum limit... ";
            }
            else{
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection bah";
                    } else {

                        String query = "insert into customer_acc (username, email, password) values('"+ username + "','" + email + "','" + password + "')";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        z = "Register successful";
                        isSuccess = true;
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions" + ex;
                }
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(getBaseContext(), "" + z, Toast.LENGTH_LONG).show();

            if (isSuccess) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }


            progressDialog.hide();
        }
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
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                getCustomerUsername = response.body();
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {

            }

        });
    }


    public void goto_login(View view) {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }
}
