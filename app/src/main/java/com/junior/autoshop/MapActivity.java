package com.junior.autoshop;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Trans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.junior.autoshop.ChooseAutoshopFragment.EXTRA_AUTOSHOP;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Location mLastLocation;
    Autoshop autoshop;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    MarkerOptions markerOptions;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public final static String EXTRA_ORIGIN = "ORIGIN";
    public final static String EXTRA_PROFILE = "PROFILE";
    public final static String EXTRA_PICKUP = "PICKUP";

    private UserPreference mUserPreference;

    private ProgressDialog loading;
    private TextView tvFee;
    private String origin;
    private LatLng position;
    private Trans trans;
    private DecimalFormat df = new DecimalFormat("#,###.###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mUserPreference = new UserPreference(MapActivity.this);
        autoshop = mUserPreference.getAutoshop();

        Button btnChangeLoc = findViewById(R.id.btn_change_loc);
        ImageView imgClose = findViewById(R.id.img_close);
        tvFee = findViewById(R.id.txt_pickup_fee);

        handleSSLHandshake();
        Intent intent = getIntent();
        origin = intent.getStringExtra(EXTRA_ORIGIN);
        if (origin.equals(EXTRA_PROFILE)) {
            tvFee.setVisibility(View.GONE);
        } else {
            trans = getIntent().getParcelableExtra(EXTRA_AUTOSHOP);
            tvFee.setVisibility(View.VISIBLE);

            double price = 0;
            if (trans.getDeliveryFee().equals("") ) {
                price = 0;
            } else price = Double.parseDouble(trans.getDeliveryFee());
            tvFee.setText("Delivery Fee/Km: " + getString(R.string.amount_parse, df.format(price)));
        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnChangeLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin.equals(EXTRA_PROFILE)) {
                    LatLng position = markerOptions.getPosition();
                    String latlong = position.latitude + "," + position.longitude;
                    updateLoc(latlong);
                } else {
                    position = markerOptions.getPosition();
                    String latlong = position.latitude + "," + position.longitude;
                    updateDeliveryLoc(latlong);
                }
            }
        });
    }

    private void updateLoc(final String latlong) {
        loading = ProgressDialog.show(MapActivity.this, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(MapActivity.this);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPDATE_LOCATION_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json update LOC", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        finish();
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
                Toast.makeText(MapActivity.this, getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("AUTOSHOP_ID", autoshop.getId());
                params.put("LATLONG", latlong);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void updateDeliveryLoc(final String latlong) {
        loading = ProgressDialog.show(MapActivity.this, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(MapActivity.this);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPDATE_LOCATION_DELIVERY, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json update DelivLoc", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        String[] arrOfStr = trans.getAutoshopLatlong().split(",");
                        double shopLat = Double.parseDouble(arrOfStr[0]);
                        double shopLong = Double.parseDouble(arrOfStr[1]);
                        double distance = distance(shopLat, shopLong, position.latitude, position.longitude);
                        double price;
                        if(trans.getDeliveryFee().equals("")){
                            price = 0;
                        }else price = distance * Double.parseDouble(trans.getDeliveryFee());

                        addCost(Double.toString(price), "Delivery Fee");
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
                Toast.makeText(MapActivity.this, getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", trans.getId());
                params.put("LATLONG", latlong);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;

            dist = dist * 1.609344;

            return (dist);
        }
    }

    private void addCost(final String price, final String serviceAct) {
        loading = ProgressDialog.show(this, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_ADD_TRANS_COST, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json add trans cost", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    String response = jo.getString("response");
                    loading.dismiss();
                    String message = jo.getString("message");
                    Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (response.equals("1")) {
                        finish();
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
                Toast.makeText(MapActivity.this, getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                double total = 0;
                if (trans.getTotalPrice().equals("null")) {
                    total = Double.parseDouble(price);
                } else {
                    total = Double.parseDouble(trans.getTotalPrice()) + Double.parseDouble(price);
                }

                params.put("TRANSACTION_ID", trans.getId());
                params.put("SERVICE_ACT", serviceAct);
                params.put("TOTAL_PRICE", Double.toString(total));
                params.put("PRICE", price);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MapActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng).draggable(true);
        markerOptions.title("Drag me to your location!");
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
        markerOptions.icon(icon);
        markerOptions.anchor(0.5f, 1.0f);
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
