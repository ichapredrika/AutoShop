package com.junior.autoshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    private int spaceNumber;
    private RecyclerView rvBooked;
    private Button btnScan;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private ImageView imgInfo;
    private Dialog popUpDialog;
    private BookedAdapter bookedAdapter;
    private Autoshop autoshop;
    private ArrayList<String> occupiedSpaces = new ArrayList<>();
    private String qrcode;
    private ArrayList<Trans> listBooked = new ArrayList<>();
    private ArrayList<Trans> listBookedToAdapter = new ArrayList<>();
    private ArrayList<Trans> listOutdated = new ArrayList<>();

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
        imgInfo = view.findViewById(R.id.img_info);

        popUpDialog = new Dialog(getContext());
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

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpInfo();
            }
        });
        getTransBooked();
    }

    private void popUpInfo() {
        popUpDialog.setContentView(R.layout.pop_up_info);

        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.dismiss();
            }
        });

        if (popUpDialog.getWindow() != null) {
            popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popUpDialog.show();
    }

    private void getTransBooked() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_BOOKED_TRANS, new Response.Listener<String>() {
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

                                if(currentDate.compareTo(startDate)<=0 || trans.getStatus().equals("REISSUE")){
                                    listBooked.add(trans);
                                }else listOutdated.add(trans);
                            } catch (ParseException e) {
                            }
                            if(listOutdated.size()>0) changeStatusOutdated();
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
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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

    private void updateAdapter(ArrayList<Trans> list) {
        ArrayList<Trans> transactions = sort(list);
        listBookedToAdapter.clear();
        listBookedToAdapter.addAll(transactions);
        bookedAdapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BARCODE_REQUEST_CODE) {
                qrcode = intent.getStringExtra(SCAN_RESULT);
                getOccupiedSpace();
            }
        }
    }

    public ArrayList<Trans> sort(ArrayList<Trans> trans){
        ArrayList<Trans> sos = new ArrayList<>();
        ArrayList<Trans> reissue = new ArrayList<>();
        ArrayList<Trans> regular = new ArrayList<>();
        ArrayList<Trans> autoshopPickup = new ArrayList<>();
        ArrayList<Trans> selfDelivery = new ArrayList<>();
        ArrayList<Trans> result = new ArrayList<>();
        for (int i=0; i<trans.size();i++){
            if(trans.get(i).getStatus().equals("REISSUE")){
                reissue.add(trans.get(i));
            }else if(trans.get(i).getType().equals("SOS")){
                sos.add(trans.get(i));
            }else {
                regular.add(trans.get(i));
            }
        }

        for (int j=0; j<regular.size();j++){
            if(regular.get(j).getMovementOption().equals("AUTOSHOP PICKUP")){
                autoshopPickup.add(regular.get(j));
            }else selfDelivery.add(regular.get(j));
        }

        result.addAll(reissue);
        result.addAll(sos);
        result.addAll(autoshopPickup);
        result.addAll(selfDelivery);
        return result;
    }

    private void getOccupiedSpace() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_OCCUPIED_SPACE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json occupied space", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    loading.dismiss();
                    occupiedSpaces.clear();
                    if (response.equals("1")) {
                        JSONArray spaces = jo.getJSONArray("DATA");
                        for (int i = 0; i < spaces.length(); i++) {
                            JSONObject object = spaces.getJSONObject(i);
                            occupiedSpaces.add(object.getString("SPACE_NUMBER"));
                        }
                    }else spaceNumber = assignSpace();
                    spaceNumber = assignSpace();
                    if (spaceNumber == 0) {
                        Toast.makeText(getContext(), "Working Space is full", Toast.LENGTH_SHORT).show();
                    } else {
                        acceptBooking(spaceNumber);
                    }
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
                params.put("AUTOSHOP_ID", autoshop.getId());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private int assignSpace() {
        int autoshopSpace = Integer.parseInt(autoshop.getSpace());
        if (occupiedSpaces.size() == 0) {
            return 1;
        }
        if (occupiedSpaces.size() == autoshopSpace) {
            return 0;
        } else {
            for (int i = 1; i <= autoshopSpace; i++) {
                for (int j = 0; j < occupiedSpaces.size(); j++) {
                    if (Integer.toString(i).equals(occupiedSpaces.get(j))) {
                        break;
                    }
                    if (j == occupiedSpaces.size() - 1) {
                        return i;
                    }
                }
            }
        }
        return 0;
    }

    private void acceptBooking(final int spaceNumber) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...",
                false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_ACCEPT_TRANS,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json accept trans", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    loading.dismiss();
                    if (response.equals("1")) {
                        Trans trans = new Trans();
                        for(int j=0; j<listBooked.size();j++){
                            if (qrcode.equals(listBooked.get(j).getId())){
                                trans = listBooked.get(j);
                            }
                        }
                        List<String> toEmailList = new ArrayList<>();
                        toEmailList.add(trans.getCustomerEmail());
                        Log.i("SendMailActivity", "To List: " + toEmailList);
                        String emailSubject = "Order Accepted";
                        String emailBody = "Your order has been accepted as detailed below:\n" +
                                "Autoshop: "+trans.getAutoshopName()+"\n"+
                                "Order Date: "+trans.getStartDate()+"\n"+
                                "Type : " + trans.getType()+"\n\n"+
                                "Check Autoshop App now!";
                        new SendMailTask(getActivity()).execute(getActivity().getString(R.string.autoshop_email),
                                getActivity().getString(R.string.autoshop_password), toEmailList, emailSubject, emailBody);

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
                params.put("SPACE_NUMBER", Integer.toString(spaceNumber));
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
