package com.junior.autoshop;

import android.app.Dialog;
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
import com.junior.autoshop.adapter.VehicleCustomerAdapter;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.TransOngoing;
import com.junior.autoshop.models.Vehicle;
import com.junior.autoshop.models.VehicleCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class OnGoingFragment extends Fragment {
    RecyclerView rvOnGoing;
    private OnGoingAdapter onGoingAdapter;
    private Customer customer;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private ArrayList<TransOngoing> listOnGoing = new ArrayList<>();
    private ArrayList<TransOngoing> listOnGoingToAdapter = new ArrayList<>();
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

        getTransOnGoing();
    }

    private void getTransOnGoing() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_GET_ONGOING_TRANS, new Response.Listener<String>() {
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

                    if (response.equals("1")) {
                        listOnGoing.clear();

                        JSONArray transData = jo.getJSONArray("DATA");
                        for (int i = 0; i < transData.length(); i++) {
                            JSONObject object = transData.getJSONObject(i);
                            TransOngoing transOngoing = new TransOngoing(object);
                            listOnGoing.add(transOngoing);
                        }
                        updateAdapter(listOnGoing);

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
                params.put("CUSTOMER_ID", customer.getId());

                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void updateAdapter(ArrayList<TransOngoing> list) {
        listOnGoingToAdapter.clear();
        listOnGoingToAdapter.addAll(list);
        onGoingAdapter.notifyDataSetChanged();
    }
}
