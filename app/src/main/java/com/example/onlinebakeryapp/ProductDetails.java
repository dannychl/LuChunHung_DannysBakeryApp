package com.example.onlinebakeryapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onlinebakeryapp.model.CustomerWishList;
import com.example.onlinebakeryapp.model.Product;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductDetails extends AppCompatActivity {

    private String loadProductDetailsURL = "http://192.168.8.107/cakeProduct/displayProductDetails.php/";
    private String loadUserWishListURL = "http://192.168.8.107";
    private ImageView mIvProduct, mIvLove, mIvBookingHeader, mIvDescriptionBg;
    private RatingBar mRatingBar;
    private TextView mTvDescriptionName, mTvDescriptionKg, mTvDescriptionPrice, mTvReviews, mTvRating, mIvDescriptionMessage;

    private TextInputLayout  mTMessage;
    private EditText mEtDescriptionMessage;
    private RadioGroup mRgProductKG;
    private RadioButton mRbOne, mRbOneFive, mRbTwo;
    private List<Product> mProductList;
    private List<CustomerWishList> mCustomerWishLists;
    private ProductAdapterRV adapter;
    private int position;
    private Animator currentAnimator;
    private int shortAnimationDuration, productID, custId;
    private double oneKgPrice = 10.00, oneFiveKgPrice = 15.00, twoKgPrice = 20.00, productKg = 0.0;
    private boolean oneKgChecked = false, oneFiveKgChecked = false, twoKgChecked = false;
    private Button mAddCart;
    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mIvProduct = findViewById(R.id.ivProductImg);
        mRatingBar = findViewById(R.id.rating);
        mTvDescriptionName = findViewById(R.id.tvProductName);
        mTvDescriptionPrice = findViewById(R.id.tvDescriptionPrice);
        mEtDescriptionMessage = findViewById(R.id.etDescriptionMessage);
        mTvReviews = findViewById(R.id.tvReviews);
        mIvLove = findViewById(R.id.ivLove);
        mTvRating = findViewById(R.id.tvRating);
        mIvBookingHeader = findViewById(R.id.bookingHeader);
        mIvDescriptionBg = findViewById(R.id.ivDescriptionBg);
        mTMessage = findViewById(R.id.tvMessage);
        mRgProductKG = findViewById(R.id.rgProductKG);
        mRbOne = findViewById(R.id.oneKg);
        mRbOneFive = findViewById(R.id.oneFiveKg);
        mRbTwo = findViewById(R.id.twoKg);
        mAddCart = findViewById(R.id.addCart);

        connectionClass = new ConnectionClass();

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("Position"));
        custId = Integer.parseInt(intent.getStringExtra("CustId"));


        updateKgPrice();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(loadProductDetailsURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<Product>> displayProduct = jsonPlaceHolderApi.getProducts();

        displayProduct.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code "+ response.code();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mProductList = response.body();
                productID = mProductList.get(position).getId();
                mTvDescriptionName.setText(mProductList.get(position).getTitle());
                mTvDescriptionPrice.setText(mProductList.get(position).getPrice());
                Glide.with(getApplicationContext()).load(mProductList.get(position).getProductImage()).override(400, 300).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mIvProduct);

                checkWishList();



                mIvProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomImageFromThumb(mIvProduct);
                    }
                });
                shortAnimationDuration= getResources().getInteger(android.R.integer.config_shortAnimTime);

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });

        mAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCart();
                onBackPressed();
            }
        });



    }

    private void checkWishList() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(loadUserWishListURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerWishList>> displayWishListProduct =
                jsonPlaceHolderApi.checkUserWishList("GET", String.valueOf(custId), String.valueOf(productID));

        displayWishListProduct.enqueue(new Callback<List<CustomerWishList>>() {
           @Override
           public void onResponse(Call<List<CustomerWishList>> call, Response<List<CustomerWishList>> response) {
               if (!response.isSuccessful()) {
                   String error = "Code "+ response.code();
                   Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                   return;
               }
               mCustomerWishLists = response.body();

               if (mCustomerWishLists.size() != 0 && String.valueOf(mCustomerWishLists.size())!=null){
                   if (mCustomerWishLists.get(0).getWishListStatus().equals("1"))
                       mIvLove.setBackgroundResource(R.drawable.loveiconred);
               }
               else
                   mIvLove.setBackgroundResource(R.drawable.loveicon);


                mIvLove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //insert wishlist item details when item in database not existed (new favorite wish list)
                        if (String.valueOf(mCustomerWishLists.size())==null || String.valueOf(mCustomerWishLists.size()).equals("0")){
                            updateWishListDetails("2");
                            mIvLove.setBackgroundResource(R.drawable.loveiconred);
                            Snackbar.make(view, getString(R.string.item_added),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                // item in database existed and check the status whether is 0, if yes update to 1 and set into favourite list (favorite wish list)
                else if(mCustomerWishLists.get(0).getWishListStatus().equals("0")){
                    mIvLove.setBackgroundResource(R.drawable.loveiconred);
                    updateWishListDetails("1");
                    Snackbar.make(view, getString(R.string.item_added),
                            Snackbar.LENGTH_SHORT).show();
                }
                // item in database exited and check the status whether is 1, if yes update to 0 and set into unfavourite list (non-favorite wish list anymore)
                else{
                    mIvLove.setBackgroundResource(R.drawable.loveicon);
                    updateWishListDetails("0");
                    Snackbar.make(view, getString(R.string.item_removed),
                            Snackbar.LENGTH_SHORT).show();
                }
                checkWishList();
            }
        });
       }

               @Override
               public void onFailure(Call<List<CustomerWishList>> call, Throwable t) {

               }
           });
    }
    private void updateWishListDetails(String status) {

        final String favoriteStatus = "1", unfavoriteStatus = "0";
        switch (status){
            case "0":{
                updateWishListStatus(unfavoriteStatus);
            }
            break;
            case "1":{
                updateWishListStatus(favoriteStatus);
            }
            break;
            case "2":{
                String z = "";
                boolean isSuccess = false;

                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        String query= "insert into customer_wishlist (custId, productId, wishlistStatus) VALUES ("+ custId +", "+ productID +","+ favoriteStatus + ")";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        z = "Add to Wish List Successfully";
                        isSuccess = true;
                    }
                }catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions" + ex;
                }

                if(isSuccess) {
                    Toast.makeText(ProductDetails.this, z, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateWishListStatus(String wishListStatus){
        String z = "";
        boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "update customer_wishlist SET wishlistStatus = " + wishListStatus + " WHERE productId = "+ productID +"";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(!isSuccess) {
            Toast.makeText(ProductDetails.this, z, Toast.LENGTH_SHORT).show();
        }
}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        finish();
    }

    private void addCart() {
        String z = "", messageOnCake = "";
        boolean isSuccess = false;

        if(mEtDescriptionMessage.getText().toString().equals("")||mEtDescriptionMessage.getText().toString() == null){
            messageOnCake = "";
        }else{
            messageOnCake = mEtDescriptionMessage.getText().toString();
        }

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "insert into customer_cart(custId, productId, product_kg, cakeOnMessage, price, status) VALUES ("+ custId +", "+ productID +","+ productKg +
                        ",'"+messageOnCake+"','"+mTvDescriptionPrice.getText().toString()+"','"+"NEW"+"')";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                z = "Add to Cart Successfully";
                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(isSuccess) {
            Toast.makeText(ProductDetails.this, z, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateKgPrice(){
        oneKgChecked = true;
        productKg = 1.0;

        mRgProductKG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(radioGroup.getCheckedRadioButtonId()){
                    case R.id.oneKg: {
                        oneKgChecked = true;
                        productKg = 1.0;
                        if (oneFiveKgChecked) {
                            oneFiveKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) - oneFiveKgPrice;
                            mTvDescriptionPrice.setText(String.format("%.2f",price));
                        } else if (twoKgChecked) {
                            twoKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) - twoKgPrice;
                            mTvDescriptionPrice.setText(String.format("%.2f",price));
                        }
                        Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) + oneKgPrice;
                        mTvDescriptionPrice.setText(String.format("%.2f",price));
                    }
                    break;

                    case R.id.oneFiveKg: {
                        oneFiveKgChecked = true;
                        productKg = 1.5;
                        if (oneKgChecked) {
                            oneKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) - oneKgPrice;
                            mTvDescriptionPrice.setText(String.format("%.2f",price));
                        } else if (twoKgChecked) {
                            twoKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) - twoKgPrice;
                            mTvDescriptionPrice.setText(String.format("%.2f",price));
                        }
                        Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) + oneFiveKgPrice;
                        mTvDescriptionPrice.setText(String.format("%.2f",price));
                    }
                    break;

                    case R.id.twoKg:{
                        twoKgChecked = true;
                        productKg = 2.0;
                        if (oneKgChecked) {
                            oneKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) - oneKgPrice;
                            mTvDescriptionPrice.setText(String.format("%.2f",price));
                        } else if (oneFiveKgChecked) {
                            oneFiveKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) - oneFiveKgPrice;
                            mTvDescriptionPrice.setText(String.format("%.2f",price));
                        }
                        Double price = Double.parseDouble(mTvDescriptionPrice.getText().toString()) + twoKgPrice;
                        mTvDescriptionPrice.setText(String.format("%.2f",price));
                    }
                }
            }
        });
    }


    @SuppressLint("Range")
    private void zoomImageFromThumb(final View thumbView){
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expandedProductImg);

        Glide.with(getApplicationContext()).load(mProductList.get(position).getProductImage()).override(400, 300).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(expandedImageView);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.Product)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(10f);

        expandedImageView.setVisibility(View.VISIBLE);
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
