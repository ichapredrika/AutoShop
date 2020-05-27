package com.junior.autoshop.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.json.JSONObject;

public class Autoshop implements Parcelable {
    private String id;
    private String name;
    private String username;
    private String email;
    private String pickerContact;
    private String adminContact;
    private String password;
    private String address;
    private String latlong;
    private String space;
    private String bank;
    private String accountNumber;
    private String photo;
    private String openHours;
    private String closeHours;

    public Autoshop(){

    }

    public Autoshop(JSONObject object) {
        try {
            this.id = object.optString("AUTOSHOP_ID", "");
            this.name = object.optString("NAME", "");
            this.username = object.optString("USERNAME", "");
            this.password = object.optString("PASSWORD", "");
            this.email = object.optString("EMAIL", "");
            this.pickerContact = object.optString("PICKUP_CONTACT", "");
            this.adminContact = object.optString("ADMIN_CONTACT", "");
            this.address = object.optString("ADDRESS", "");
            this.latlong = object.optString("LATLONG", "");
            this.space = object.optString("SPACE", "");
            this.bank = object.optString("BANK", "");
            this.accountNumber = object.optString("ACCOUNT_NUMBER", "");
            this.photo = object.optString("PHOTO", "");
            this.openHours = object.optString("OPEN_HOURS", "");
            this.closeHours = object.optString("CLOSE_HOURS", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Autoshop(String id, String name, String username, String email, String pickerContact, String adminContact, String password, String address, String latlong, String space, String bank, String accountNumber, String photo, String openHours, String closeHours) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.pickerContact = pickerContact;
        this.adminContact = adminContact;
        this.password = password;
        this.address = address;
        this.latlong = latlong;
        this.space = space;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.photo = photo;
        this.openHours = openHours;
        this.closeHours = closeHours;
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

    public String getPickerContact() {
        return pickerContact;
    }

    public void setPickerContact(String pickerContact) {
        this.pickerContact = pickerContact;
    }

    public String getAdminContact() {
        return adminContact;
    }

    public void setAdminContact(String adminContact) {
        this.adminContact = adminContact;
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

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getCloseHours() {
        return closeHours;
    }

    public void setCloseHours(String closeHours) {
        this.closeHours = closeHours;
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
        dest.writeString(this.pickerContact);
        dest.writeString(this.adminContact);
        dest.writeString(this.password);
        dest.writeString(this.address);
        dest.writeString(this.latlong);
        dest.writeString(this.space);
        dest.writeString(this.bank);
        dest.writeString(this.accountNumber);
        dest.writeString(this.photo);
        dest.writeString(this.openHours);
        dest.writeString(this.closeHours);
    }

    protected Autoshop(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.pickerContact = in.readString();
        this.adminContact = in.readString();
        this.password = in.readString();
        this.address = in.readString();
        this.latlong = in.readString();
        this.space = in.readString();
        this.bank = in.readString();
        this.accountNumber = in.readString();
        this.photo = in.readString();
        this.openHours = in.readString();
        this.closeHours = in.readString();
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
