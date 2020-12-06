package com.example.onlinebakeryapp.model;

public class CustomerCart {

    private String cartId;
    private String productId;
    private String product_kg;
    private String cakeOnMessage;
    private String price;
    private String date;
    private String time;

    public CustomerCart(String cartId, String productId, String product_kg, String cakeOnMessage, String price, String date, String time) {
        this.cartId = cartId;
        this.productId = productId;
        this.product_kg = product_kg;
        this.cakeOnMessage = cakeOnMessage;
        this.price = price;
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProduct_kg() {
        return product_kg;
    }

    public void setProduct_kg(String product_kg) {
        this.product_kg = product_kg;
    }

    public String getCakeOnMessage() {
        return cakeOnMessage;
    }

    public void setCakeOnMessage(String cakeOnMessage) {
        this.cakeOnMessage = cakeOnMessage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
