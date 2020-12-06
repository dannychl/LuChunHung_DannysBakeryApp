package com.example.onlinebakeryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onlinebakeryapp.model.CustomerCart;
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

public class ProductCartDetails extends AppCompatActivity {

    private String loadProductDetailsURL = "http://192.168.8.107/cakeProduct/displayProductDetails.php/";
    private String displayUserCartURL = "http://192.168.8.107";
    private String loadUserWishListURL = "http://192.168.8.107";
    private ImageView mIvCartProduct, mIvCartLove, mIvBookingHeader, mIvDescriptionCartBg;
    private RatingBar mRatingBarCart;
    private TextView mTvDescriptionCartName, mTvDescriptionKg, mTvDescriptionCartPrice, mTvCartReviews, mTvCartRating, mIvDescriptionMessage;

    private TextInputLayout mTCartMessage;
    private EditText mEtDescriptionCartMessage;
    private RadioGroup mRgProductCartKG;
    private RadioButton mRbOneCart, mRbOneFiveCart, mRbTwoCart;
    private List<Product> mProductList;
    private ProductAdapterRV adapter;
    private int position;
    private Animator currentAnimator;
    private int shortAnimationDuration, productID, custId;
    private double oneKgPrice = 10.00, oneFiveKgPrice = 15.00, twoKgPrice = 20.00, productKg = 0.0;
    private boolean oneKgChecked = false, oneFiveKgChecked = false, twoKgChecked = false;
    private Button mSaveCart;
    private ConnectionClass connectionClass;
    private SharedPreferences prf;
    private List<CustomerCart> mCustomerCartList;
    private List<CustomerWishList> mCustomerWishLists;
    private String cartId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart_details);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        prf = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);

        mIvCartProduct = findViewById(R.id.ivProductCartImg);
        mRatingBarCart = findViewById(R.id.ratingCart);
        mTvDescriptionCartName = findViewById(R.id.tvProductCartName);
        mTvDescriptionCartPrice = findViewById(R.id.tvDescriptionCartPrice);
        mEtDescriptionCartMessage = findViewById(R.id.etDescriptionCartMessage);
        mTvCartReviews = findViewById(R.id.tvCartReviews);
        mIvCartLove = findViewById(R.id.ivCartLove);
        mTvCartRating = findViewById(R.id.tvCartRating);
        mIvBookingHeader = findViewById(R.id.bookingHeader);
        mIvDescriptionCartBg = findViewById(R.id.ivDescriptionCartBg);
        mTCartMessage = findViewById(R.id.tvCartMessage);
        mRgProductCartKG = findViewById(R.id.rgProductCartKG);
        mRbOneCart = findViewById(R.id.oneKgCart);
        mRbOneFiveCart = findViewById(R.id.oneFiveKgCart);
        mRbTwoCart = findViewById(R.id.twoKgCart);
        mSaveCart = findViewById(R.id.saveCart);


        connectionClass = new ConnectionClass();

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("Position"));
        custId = Integer.parseInt(intent.getStringExtra("CustId"));
        productID = Integer.parseInt(intent.getStringExtra("ProductId"));


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(displayUserCartURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerCart>> displayUserCart = jsonPlaceHolderApi.getUserCart("GET", prf.getString("Id", null));

        displayUserCart.enqueue(new Callback<List<CustomerCart>>() {
            @Override
            public void onResponse(Call<List<CustomerCart>> call, Response<List<CustomerCart>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code "+ response.code();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerCartList = response.body();
                cartId = mCustomerCartList.get(position).getCartId();

                checkProductDetails(mCustomerCartList);
                checkWishList();

                mIvCartProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomImageFromThumb(mIvCartProduct);
                    }
                });
                shortAnimationDuration= getResources().getInteger(android.R.integer.config_shortAnimTime);

            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });

        mSaveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCart();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
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

        Call<List<CustomerWishList>> displayWishListProduct = jsonPlaceHolderApi.checkUserWishList("GET", String.valueOf(custId), String.valueOf(productID));

        displayWishListProduct.enqueue(new Callback<List<CustomerWishList>>() {
            @Override
            public void onResponse(Call<List<CustomerWishList>> call, Response<List<CustomerWishList>> response) {
                if (!response.isSuccessful()) {
                    String error = "Code " + response.code();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomerWishLists = response.body();

                if (mCustomerWishLists.size() != 0 && String.valueOf(mCustomerWishLists.size()) != null) {
                    if (mCustomerWishLists.get(0).getWishListStatus().equals("1"))
                        mIvCartLove.setBackgroundResource(R.drawable.loveiconred);
                } else
                    mIvCartLove.setBackgroundResource(R.drawable.loveicon);


                mIvCartLove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //insert wishlist item details when item in database not existed (new favorite wish list)
                        if (String.valueOf(mCustomerWishLists.size()) == null || String.valueOf(mCustomerWishLists.size()).equals("0")) {
                            updateWishListDetails("2");
                            mIvCartLove.setBackgroundResource(R.drawable.loveiconred);
                            Snackbar.make(view, getString(R.string.item_added),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        // item in database exited and check the status whether is 0, if yes update to 1 and set into favourite list (favorite wish list)
                        else if (mCustomerWishLists.get(0).getWishListStatus().equals("0")) {
                            mIvCartLove.setBackgroundResource(R.drawable.loveiconred);
                            updateWishListDetails("1");
                            Snackbar.make(view, getString(R.string.item_added),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        // item in database exited and check the status whether is 1, if yes update to 0 and set into unfavourite list (non-favorite wish list anymore)
                        else {
                            mIvCartLove.setBackgroundResource(R.drawable.loveicon);
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

                String z = "", messageOnCake = "";
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
                    Toast.makeText(ProductCartDetails.this, z, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ProductCartDetails.this, z, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkProductDetails(final List<CustomerCart> mCustomerCartList) {

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
            mTvDescriptionCartName.setText(mProductList.get(Integer.parseInt(mCustomerCartList.get(position).getProductId())-1).getTitle());
            mTvDescriptionCartPrice.setText(mCustomerCartList.get(position).getPrice());
            updateKgPrice(mCustomerCartList);
            mTCartMessage.getEditText().setText(mCustomerCartList.get(position).getCakeOnMessage());
            Glide.with(getApplicationContext()).load(mProductList.get(Integer.parseInt(mCustomerCartList.get(position).getProductId())-1)
                    .getProductImage())
                    .override(400, 300).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mIvCartProduct);

            mIvCartProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(mIvCartProduct);
                }
            });
            shortAnimationDuration= getResources().getInteger(android.R.integer.config_shortAnimTime);
        }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    private void saveCart() {

        String z = "", messageOnCake = "", price = "", weightOfCakes = "";
        boolean isSuccess = false;

        if(mEtDescriptionCartMessage.getText().toString().equals("")||mEtDescriptionCartMessage.getText().toString() == null){
            messageOnCake = "";
        }else{
            messageOnCake = mEtDescriptionCartMessage.getText().toString();
        }
        if (String.valueOf(productKg) !=null && !String.valueOf(productKg).equals("")){
            weightOfCakes = String.valueOf(productKg);
        }
        if (mTvDescriptionCartPrice.getText().toString()!=null && !mTvDescriptionCartPrice.getText().toString().equals("")){
            price = mTvDescriptionCartPrice.getText().toString();
        }

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "update customer_cart SET product_kg = '"+ weightOfCakes + "', cakeOnMessage = '"+ messageOnCake + "', price = '" + price
                        +"' WHERE cartId = "+ cartId+ "";

                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);

                z = "Save Cart Successfully";
                isSuccess = true;
            }
        }catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions" + ex;
        }

        if(isSuccess) {
            Toast.makeText(ProductCartDetails.this, z, Toast.LENGTH_SHORT).show();
        }

    }

    private void updateKgPrice(final List<CustomerCart> mCustomerCartList){
        mRgProductCartKG.clearCheck();
        productKg = Double.parseDouble(mCustomerCartList.get(position).getProduct_kg());

        switch(String.valueOf(productKg)){
            case "1.0":
                oneKgChecked = true;
                mRbOneCart.setChecked(true);
                break;
            case "1.5":
                oneFiveKgChecked = true;
                mRbOneFiveCart.setChecked(true);
                break;
            case "2.0":
                twoKgChecked = true;
                mRbTwoCart.setChecked(true);
                break;
        }

        mRgProductCartKG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(radioGroup.getCheckedRadioButtonId()){
                    case R.id.oneKgCart: {
                        oneKgChecked = true;
                        productKg = 1.0;
                        if (oneFiveKgChecked) {
                            oneFiveKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionCartPrice.getText().toString()) - oneFiveKgPrice;
                            mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                        } else if (twoKgChecked) {
                            twoKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionCartPrice.getText().toString()) - twoKgPrice;
                            mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                        }
                        Double price = Double.parseDouble(mTvDescriptionCartPrice.getText().toString()) + oneKgPrice;
                        mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                    }
                    break;

                    case R.id.oneFiveKgCart: {
                        oneFiveKgChecked = true;
                        productKg = 1.5;
                        if (oneKgChecked) {
                            oneKgChecked = false;
                            Double price = Double.parseDouble( mTvDescriptionCartPrice.getText().toString()) - oneKgPrice;
                            mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                        } else if (twoKgChecked) {
                            twoKgChecked = false;
                            Double price = Double.parseDouble( mTvDescriptionCartPrice.getText().toString()) - twoKgPrice;
                            mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                        }
                        Double price = Double.parseDouble( mTvDescriptionCartPrice.getText().toString()) + oneFiveKgPrice;
                        mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                    }
                    break;

                    case R.id.twoKgCart:{
                        twoKgChecked = true;
                        productKg = 2.0;
                        if (oneKgChecked) {
                            oneKgChecked = false;
                            Double price = Double.parseDouble(mTvDescriptionCartPrice.getText().toString()) - oneKgPrice;
                            mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                        } else if (oneFiveKgChecked) {
                            oneFiveKgChecked = false;
                            Double price = Double.parseDouble( mTvDescriptionCartPrice.getText().toString()) - oneFiveKgPrice;
                            mTvDescriptionCartPrice.setText(String.format("%.2f",price));
                        }
                        Double price = Double.parseDouble( mTvDescriptionCartPrice.getText().toString()) + twoKgPrice;
                        mTvDescriptionCartPrice.setText(String.format("%.2f",price));
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
                R.id.expandedProductCartImg);

        Glide.with(getApplicationContext()).load(mProductList.get(Integer.parseInt(mCustomerCartList.get(position).getProductId())-1).getProductImage())
                .override(400, 300).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
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