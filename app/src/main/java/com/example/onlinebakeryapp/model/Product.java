package com.example.onlinebakeryapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("product_id")
    @Expose
    private int id;
    @SerializedName("product_title")
    @Expose
    private String title;
    @SerializedName("product_price")
    @Expose
    private String price;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("product_quantity")
    @Expose
    private String productQuantity;

    public Product(int id, String title, String price, String productImage, String productQuantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.productImage = productImage;
        this.productQuantity = productQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
