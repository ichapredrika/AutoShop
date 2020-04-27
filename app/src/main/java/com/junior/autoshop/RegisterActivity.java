package com.junior.autoshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    Toolbar toolbar;
    private TextView tvFullname;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvPass1;
    private TextView tvPass2;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvFullname = findViewById(R.id.txt_fullname);
        tvUsername = findViewById(R.id.txt_username);
        tvEmail = findViewById(R.id.txt_email);
        tvPhone = findViewById(R.id.txt_phone);
        tvPass1 = findViewById(R.id.txt_password);
        tvPass2 = findViewById(R.id.txt_confirm_password);
        Button btnRegist = findViewById(R.id.btn_register);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    user = new User();
                    user.setFullname(tvFullname.getText().toString().trim());
                    user.setUsername(tvUsername.getText().toString().trim());
                    user.setEmail(tvEmail.getText().toString().trim());
                    user.setPassword(tvPass1.getText().toString().trim());
                    user.setPhone(tvPhone.getText().toString().trim());

                    //hitRegist(user);
                    //todo
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
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
        }  else if (tvPass1.getText().toString().trim().isEmpty()) {
            tvPass1.setError("Password Field Can't be Empty!");
            tvPass1.requestFocus();
        } else if (tvPass2.getText().toString().isEmpty()) {
            tvPass2.setError("Password Field Can't be Empty!");
            tvPass2.requestFocus();
        } else if (!tvPass1.getText().toString().equals(tvPass2.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
        } else {
            isValid = true;
        }
        return isValid;
    }

    private void hitRegist(final User user) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(RegisterActivity.this);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(TAG, s);
                try {
                    Log.d("Json register", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("STATUS");
                    String message = jo.getString("message");

                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                    if (response.equals("1")) {
                        Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(in);
                        //todo differentiate session (admin and user)
                    }
                } catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.msg_something_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("EMAIL", user.getEmail());
                params.put("FULLNAME", user.getFullname());
                params.put("USERNAME", user.getUsername());
                params.put("PASSWORD", user.getPassword());
                params.put("PHONE", user.getPhone());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
