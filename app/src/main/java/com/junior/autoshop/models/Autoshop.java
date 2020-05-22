package com.junior.autoshop.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Autoshop implements Parcelable {
    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String address;
    private String latlong;
    private int space;
    private String bank;
    private int accountNumber;
    private String photo;

    public Autoshop(){

    }

    public Autoshop(JSONObject object) {
        try {
            this.id = object.getString("AUTOSHOP_ID");
            this.name = object.getString("NAME");
            this.username = object.getString("USERNAME");
            this.email = object.getString("EMAIL");
            this.phone = object.getString("PHONE");
            this.password = object.getString("PASSWORD");
            this.address = object.getString("ADDRESS");
            this.latlong = object.getString("LATLONG");
            this.space = object.getInt("SPACE");
            this.bank = object.getString("BANK");
            this.accountNumber = object.getInt("ACCOUNT_NUMBER");
            this.photo = object.getString("PHOTO");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Autoshop(JSONObject object, boolean partial) {
        try {
            this.id = object.getString("AUTOSHOP_ID");
            this.name = object.getString("NAME");
            this.username = object.getString("USERNAME");
            this.email = object.getString("EMAIL");
            this.password = object.getString("PASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Autoshop(String id, String name, String username, String email, String phone, String password, String address, String latlong, int space, String bank, int accountNumber, String photo) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.latlong = latlong;
        this.space = space;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.password);
        dest.writeString(this.address);
        dest.writeString(this.latlong);
        dest.writeInt(this.space);
        dest.writeString(this.bank);
        dest.writeInt(this.accountNumber);
        dest.writeString(this.photo);
    }

    protected Autoshop(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.password = in.readString();
        this.address = in.readString();
        this.latlong = in.readString();
        this.space = in.readInt();
        this.bank = in.readString();
        this.accountNumber = in.readInt();
        this.photo = in.readString();
    }

    public static final Parcelable.Creator<Autoshop> CREATOR = new Parcelable.Creator<Autoshop>() {
        @Override
        public Autoshop createFromParcel(Parcel source) {
            return new Autoshop(source);
        }

        @Override
        public Autoshop[] newArray(int size) {
            return new Autoshop[size];
        }
    };
}
