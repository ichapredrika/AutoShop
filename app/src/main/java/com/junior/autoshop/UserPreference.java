package com.junior.autoshop;

import android.content.Context;
import android.content.SharedPreferences;

import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Customer;

public class UserPreference {

    private static final String PREFS_NAME = "user_pref";
    private static final String ROLE_TYPE = "TYPE";
    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String CUSTOMER_FULLNAME = "FULLNAME";
    private static final String CUSTOMER_EMAIL = "EMAIL";
    private static final String CUSTOMER_USERNAME = "USERNAME";
    private static final String CUSTOMER_PHONE = "PHONE";
    private static final String CUSTOMER_PASSWORD = "PASSWORD";

    private static final String AUTOSHOP_ID = "AUTOSHOP_ID";
    private static final String AUTOSHOP_NAME = "NAME";
    private static final String AUTOSHOP_USERNAME = "USERNAME";
    private static final String AUTOSHOP_EMAIL = "EMAIL";
    private static final String AUTOSHOP_ADMIN_CONTACT = "ADMIN_CONTACT";
    private static final String AUTOSHOP_PICKER_CONTACT = "PICKER_CONTACT";
    private static final String AUTOSHOP_PASSWORD = "PASSWORD";
    private static final String AUTOSHOP_ADDRESS = "ADDRESS";
    private static final String AUTOSHOP_LATLONG = "LATLONG";
    private static final String AUTOSHOP_SPACE = "SPACE";
    private static final String AUTOSHOP_BANK = "BANK";
    private static final String AUTOSHOP_ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    private static final String AUTOSHOP_PHOTO = "PHOTO";


    private final SharedPreferences preferences;

    public UserPreference(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setCustomer (Customer value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CUSTOMER_ID, value.getId());
        editor.putString(CUSTOMER_PASSWORD, value.getPassword());
        editor.putString(CUSTOMER_FULLNAME, value.getFullname());
        editor.putString(CUSTOMER_EMAIL, value.getEmail());
        editor.putString(CUSTOMER_USERNAME, value.getUsername());
        editor.putString(CUSTOMER_PHONE, value.getPhone());

        editor.apply();
    }

    public void setType(String type){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ROLE_TYPE, type);
        editor.apply();
    }

    public String getType(){
        return preferences.getString(ROLE_TYPE,"");
    }


    public void setAutoshop(Autoshop value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(AUTOSHOP_ID, value.getId());
        editor.putString(AUTOSHOP_NAME, value.getName());
        editor.putString(AUTOSHOP_PASSWORD, value.getPassword());
        editor.putString(AUTOSHOP_USERNAME, value.getUsername());
        editor.putString(AUTOSHOP_EMAIL, value.getEmail());
        editor.putString(AUTOSHOP_ADMIN_CONTACT, value.getAdminContact());
        editor.putString(AUTOSHOP_PICKER_CONTACT, value.getPickerContact());
        editor.putString(AUTOSHOP_ADDRESS, value.getAddress());
        editor.putString(AUTOSHOP_LATLONG, value.getLatlong());
        editor.putString(AUTOSHOP_SPACE, value.getSpace());
        editor.putString(AUTOSHOP_BANK, value.getBank());
        editor.putString(AUTOSHOP_ACCOUNT_NUMBER, value.getAccountNumber());
        editor.putString(AUTOSHOP_PHOTO, value.getPhoto());

        editor.apply();
    }

    public Customer getCustomer() {
        Customer model = new Customer();
        model.setId(preferences.getString(CUSTOMER_ID, ""));
        model.setPassword(preferences.getString(CUSTOMER_PASSWORD, ""));
        model.setFullname(preferences.getString(CUSTOMER_FULLNAME, ""));
        model.setEmail(preferences.getString(CUSTOMER_EMAIL, ""));
        model.setUsername(preferences.getString(CUSTOMER_USERNAME, ""));
        model.setPhone(preferences.getString(CUSTOMER_PHONE, ""));

        return model;
    }

    public Autoshop getAutoshop() {
        Autoshop model = new Autoshop();
        model.setId(preferences.getString(AUTOSHOP_ID, ""));
        model.setName(preferences.getString(AUTOSHOP_NAME, ""));
        model.setPassword(preferences.getString(AUTOSHOP_PASSWORD, ""));
        model.setUsername(preferences.getString(AUTOSHOP_USERNAME, ""));
        model.setEmail(preferences.getString(AUTOSHOP_EMAIL, ""));
        model.setAdminContact(preferences.getString(AUTOSHOP_ADMIN_CONTACT, ""));
        model.setPickerContact(preferences.getString(AUTOSHOP_PICKER_CONTACT, ""));
        model.setAddress(preferences.getString(AUTOSHOP_ADDRESS, ""));
        model.setLatlong(preferences.getString(AUTOSHOP_LATLONG, ""));
        model.setSpace(preferences.getString(AUTOSHOP_SPACE, ""));
        model.setBank(preferences.getString(AUTOSHOP_BANK, ""));
        model.setAccountNumber(preferences.getString(AUTOSHOP_ACCOUNT_NUMBER, ""));
        model.setPhoto(preferences.getString(AUTOSHOP_PHOTO, ""));

        return model;
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
