package com.example.onlinebakeryapp.model;

public class Customer {

    private int id;
    private String username;
    private String cash;

    public Customer(int id, String username, String cash) {
        this.id = id;
        this.username = username;
        this.cash = cash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }
}
