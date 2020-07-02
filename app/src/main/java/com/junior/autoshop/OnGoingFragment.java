package com.junior.autoshop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.adapter.OnGoingAdapter;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.Trans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class OnGoingFragment extends Fragment {
    RecyclerView rvOnGoing;
    private OnGoingAdapter onGoingAdapter;
    private Customer customer;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private ArrayList<Trans> listOnGoing = new ArrayList<>();
    private ArrayList<Trans> listOnGoingToAdapter = new ArrayList<>();
    private ArrayList<Trans> listOutdated = new ArrayList<>();
    public OnGoingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_on_going, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvOnGoing = view.findViewById(R.id.rv_ongoing);
        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();

        onGoingAdapter = new OnGoingAdapter(getContext(), listOnGoingToAdapter);
        onGoingAdapter.notifyDataSetChanged();
        rvOnGoing.setHasFixedSize(true);
        rvOnGoing.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOnGoing.setAdapter(onGoingAdapter);

    }

    private void getTrans() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_ONGOING_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json On Going", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();
                    listOnGoing.clear();
                    listOutdated.clear();
                    if (response.equals("1")) {
                        JSONArray transData = jo.getJSONArray("DATA");
                        for (int i = 0; i < transData.length(); i++) {
                            JSONObject object = transData.getJSONObject(i);
                            Trans trans = new Trans(object);

                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date startDate = format.parse(trans.getStartDate());
                                Date currentDate = new Date(System.currentTimeMillis() - 86400000);

                                if (trans.getStatus().equals("ON QUEUE")){
                                    if(currentDate.compareTo(startDate)<=0){
                                        listOnGoing.add(trans);
                                    }else
                                        listOutdated.add(trans);
                                } else listOnGoing.add(trans);
                            } catch (ParseException e) {
                            }
                        }
                    } else {
                        String message = jo.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    updateAdapter(listOnGoing);
                    if(listOutdated.size()>0) changeStatusOutdated();

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
                params.put("CUSTOMER_ID", customer.getId());

                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void updateAdapter(ArrayList<Trans> list) {
        listOnGoingToAdapter.clear();
        listOnGoingToAdapter.addAll(list);
        onGoingAdapter.notifyDataSetChanged();
    }

    private void changeStatusOutdated() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_CHANGE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json status:cancel", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    if(response.equals("1")){
                        listOutdated.remove(0);
                        if(listOutdated.size()>0) changeStatusOutdated();
                    } else Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getContext().getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", listOutdated.get(0).getId());
                params.put("STATUS", "CANCELLED");
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    @Override
    public void onResume() {
        super.onResume();
        handleSSLHandshake();
        getTrans();
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
