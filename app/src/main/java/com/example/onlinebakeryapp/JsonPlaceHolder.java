package com.example.onlinebakeryapp;

import com.example.onlinebakeryapp.model.Customer;
import com.example.onlinebakeryapp.model.CustomerCart;
import com.example.onlinebakeryapp.model.CustomerTransaction;
import com.example.onlinebakeryapp.model.CustomerWishList;
import com.example.onlinebakeryapp.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolder {
    @GET("posts")
    Call<List<Product>> getProducts();

    @GET("posts")
    Call<List<Customer>> getUsername();

    @GET("/cakeProduct/displayAllUsername.php")
    Call<List<Customer>> getUsername(
            @Query("method") String method,
            @Query("username") String username);

    @GET("/cakeProduct/loadUserCash.php")
    Call<List<Customer>> getUserCash(
            @Query("method") String method,
            @Query("id") String id);

    @GET("/cakeProduct/displayUserCart.php")
    Call<List<CustomerCart>> getUserCart(
            @Query("method") String method,
            @Query("id") String id);

    @GET("/cakeProduct/displayUserCartPayment.php")
    Call<List<CustomerCart>> getUserCartPayment(
            @Query("method") String method,
            @Query("id") String id);

    @GET("/cakeProduct/checkUserWishList.php")
    Call<List<CustomerWishList>> checkUserWishList(
            @Query("method") String method,
            @Query("id") String id,
            @Query("productId") String productId);

    @GET("/cakeProduct/displayUserWishList.php")
    Call<List<CustomerWishList>> getUserWishList(
            @Query("method") String method,
            @Query("id") String id);

    @GET("/cakeProduct/displayUserOrderDetails.php")
    Call<List<CustomerCart>> getUserOrderDetails(
            @Query("method") String method,
            @Query("id") String id);

    @GET("/cakeProduct/displayUserOrderPlaced.php")
    Call<List<CustomerCart>> getUserOrderPlaced(
            @Query("method") String method,
            @Query("id") String id);

    @GET("/cakeProduct/displayUserAllTransactionHistory.php")
    Call<List<CustomerTransaction>> getUserAllTransactionHis(
            @Query("method") String method,
            @Query("id") String id);

    @GET("/cakeProduct/displayUserTransactionHistory.php")
    Call<List<CustomerTransaction>> getUserTransactionHis(
            @Query("method") String method,
            @Query("id") String id,
            @Query("date") String date,
            @Query("time") String time);
}
