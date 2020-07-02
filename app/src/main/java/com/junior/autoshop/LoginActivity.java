package com.junior.autoshop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Customer;

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

    private final static String EXTRA_AUTOSHOP ="AUTOSHOP";
    private final static String EXTRA_CUSTOMER ="CUSTOMER";
    private TextView tvUsername, tvPassword;
    private Customer customer;
    private Autoshop autoshop;
    private RadioGroup rgRole;
    private String type;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvUsername = findViewById(R.id.txt_username);
        tvPassword = findViewById(R.id.txt_password);
        rgRole = findViewById(R.id.rg_type);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.txt_register);

        handleSSLHandshake();

        UserPreference mUserPreference = new UserPreference(this);
        type = mUserPreference.getType();

        if(type.equals(EXTRA_CUSTOMER)){
            customer = mUserPreference.getCustomer();
            if (customer.getId() != null && !customer.getId().equals("")) {
                hitLogin(customer.getUsername(), customer.getPassword(), EXTRA_CUSTOMER);
            }
        }else if (type.equals(EXTRA_AUTOSHOP)){
            autoshop = mUserPreference.getAutoshop();
            if (autoshop.getId() != null && !autoshop.getId().equals("")) {
                hitLogin(autoshop.getUsername(), autoshop.getPassword(), EXTRA_AUTOSHOP);
            }
        }else{
            type=EXTRA_CUSTOMER;
            customer = new Customer();
        }


        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inReg = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(inReg);
            }
        });

        rgRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_customer:
                        type = EXTRA_CUSTOMER;
                        break;
                    case R.id.rb_autoshop:
                        type = EXTRA_AUTOSHOP;
                        break;
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = tvUsername.getText().toString();
                String password = tvPassword.getText().toString();

                if (username.isEmpty()) {
                    tvUsername.setError("Username Can't be Empty");
                    tvUsername.requestFocus();
                    Toast.makeText(LoginActivity.this, "Username Can't be Empty", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    tvPassword.setError("Password Can't be Empty");
                    tvPassword.requestFocus();
                    Toast.makeText(LoginActivity.this, "Password Can't be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    hitLogin(username, password, type);
                }
            }
        });
    }

    private void hitLogin(final String username, final String password, final String type) {
        loading = ProgressDialog.show(LoginActivity.this, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json Login", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("STATUS");
                    String message = jo.getString("message");

                    loading.dismiss();

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (response.equals("1")) {
                        if (type.equals(EXTRA_CUSTOMER)){
                            Customer customer = new Customer(jo);
                            saveCustomer(customer);
                            Intent intent = new Intent(LoginActivity.this, HomeCustomerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }if (type.equals(EXTRA_AUTOSHOP)){
                            Autoshop autoshop = new Autoshop(jo);
                            saveAdmin(autoshop);

                            Intent intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put("USERNAME", username);
                params.put("PASSWORD", password);
                params.put("TYPE", type);

                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    void saveCustomer(Customer customer) {
        UserPreference userPreference = new UserPreference(this);
        customer.setId(customer.getId());
        customer.setPassword(customer.getPassword());
        customer.setFullname(customer.getFullname());
        customer.setUsername(customer.getUsername());
        customer.setEmail(customer.getEmail());
        customer.setPhone(customer.getPhone());

        userPreference.setCustomer(customer);
        userPreference.setType(EXTRA_CUSTOMER);
    }

    void saveAdmin(Autoshop autoshop) {
        UserPreference userPreference = new UserPreference(LoginActivity.this);
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
        autoshop.setOpenHours(autoshop.getOpenHours());
        autoshop.setCloseHours(autoshop.getCloseHours());

        userPreference.setAutoshop(autoshop);
        userPreference.setType(EXTRA_AUTOSHOP);
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
