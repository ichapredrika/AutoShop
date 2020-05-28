package com.junior.autoshop.models;

import org.json.JSONObject;

public class Customer {
    private String id;
    private String fullname;
    private String email;
    private String phone;
    private String username;
    private String password;

    public Customer() {

    }

    public Customer(JSONObject object) {
        try {
            this.id = object.optString("CUSTOMER_ID", "");
            this.fullname = object.optString("FULLNAME", "");
            this.email = object.optString("EMAIL", "");
            this.phone = object.optString("PHONE", "");
            this.username = object.optString("USERNAME", "");
            this.password = object.optString("PASSWORD", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Customer(String id, String fullname, String email, String phone, String username, String password) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
