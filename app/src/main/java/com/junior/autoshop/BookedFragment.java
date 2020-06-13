package com.junior.autoshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.adapter.BookedAdapter;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Trans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.Manifest.permission.CAMERA;


public class BookedFragment extends Fragment {
    private static String SCAN_MODE = "SCAN_MODE";
    private static String BARCODE_MODE = "BARCODE_MODE";
    private static String SCAN_RESULT = "SCAN_RESULT";
    private final static int BARCODE_REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA = 1;
    private RecyclerView rvBooked;
    private Button btnScan;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private BookedAdapter bookedAdapter;
    private Autoshop autoshop;
    private String qrcode;
    private ArrayList<Trans> listBooked = new ArrayList<>();
    private ArrayList<Trans> listBookedToAdapter = new ArrayList<>();

    public BookedFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_booked, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnScan = view.findViewById(R.id.btn_scan_qr);
        rvBooked = view.findViewById(R.id.rv_booked);

        mUserPreference = new UserPreference(getContext());
        autoshop = mUserPreference.getAutoshop();

        bookedAdapter = new BookedAdapter(getContext(), listBookedToAdapter);
        bookedAdapter.notifyDataSetChanged();
        rvBooked.setHasFixedSize(true);
        rvBooked.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBooked.setAdapter(bookedAdapter);

        handleSSLHandshake();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent scanmember = new Intent(getContext(), QRScanActivity.class);
                    scanmember.putExtra(SCAN_MODE, BARCODE_MODE);
                    startActivityForResult(scanmember, BARCODE_REQUEST_CODE);
                } else checkPermission();

            }
        });
        getTransBooked();
    }

    private void getTransBooked() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_GET_BOOKED_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json Booked", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    listBooked.clear();
                    if (response.equals("1")) {
                        JSONArray transData = jo.getJSONArray("DATA");
                        for (int i = 0; i < transData.length(); i++) {
                            JSONObject object = transData.getJSONObject(i);
                            Trans trans = new Trans(object);
                            listBooked.add(trans);
                        }
                    } else {
                        String message = jo.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    updateAdapter(listBooked);

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

    private void updateAdapter(ArrayList<Trans> list) {
        listBookedToAdapter.clear();
        listBookedToAdapter.addAll(list);
        bookedAdapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BARCODE_REQUEST_CODE) {
                qrcode = intent.getStringExtra(SCAN_RESULT);
                acceptBooking();
            }
        }
    }

    private void acceptBooking() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_ACCEPT_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json accept trans", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    loading.dismiss();
                    if (response.equals("1")) {
                        Toast.makeText(getContext(), "Booking accepted", Toast.LENGTH_SHORT).show();
                        getTransBooked();
                    } else
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getContext().getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", qrcode);
                params.put("STATUS", "ON PROGRESS");
                params.put("SPACE", autoshop.getSpace());
                params.put("AUTOSHOP_ID", autoshop.getId());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private boolean checkPermission() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
