package com.example.onlinebakeryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onlinebakeryapp.model.CustomerCart;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderDetails extends AppCompatActivity {

    private String displayUserOrderDetailsURL = "http://192.168.8.107";
    private TextView mTvPaymentCartView, mTvDateOrdered, mTvTimeOrdered, mTvOrderDescriptions, mTvOrderRm,
            mTvTotalPaymentOrderDetails, mTvTotalPaymentOrderDetailsAmt, mTvTotalPaymentDiscOrderDetails,
            mTvTotalPaymentDiscOrderDetailsAmt;
    private ImageView mIvOrderDetailsBg, mIvDateOrdered, mIvTimeOrdered, mIvOrderDescriptionBg;
    private RecyclerView rV;
    private PaymentAdapter adapter;
    private SharedPreferences prf;
    private List<CustomerCart> mCustomerCartList;
    private String dateString= "", timeString = "", hour = "", min = "", amPm = "";
    private double totalPrice = 0.0, discountRate = 0.06;
    private SimpleDateFormat sdf, sdf2;
    private ConnectionClass connectionClass;
    private ImageView mIvQrCode, mIvQrCode2;
    private int shortAnimationDuration;
    private Animator currentAnimator;
    private Bitmap mBitmap;
    private ByteArrayOutputStream stream;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setTitle("My Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headerColor)));

        mTvPaymentCartView = findViewById(R.id.tvPaymentCartView);
        mTvPaymentCartView.setVisibility(View.GONE);
        mTvDateOrdered = findViewById(R.id.tvDateOrdered);
        mTvTimeOrdered = findViewById(R.id.tvTimeOrdered);
        mTvOrderDescriptions = findViewById(R.id.tvOrderDescriptions);
        mTvOrderRm = findViewById(R.id.tvOrderRM);
        mTvTotalPaymentOrderDetails = findViewById(R.id.tvTotalPaymentOrderDetails);
        mTvTotalPaymentDiscOrderDetailsAmt = findViewById(R.id.tvTotalPaymentDiscOrderDetailsAmt);
        mTvTotalPaymentOrderDetailsAmt = findViewById(R.id.tvTotalPaymentOrderDetailsAmt);
        mTvTotalPaymentDiscOrderDetails = findViewById(R.id.tvTotalPaymentDiscOrderDetails);
        mIvQrCode = findViewById(R.id.ivQrCode);
        mIvQrCode2 = findViewById(R.id.ivQrCode2);
        mRelativeLayout = findViewById(R.id.containerImg);

        connectionClass = new ConnectionClass();

        mIvOrderDescriptionBg = findViewById(R.id.ivOrderDescriptionBg);
        mIvDateOrdered = findViewById(R.id.ivDateOrdered);
        mIvTimeOrdered = findViewById(R.id.ivTimeOrdered);
        mIvOrderDetailsBg = findViewById(R.id.ivOrderDetailsBg);

        prf = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);

        rV = findViewById(R.id.recyclerView);
        rV.setHasFixedSize(true);
        rV.setLayoutManager(new LinearLayoutManager(this));
        rV.setAdapter(adapter);

        ViewCompat.setNestedScrollingEnabled(rV, false);


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build())
                .baseUrl(displayUserOrderDetailsURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        Call<List<CustomerCart>> displayUserCart = jsonPlaceHolderApi.getUserOrderDetails("GET", prf.getString("Id", null));

    displayUserCart.enqueue(new Callback<List<CustomerCart>>() {
        @Override
        public void onResponse(Call<List<CustomerCart>> call, Response<List<CustomerCart>> response) {
            if (!response.isSuccessful()) {
                String error = "Code "+ response.code();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                return;
            }

            mCustomerCartList = response.body();
            adapter = new PaymentAdapter(getApplicationContext(), mCustomerCartList);
            rV.setHasFixedSize(true);
            rV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            ViewCompat.setNestedScrollingEnabled(rV, false);

            for (int i = 0; i<mCustomerCartList.size(); i++){
                totalPrice += Double.parseDouble(mCustomerCartList.get(i).getPrice());
            }

            dateString = mCustomerCartList.get(mCustomerCartList.size()-1).getDate();
            timeString = mCustomerCartList.get(mCustomerCartList.size()-1).getTime();

            setDateTime();

            mTvTotalPaymentOrderDetailsAmt.setText(String.format("%.2f", totalPrice));
            mTvTotalPaymentDiscOrderDetailsAmt.setText(String.format("%.2f", totalPrice-(totalPrice*discountRate)));
            generateQrCode();

            mIvQrCode2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRelativeLayout.setVisibility(View.VISIBLE);
                    zoomImageFromThumb(mIvQrCode);
                }
            });
                shortAnimationDuration= getResources().getInteger(android.R.integer.config_shortAnimTime);

                if (mCustomerCartList.size()<1){
                    mTvPaymentCartView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<CustomerCart>> call, Throwable t) {

            }
        });
    }

    private void generateQrCode() {

        String data = "CI: " +prf.getString("Id", null);
        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 1600);

        try{
            mBitmap = qrgEncoder.getBitmap();
            stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            mIvQrCode.setImageBitmap(mBitmap);
            mIvQrCode2.setImageBitmap(mBitmap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.order_details_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.cancel_order:

                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetails.this, R.style.MyAlertDialogStyle);
                StringBuilder strBuilder = new StringBuilder();
                builder.setTitle("Press confirm to cancel all your order now");

                builder.setMessage(strBuilder.toString());
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelOrderClicked();
                        onBackPressed();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
                break;
        }
        return true;
    }

    private void cancelOrderClicked() {
        String z = "";
        boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "update customer_cart SET status = '"+ "NEW"+ "' where custId = " + prf.getString("Id",null) + " AND status = '"+ "PendingConfirm" +"';";

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
            Toast.makeText(OrderDetails.this, z, Toast.LENGTH_SHORT).show();
        }else{
            deleteOrder();
        }
    }

    private void deleteOrder() {

        String z = "";
        boolean isSuccess = false;

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Please check your internet connection";
            } else {
                String query= "delete from order_details where custId = " + prf.getString("Id",null) + " AND status = '"+ "Confirm" +"';";

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
            Toast.makeText(OrderDetails.this, z, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(OrderDetails.this, "Order cancelled successfuly", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDateTime() {

        hour = timeString.substring(0, 2);

        if (Integer.parseInt(hour) >= 12)
            amPm = "PM";
        else
            amPm = "AM";

        mTvDateOrdered.setText("Date Ordered: "+ dateString);
        mTvTimeOrdered.setText("Time Ordered: "+ timeString+" " +amPm);
    }

    @Override
        public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent (OrderDetails.this, MainActivity2.class);
        intent.putExtra("CallingActivity", CartActivity.ACTIVITY_1);
        startActivity(intent);
    }

    @SuppressLint("Range")
    private void zoomImageFromThumb(final View thumbView){
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expandedProductImg);

        Glide.with(this)
                .load(stream.toByteArray()).override(1000, 1100).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(expandedImageView);

//        Glide.with(getApplicationContext()).load(mDrawable).override(400, 300).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(expandedImageView);


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
                    mIvQrCode.setVisibility(View.GONE);
                    mRelativeLayout.setVisibility(View.GONE);
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
                        mIvQrCode.setVisibility(View.GONE);
                        mRelativeLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                        mIvQrCode.setVisibility(View.GONE);
                        mRelativeLayout.setVisibility(View.GONE);
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}