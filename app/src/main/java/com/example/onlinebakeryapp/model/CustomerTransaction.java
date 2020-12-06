package com.example.onlinebakeryapp.model;

public class CustomerTransaction {

    private String orderHistoryId;
    private String productId;
    private String product_kg;
    private String cakeOnMessage;
    private String price;
    private String orderDate;
    private String orderTime;
    private String paymentType;

    public CustomerTransaction(String orderHistoryId, String productId, String product_kg, String cakeOnMessage, String price, String orderDate, String orderTime, String paymentType) {
        this.orderHistoryId = orderHistoryId;
        this.productId = productId;
        this.product_kg = product_kg;
        this.cakeOnMessage = cakeOnMessage;
        this.price = price;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.paymentType = paymentType;
    }

    public String getOrderHistoryId() {
        return orderHistoryId;
    }

    public void setOrderHistoryId(String orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
