package com.junior.autoshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.models.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterCustomerFragment extends Fragment {

    private static final String TAG = "RegCustActivity";
    private TextView tvFullname;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvPass1;
    private TextView tvPass2;
    private Customer customer;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static RegisterCustomerFragment newInstance(int index) {
        RegisterCustomerFragment fragment = new RegisterCustomerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    public RegisterCustomerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvFullname = view.findViewById(R.id.txt_fullname);
        tvUsername = view.findViewById(R.id.txt_username);
        tvEmail = view.findViewById(R.id.txt_email);
        tvPhone = view.findViewById(R.id.txt_phone);
        tvPass1 = view.findViewById(R.id.txt_password);
        tvPass2 = view.findViewById(R.id.txt_confirm_password);
        Button btnRegist = view.findViewById(R.id.btn_register);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    customer = new Customer();
                    customer.setFullname(tvFullname.getText().toString().trim());
                    customer.setUsername(tvUsername.getText().toString().trim());
                    customer.setEmail(tvEmail.getText().toString().trim());
                    customer.setPassword(tvPass1.getText().toString().trim());
                    customer.setPhone(tvPhone.getText().toString().trim());

                    hitRegist(customer);
                }
            }
        });
    }

    private boolean isValid() {
        boolean isValid = false;
        if (tvFullname.getText().toString().trim().isEmpty()) {
            tvFullname.setError("Fullname Can't be Empty!");
            tvFullname.requestFocus();
        } else if (tvUsername.getText().toString().trim().isEmpty()) {
            tvUsername.setError("Username Can't be Empty!");
            tvUsername.requestFocus();
        } else if (tvEmail.getText().toString().trim().isEmpty()) {
            tvEmail.setError("Email Field Can't be Empty!");
            tvEmail.requestFocus();
        } else if (!tvEmail.getText().toString().trim().contains("@") || !tvEmail.getText().toString().trim().contains(".com")) {
            tvEmail.setError("Email is not valid!");
            tvEmail.requestFocus();
        } else if (tvPhone.getText().toString().trim().isEmpty()) {
            tvPhone.setError("Phone Number Field Can't be Empty!");
            tvPhone.requestFocus();
        } else if (tvPass1.getText().toString().trim().isEmpty()) {
            tvPass1.setError("Password Field Can't be Empty!");
            tvPass1.requestFocus();
        } else if (tvPass2.getText().toString().isEmpty()) {
            tvPass2.setError("Password Field Can't be Empty!");
            tvPass2.requestFocus();
        } else if (!tvPass1.getText().toString().equals(tvPass2.getText().toString())) {
            Toast.makeText(getContext(), "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
        } else {
            isValid = true;
        }
        return isValid;
    }

    private void hitRegist(final Customer customer) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_REGISTER_CUSTOMER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d(TAG, s);
                        try {
                            Log.d("Json register", s);
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray data = jsonObject.getJSONArray("result");
                            JSONObject jo = data.getJSONObject(0);
                            String response = jo.getString("STATUS");
                            String message = jo.getString("message");

                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            if (response.equals("1")) {
                                Intent in = new Intent(getContext(), LoginActivity.class);
                                startActivity(in);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), getString(R.string.msg_something_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("EMAIL", customer.getEmail());
                params.put("FULLNAME", customer.getFullname());
                params.put("USERNAME", customer.getUsername());
                params.put("PASSWORD", customer.getPassword());
                params.put("PHONE", customer.getPhone());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
