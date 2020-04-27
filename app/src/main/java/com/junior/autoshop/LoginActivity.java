package com.junior.autoshop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {

    private TextView tvUsername, tvPassword;
    private User userModel;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvUsername = findViewById(R.id.txt_username);
        tvPassword = findViewById(R.id.txt_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.txt_register);

        handleSSLHandshake();

        UserPreference mUserPreference = new UserPreference(this);
        userModel = mUserPreference.getUser();
        Log.d("tag", userModel.getId().toString());
        if (userModel.getId() != null && !userModel.getId().equals("")) {
            hitLogin(userModel.getEmail(), userModel.getPassword());
        }
        userModel = new User();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inReg = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(inReg);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = tvUsername.getText().toString();
                String userPass = tvPassword.getText().toString();

                if (userEmail.isEmpty()) {
                    tvUsername.setError("Username Can't be Empty");
                    tvUsername.requestFocus();
                    Toast.makeText(LoginActivity.this, "Username Can't be Empty", Toast.LENGTH_SHORT).show();
                } else if (userPass.isEmpty()) {
                    tvPassword.setError("Password Can't be Empty");
                    tvPassword.requestFocus();
                    Toast.makeText(LoginActivity.this, "Password Can't be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    //hitLogin(userEmail, userPass);
                    //todo
                    Intent intent = new Intent(LoginActivity.this, MainCustomerActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void hitLogin(final String userEmail, final String userPassword) {
        loading = ProgressDialog.show(LoginActivity.this, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json Login", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");

                    loading.dismiss();

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (response.equals("1")) {
                        String userId = jo.getString("ID");
                        String userName = jo.getString("FULLNAME");
                        String userNik = jo.getString("NIK");
                        String userEmail = jo.getString("EMAIL");
                        String userPass = jo.getString("PASSWORD");
                        String userPhone = jo.getString("PHONE");
                        String userAddress = jo.getString("ADDRESS");
                        String userRole = jo.getString("ROLE");
                        saveUser(userId, userPass, userName, userEmail, userAddress, userPhone, userNik, userRole);
                        if(userRole.equals("ADMIN")){
                            Intent intentAdmin = new Intent(LoginActivity.this, MainAdminActivity.class);
                            startActivity(intentAdmin);
                            finish();
                        }else{
                            Intent intentCustomer = new Intent(LoginActivity.this, MainCustomerActivity.class);
                            startActivity(intentCustomer);
                            finish();
                        }

                        Toast.makeText(LoginActivity.this, "Welcome, " + userName + " !", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoginActivity.this, getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("EMAIL", userEmail);
                params.put("PASSWORD", userPassword);

                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    void saveUser(String userId, String userPassword, String userFullname, String userEmail,
                  String userAddress, String userPhone, String userNik, String userRole) {
        UserPreference userPreference = new UserPreference(this);
        userModel.setId(userId);
        userModel.setPassword(userPassword);
        userModel.setFullname(userFullname);
        userModel.setUsername(userNik);
        userModel.setEmail(userEmail);
        userModel.setPhone(userPhone);
        userModel.setRole(userRole);

        userPreference.setUser(userModel);
    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}
