package com.junior.autoshop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junior.autoshop.adapter.AutoshopAdapter;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class ChooseAutoshopFragment extends Fragment implements SelectedAutoshopCallback, OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private ArrayList<Service> listSelectedService;
    public static String EXTRA_SERVICE = "SERVICE";
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
    private String serviceString;
    private int serviceCount;
    private SupportMapFragment mMapFragment;
    private boolean isFound;
    private double userLat, userLong;

    private ArrayList<Autoshop> listAutoshop = new ArrayList<>();
    private ArrayList<Autoshop> listAutoshopToAdapter = new ArrayList<>();

    public ChooseAutoshopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_autoshop, container, false);
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

        listSelectedService = (ArrayList<Service>) getArguments().getSerializable(EXTRA_SERVICE);
        Log.d("selected service", listSelectedService.toString());
        serviceCount = listSelectedService.size();
        for (int i = 0; i < listSelectedService.size(); i++) {
            if (serviceString == null) {
                serviceString = "'" + listSelectedService.get(i).getId() + "'";
            } else serviceString = serviceString + "'" + listSelectedService.get(i).getId() + "'";

            if (i != listSelectedService.size() - 1) serviceString = serviceString + ",";
        }
        Log.d("selected service string", serviceString);

        autoshopAdapter = new AutoshopAdapter(getContext(), listAutoshopToAdapter, this);
        autoshopAdapter.notifyDataSetChanged();
        rvAutoshop.setHasFixedSize(true);
        rvAutoshop.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAutoshop.setAdapter(autoshopAdapter);


        btnSearchAutoshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFound = !isFound;
                if (isFound) {
                    btnSearchAutoshop.setText("Change Location");
                    getAutoshop();
                    flMap.setVisibility(View.GONE);
                    rvAutoshop.setVisibility(View.VISIBLE);
                } else {
                    btnSearchAutoshop.setText("Search AutoShop");
                    flMap.setVisibility(View.VISIBLE);
                    rvAutoshop.setVisibility(View.GONE);
                }
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingDetailFragment bookingDetailFragment = new BookingDetailFragment();
              /*  Bundle mBundle = new Bundle();
                mBundle.putString(DetailCategoryFragment.EXTRA_NAME, "Lifestyle");
                String description = "Kategori ini akan berisi produk-produk lifestyle";
                mDetailCategoryFragment.setArguments(mBundle);
                mDetailCategoryFragment.setDescription(description);*/
                FragmentManager mFragmentManager = getFragmentManager();
                if (mFragmentManager != null) {
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container_layout, bookingDetailFragment, BookingDetailFragment.class.getSimpleName());
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.commit();
                }
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

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {
            @Override
            public void onMarkerDragStart(Marker marker)
            {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                userLat     = marker.getPosition().latitude;
                userLong     = marker.getPosition().longitude;
            }

            @Override
            public void onMarkerDrag(Marker marker)
            {
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
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

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

    @Override
    public void selectAutoshop(Autoshop autoshop) {
        selectedAutoshop = autoshop;
    }

    private void getAutoshop() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_GET_NEARBY_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json nearby autoshop", s);
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
                        calculateDistance(listAutoshop);
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
                params.put("LIST_STRING", serviceString);
                params.put("SERVICE_COUNT", Integer.toString(serviceCount));

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
        updateAdapter(autoshops);
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

}
