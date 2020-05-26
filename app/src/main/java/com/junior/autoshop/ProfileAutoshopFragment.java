package com.junior.autoshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Customer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ProfileAutoshopFragment extends Fragment {
    private static final String EXTRA_AUTOSHOP = "AUTOSHOP";
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private Autoshop autoshop;
    private ImageView imgEditProfile, imgAddService, imgAutoshop;
    private TextView tvName, tvUsername, tvEmail, tvPickerContact, tvAdminContact;
    private TextView tvAddress, tvLatlong, tvSpace, tvBank, tvAccount;

    private Dialog popUpDialog;

    public ProfileAutoshopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_autoshop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserPreference = new UserPreference(getContext());
        autoshop = mUserPreference.getAutoshop();

        initProfile(view);

        popUpDialog = new Dialog(getContext());
        Button btnLogout = view.findViewById(R.id.btn_logout);

        getProfile();

        imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpEditProfile();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                UserPreference mUserPreference = new UserPreference(getContext());
                mUserPreference.logoutUser();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initProfile(View view){
        imgAutoshop = view.findViewById(R.id.img_autoshop);
        imgEditProfile = view.findViewById(R.id.img_edit_profile);
        imgAddService = view.findViewById(R.id.img_add_service);
        tvName = view.findViewById(R.id.txt_name);
        tvUsername = view.findViewById(R.id.txt_username);
        tvEmail = view.findViewById(R.id.txt_email);
        tvPickerContact = view.findViewById(R.id.txt_phone_picker);
        tvAdminContact = view.findViewById(R.id.txt_phone_admin);
        tvAddress = view.findViewById(R.id.txt_address);
        tvLatlong = view.findViewById(R.id.txt_latlong);
        tvSpace = view.findViewById(R.id.txt_space);
        tvBank = view.findViewById(R.id.txt_bank);
        tvAccount = view.findViewById(R.id.txt_account_number);
    }
    private void popUpEditProfile(){
        popUpDialog.setContentView(R.layout.pop_up_edit_profile);
        ImageView imgAutoshop = popUpDialog.findViewById(R.id.img_autoshop);
        ImageView imgEditProfile = popUpDialog.findViewById(R.id.img_edit_profile);
        ImageView imgAddService = popUpDialog.findViewById(R.id.img_add_service);
        TextView tvName = popUpDialog.findViewById(R.id.txt_name);
        TextView tvUsername = popUpDialog.findViewById(R.id.txt_username);
        TextView tvEmail = popUpDialog.findViewById(R.id.txt_email);
        TextView tvPickerContact = popUpDialog.findViewById(R.id.txt_phone_picker);
        TextView tvAdminContact = popUpDialog.findViewById(R.id.txt_phone_admin);
        TextView tvAddress = popUpDialog.findViewById(R.id.txt_address);
        TextView tvLatlong = popUpDialog.findViewById(R.id.txt_latlong);
        TextView tvSpace = popUpDialog.findViewById(R.id.txt_space);
        TextView tvBank = popUpDialog.findViewById(R.id.txt_bank);
        TextView tvAccount = popUpDialog.findViewById(R.id.txt_account_number);

        tvName.setText(autoshop.getName());
        tvUsername.setText(autoshop.getUsername());
        tvEmail.setText(autoshop.getEmail());
        tvPickerContact.setText(autoshop.getPickerContact());
        tvAdminContact.setText(autoshop.getAdminContact());
        tvAddress.setText(autoshop.getAddress());
        tvLatlong.setText(autoshop.getLatlong());
        tvBank.setText(autoshop.getBank());
        tvSpace.setText(autoshop.getSpace());
        tvAccount.setText(autoshop.getAccountNumber());
        Button btnUpdate = popUpDialog.findViewById(R.id.btn_update);
        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
        Bitmap profileBitmap = decodeBitmap(autoshop.getPhoto());
        imgAutoshop.setImageBitmap(profileBitmap);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.dismiss();
            }
        });

        //todo
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (popUpDialog.getWindow() != null) {
            popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popUpDialog.show();
    }

    private void getProfile() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_GET_PROFILE_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json profile", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        JSONObject profile = jo.getJSONArray("PROFILE").getJSONObject(0);
                        Autoshop autoshop = new Autoshop(profile);
                        saveAdmin(autoshop);
                        updateUi(autoshop);

                    } else {
                        String message = jo.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("AUTOSHOP_ID", autoshop.getId());

                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    void saveAdmin(Autoshop autoshop) {
        UserPreference userPreference = new UserPreference(getContext());
        autoshop.setId(autoshop.getId());
        autoshop.setName(autoshop.getName());
        autoshop.setPassword(autoshop.getPassword());
        autoshop.setUsername(autoshop.getUsername());
        autoshop.setEmail(autoshop.getEmail());
        autoshop.setAdminContact(autoshop.getAdminContact());
        autoshop.setPickerContact(autoshop.getPickerContact());
        autoshop.setAddress(autoshop.getAddress());
        autoshop.setLatlong(autoshop.getLatlong());
        autoshop.setSpace(autoshop.getSpace());
        autoshop.setBank(autoshop.getBank());
        autoshop.setAccountNumber(autoshop.getAccountNumber());
        autoshop.setPhoto(autoshop.getPhoto());

        userPreference.setAutoshop(autoshop);
        userPreference.setType(EXTRA_AUTOSHOP);
    }

    void updateUi(Autoshop autoshop){
        tvName.setText(autoshop.getName());
        tvUsername.setText(autoshop.getUsername());
        tvEmail.setText(autoshop.getEmail());
        tvPickerContact.setText(autoshop.getPickerContact());
        tvAdminContact.setText(autoshop.getAdminContact());
        tvAddress.setText(autoshop.getAddress());
        tvLatlong.setText(autoshop.getLatlong());
        tvBank.setText(autoshop.getBank());
        tvSpace.setText(autoshop.getSpace());
        tvAccount.setText(autoshop.getAccountNumber());

        Bitmap profileBitmap = decodeBitmap(autoshop.getPhoto());
        imgAutoshop.setImageBitmap(profileBitmap);
    }

    private Bitmap decodeBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
