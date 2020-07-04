package com.junior.autoshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.junior.autoshop.adapter.AutoshopAdapter;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SosFragment extends Fragment implements SelectedAutoshopCallback, FavoriteAutoshopCallback, OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static String EXTRA_SERVICE = "SERVICE";
    public static String EXTRA_AUTOSHOP = "AUTOSHOP";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private FrameLayout flMap;
    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Autoshop selectedAutoshop;
    MarkerOptions markerOptions;
    private UserPreference mUserPreference;
    private RecyclerView rvAutoshop;
    private ProgressDialog loading;
    AutoshopAdapter autoshopAdapter;
    Button btnSearchAutoshop;
    private Customer customer;
    private SupportMapFragment mMapFragment;
    private boolean isSet;
    private double userLat, userLong;
    private LinearLayout llAutoshop;
    private ImageView imgPrev, imgNext;
    private TextView tvDistanceZone;
    private double currentZone;
    private Button btnFavorite, btnSetLocation;
    private boolean isFavorite;

    private ArrayList<Autoshop> listAutoshop = new ArrayList<>();
    private ArrayList<Autoshop> listFavorite = new ArrayList<>();
    private ArrayList<Autoshop> listSelectedAutoshop = new ArrayList<>();
    private ArrayList<Autoshop> listAutoshopToAdapter = new ArrayList<>();
    private ArrayList<Autoshop> listPrev = new ArrayList<>();
    private ArrayList<Autoshop> listCurrent = new ArrayList<>();
    private ArrayList<Autoshop> listNext = new ArrayList<>();

    public SosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sos, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button btnProceed = view.findViewById(R.id.btn_proceed);
        btnSearchAutoshop = view.findViewById(R.id.btn_search_autoshop);
        rvAutoshop = view.findViewById(R.id.rv_autoshop);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        flMap = view.findViewById(R.id.fl_map);
        llAutoshop = view.findViewById(R.id.ll_autoshop);
        imgPrev = view.findViewById(R.id.img_prev);
        imgNext = view.findViewById(R.id.img_next);
        tvDistanceZone = view.findViewById(R.id.txt_distance_zone);
        btnFavorite = view.findViewById(R.id.btn_search_favorite);
        btnSetLocation = view.findViewById(R.id.btn_set_location);

        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();
        currentZone = 10;

        handleSSLHandshake();

        llAutoshop.setVisibility(View.GONE);

        autoshopAdapter = new AutoshopAdapter(getContext(), listAutoshopToAdapter, this, this);
        autoshopAdapter.notifyDataSetChanged();
        rvAutoshop.setHasFixedSize(true);
        rvAutoshop.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAutoshop.setAdapter(autoshopAdapter);

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSet = !isSet;
                if (isSet) {
                    btnSetLocation.setText("Change Location");
                    getAutoshop();
                    flMap.setVisibility(View.GONE);
                    rvAutoshop.setVisibility(View.VISIBLE);
                    llAutoshop.setVisibility(View.VISIBLE);
                    btnFavorite.setVisibility(View.VISIBLE);
                    btnSearchAutoshop.setVisibility(View.VISIBLE);
                } else {
                    btnSetLocation.setText("Set Location");
                    flMap.setVisibility(View.VISIBLE);
                    rvAutoshop.setVisibility(View.GONE);
                    llAutoshop.setVisibility(View.GONE);
                    btnFavorite.setVisibility(View.GONE);
                    btnSearchAutoshop.setVisibility(View.GONE);
                }
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = true;
                getFavoriteList();
                llAutoshop.setVisibility(View.GONE);
            }
        });

        btnSearchAutoshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite =false;
                getAutoshop();
                llAutoshop.setVisibility(View.VISIBLE);
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listSelectedAutoshop.size() < 1) {
                    Toast.makeText(getContext(), "Please select a workshop!", Toast.LENGTH_SHORT).show();
                } else if (listSelectedAutoshop.size() > 1) {
                    Toast.makeText(getContext(), "Please select only 1 workshop!", Toast.LENGTH_SHORT).show();
                } else {
                    String latlong = userLat + "," + userLong;

                    SosDetailFragment sosDetailFragment = new SosDetailFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable(ChooseAutoshopFragment.EXTRA_AUTOSHOP, selectedAutoshop);
                    mBundle.putString("LATLONG", latlong);
                    sosDetailFragment.setArguments(mBundle);
                    FragmentManager mFragmentManager = getFragmentManager();
                    if (mFragmentManager != null) {
                        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.replace(R.id.container_layout, sosDetailFragment, BookingDetailFragment.class.getSimpleName());
                        mFragmentTransaction.addToBackStack(BookingDetailFragment.class.getSimpleName());
                        mFragmentTransaction.commit();
                    }
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNearestZone(2);
                //zoning(listAutoshop);
            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNearestZone(1);
                //zoning(listAutoshop);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                userLat = marker.getPosition().latitude;
                userLong = marker.getPosition().longitude;
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
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
        markerOptions.title("Current Position");
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
        markerOptions.icon(icon);
        markerOptions.anchor(0.5f, 1.0f);
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        userLat = location.getLatitude();
        userLong = location.getLongitude();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getNearestZone(int zone){
        if(zone==1){
            double nearest = listPrev.get(0).getDistance();
            for (int i = 0; i< listPrev.size();i++){
                if (listPrev.get(i).getDistance()> nearest){
                    nearest = listPrev.get(i).getDistance();
                }
            }
            double mod = 10 - (nearest%10);
            currentZone = nearest + mod;
            if(currentZone<10) {
                currentZone = 10;
            }
        }else{
            double nearest = listNext.get(0).getDistance();
            for (int i = 0; i< listNext.size();i++){
                if (listNext.get(i).getDistance()< nearest){
                    nearest = listNext.get(i).getDistance();
                }
            }
            double mod = 10 - (nearest%10);
            currentZone = nearest+ mod;
        }
        zoning(listAutoshop);
    }

    private void zoning(ArrayList<Autoshop> autoshops) {
        listCurrent.clear();
        listNext.clear();
        listPrev.clear();
        for (int i = 0; i < autoshops.size(); i++) {
            if (autoshops.get(i).getDistance() < currentZone && currentZone == 10) {
                listCurrent.add(autoshops.get(i));
            } else if (autoshops.get(i).getDistance() < currentZone && autoshops.get(i).getDistance() > currentZone - 10) {
                listCurrent.add(autoshops.get(i));
            } else if (autoshops.get(i).getDistance() > currentZone) {
                listNext.add(autoshops.get(i));
            } else {
                listPrev.add(autoshops.get(i));
            }

            /*if (i == autoshops.size() - 1 && listCurrent.size() == 0) {
                i = 0;
                currentZone += 10;
            }*/
        }
        if(listPrev.size()<1){
            imgPrev.setVisibility(View.INVISIBLE);
        }else imgPrev.setVisibility(View.VISIBLE);

        if(listNext.size()<1){
            imgNext.setVisibility(View.INVISIBLE);
        }else imgNext.setVisibility(View.VISIBLE);

        tvDistanceZone.setText("Radius: " + currentZone + " Km");
        updateAdapter(listCurrent);
    }


    private void getFavoriteList() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_FAVORITE_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json fav list", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    listFavorite.clear();
                    if (response.equals("1")) {
                        JSONArray dataAutoshop = jo.getJSONArray("DATA");
                        for (int i = 0; i < dataAutoshop.length(); i++) {
                            JSONObject object = dataAutoshop.getJSONObject(i);
                            Autoshop autoshop = new Autoshop(object);
                            boolean isAvail = false;
                            for(int j=0; j<listAutoshop.size();j++){
                                if(autoshop.getId().equals(listAutoshop.get(j).getId())){
                                    isAvail=true;
                                    break;
                                }
                            }
                            if (isAvail){
                                autoshop.setAvailable(true);
                            } else autoshop.setAvailable(false);
                            autoshop.setInFavorite(true);
                            autoshop.setFavorite(true);
                            listFavorite.add(autoshop);
                        }
                        calculateDistance(listFavorite);
                        //updateAdapter(listAutoshop);

                    }  else {
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
                params.put("CUSTOMER_ID", customer.getId());
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String time = dateFormat.format(c);
                params.put("TIME", time);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void getAutoshop() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_SOS_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json SOS autoshop", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listAutoshop.clear();

                        JSONArray dataAutoshop = jo.getJSONArray("DATA");
                        for (int i = 0; i < dataAutoshop.length(); i++) {
                            JSONObject object = dataAutoshop.getJSONObject(i);
                            Autoshop autoshop = new Autoshop(object);
                            listAutoshop.add(autoshop);
                        }
                        if (listAutoshop.size() > 0) calculateDistance(listAutoshop);
                        //updateAdapter(listAutoshop);

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
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String time = dateFormat.format(c);
                params.put("TIME", time);
                Log.d("param", time);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    private void getFavorite(final ArrayList<Autoshop> autoshops) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_FAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json get Favorite", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        JSONArray dataAutoshop = jo.getJSONArray("DATA");
                        for (int i = 0; i < dataAutoshop.length(); i++) {
                            JSONObject object = dataAutoshop.getJSONObject(i);
                            String autoshopId = object.getString("AUTOSHOP_ID");
                            String favId = object.getString("FAVORITE_ID");
                            for (int j = 0; j < autoshops.size(); j++) {
                                if (autoshops.get(j).getId().equals(autoshopId)) {
                                    autoshops.get(j).setFavorite(true);
                                    autoshops.get(j).setFavoriteId(favId);
                                    break;
                                }
                            }
                        }
                    } else {
                        Log.d("tag", "no fav");
                    }
                    listNext.addAll(autoshops);
                    getNearestZone(2);
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

    private void updateAdapter(ArrayList<Autoshop> list) {
        listAutoshopToAdapter.clear();
        listAutoshopToAdapter.addAll(list);
        autoshopAdapter.notifyDataSetChanged();
    }

    private void calculateDistance(ArrayList<Autoshop> autoshops) {

        Log.d("loc", userLat + ", " + userLong);
        for (int i = 0; i < autoshops.size(); i++) {
            String[] arrOfStr = autoshops.get(i).getLatlong().split(",");
            double shopLat = Double.parseDouble(arrOfStr[0]);
            double shopLong = Double.parseDouble(arrOfStr[1]);

            autoshops.get(i).setDistance(distance(userLat, userLong, shopLat, shopLong));
        }
        Collections.sort(autoshops);
        if (isFavorite){
            updateAdapter(autoshops);
        }else{
            getFavorite(autoshops);
        }

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


    @Override
    public void selectAutoshop(Autoshop autoshop) {
        for (int i = 0; i < listAutoshopToAdapter.size(); i++) {
            if (autoshop.getId().equals(listAutoshopToAdapter.get(i).getId())) {
                selectedAutoshop = listAutoshopToAdapter.get(i);
                listSelectedAutoshop.add(selectedAutoshop);
                break;
            }
        }
    }

    @Override
    public void deleteAutoshop(Autoshop autoshop) {
        for (int i = 0; i < listSelectedAutoshop.size(); i++) {
            if (autoshop.getId().equals(listSelectedAutoshop.get(i).getId())) {
                listSelectedAutoshop.remove(i);
            }
        }
    }

    @Override
    public void favoriteAutoshop(Autoshop autoshop, String favId) {
        for (int i = 0; i < listAutoshopToAdapter.size(); i++) {
            if (autoshop.getId().equals(listAutoshopToAdapter.get(i).getId())) {
                listAutoshopToAdapter.get(i).setFavorite(true);
                listAutoshopToAdapter.get(i).setFavoriteId(favId);
                break;
            }
        }
    }

    @Override
    public void unfavoriteAutoshop(Autoshop autoshop) {
        for (int i = 0; i < listAutoshopToAdapter.size(); i++) {
            if (autoshop.getId().equals(listAutoshopToAdapter.get(i).getId())) {
                listAutoshopToAdapter.get(i).setFavorite(false);
                break;
            }
        }
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
