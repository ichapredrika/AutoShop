package com.junior.autoshop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.adapter.ServiceAdapter;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BookingFragment extends Fragment implements SelectedServiceCallback {

    private ServiceAdapter serviceAdapter;
    private UserPreference mUserPreference;
    private Customer customer;
    private RecyclerView rvService;
    private ProgressDialog loading;
    private int counter=0;
    private ArrayList<Service> listService = new ArrayList<>();
    private ArrayList<Service> listServiceToAdapter = new ArrayList<>();
    private ArrayList<Service> listSelectedService = new ArrayList<>();
    public BookingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();
        rvService = view.findViewById(R.id.rv_service);
        Button btnProceed = view.findViewById(R.id.btn_proceed);

        handleSSLHandshake();
        serviceAdapter = new ServiceAdapter(getContext(), listServiceToAdapter, this);
        serviceAdapter.notifyDataSetChanged();
        rvService.setHasFixedSize(true);
        rvService.setLayoutManager(new LinearLayoutManager(getContext()));
        rvService.setAdapter(serviceAdapter);

        if (counter==0) getService();
        counter=1;

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listSelectedService.size()>0){
                    ChooseAutoshopFragment chooseAutoshopFragment = new ChooseAutoshopFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(ChooseAutoshopFragment.EXTRA_SERVICE, listSelectedService);
                    chooseAutoshopFragment.setArguments(mBundle);
                    FragmentManager mFragmentManager = getFragmentManager();
                    if (mFragmentManager != null) {
                        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.replace(R.id.container_layout, chooseAutoshopFragment, ChooseAutoshopFragment.class.getSimpleName());
                        mFragmentTransaction.addToBackStack(ChooseAutoshopFragment.class.getSimpleName());
                        mFragmentTransaction.commit();
                    }
                }else Toast.makeText(getContext(), "Please choose a service!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getService() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.GET, PhpConf.URL_GET_SERVICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json service", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listService.clear();
                        JSONArray dataService = jo.getJSONArray("DATA");
                        for (int i = 0; i < dataService.length(); i++) {
                            JSONObject object = dataService.getJSONObject(i);
                            Service service = new Service(object);
                            listService.add(service);
                        }
                        updateAdapter(listService);

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
        });
        mRequestQueue.add(mStringRequest);
    }

    private void updateAdapter(ArrayList<Service> list) {
        listServiceToAdapter.clear();
        listServiceToAdapter.addAll(list);
        serviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectService(Service service) {
        listSelectedService.add(service);
        for(int i=0; i< listService.size();i++){
            if (service.getId().equals(listService.get(i).getId())){
                listService.get(i).setNote(service.getNote());
                listService.get(i).setSelected(true);
            }
        }
        updateAdapter(listService);
        Log.d("selectedService", listSelectedService.toString());
    }

    @Override
    public void deleteService(Service service) {
        for(int i=0; i< listSelectedService.size();i++){
            if (service.getId().equals(listSelectedService.get(i).getId())){
                listSelectedService.remove(i);
            }
        }
        for(int i=0; i< listService.size();i++){
            if (service.getId().equals(listService.get(i).getId())){
                listService.get(i).setNote(service.getNote());
                listService.get(i).setSelected(false);
            }
        }
        updateAdapter(listService);
        Log.d("selectedService", listSelectedService.toString());
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

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter(listService);
    }

}
