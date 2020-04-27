package com.junior.autoshop;

import android.content.Context;
import android.content.SharedPreferences;

import com.junior.autoshop.models.User;

public class UserPreference {

    private static final String PREFS_NAME = "user_pref";
    private static final String USER_ID = "userId";
    private static final String USER_FULLNAME = "userFullName";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_NAME = "userName";
    private static final String USER_PHONE = "userPhone";
    private static final String USER_ROLE = "userRole";
    private static final String USER_PASSWORD = "userPassword";


    private final SharedPreferences preferences;

    public UserPreference(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setUser(User value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, value.getId());
        editor.putString(USER_PASSWORD, value.getPassword());
        editor.putString(USER_FULLNAME, value.getFullname());
        editor.putString(USER_EMAIL, value.getEmail());
        editor.putString(USER_NAME, value.getUsername());
        editor.putString(USER_PHONE, value.getPhone());
        editor.putString(USER_ROLE, value.getRole());

        editor.apply();
    }

    public User getUser() {
        User model = new User();
        model.setId(preferences.getString(USER_ID, ""));
        model.setPassword(preferences.getString(USER_PASSWORD, ""));
        model.setFullname(preferences.getString(USER_FULLNAME, ""));
        model.setEmail(preferences.getString(USER_EMAIL, ""));
        model.setUsername(preferences.getString(USER_NAME, ""));
        model.setPhone(preferences.getString(USER_PHONE, ""));
        model.setRole(preferences.getString(USER_ROLE, ""));

        return model;
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
