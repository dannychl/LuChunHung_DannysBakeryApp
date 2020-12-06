package com.example.onlinebakeryapp.model;

public class CustomerWishList {

    private String wishListId;
    private String wishListStatus;
    private String productId;
    private String custId;

    public CustomerWishList(String wishListId, String productId, String wishListStatus, String custId) {
        this.wishListId = wishListId;
        this.productId = productId;
        this.wishListStatus = wishListStatus;
        this.custId = custId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getWishListId() {
        return wishListId;
    }

    public void setWishListId(String wishListId) {
        this.wishListId = wishListId;
    }

    public String getWishListStatus() {
        return wishListStatus;
    }

    public void setWishListStatus(String wishListStatus) {
        this.wishListStatus = wishListStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
