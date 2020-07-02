package com.junior.autoshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.adapter.VehicleCustomerAdapter;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.Vehicle;
import com.junior.autoshop.models.VehicleCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private static final String EXTRA_CUSTOMER = "CUSTOMER";
    private ImageView imgEditProfile, imgAddVehicle;
    private TextView tvFullname, tvUsername, tvEmail, tvPhone;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private VehicleCustomerAdapter vehicleCustomerAdapter;
    private Dialog popUpDialog;
    private RecyclerView rvVehicle;
    private Customer customer;
    private ArrayAdapter<String> adapterBrandModel;

    private ArrayList<Vehicle> listVehicleAll = new ArrayList<>();
    private ArrayList<String> listVehicleToAdapter = new ArrayList<>();
    private ArrayList<VehicleCustomer> listVehicleCustomer = new ArrayList<>();
    private ArrayList<VehicleCustomer> listVehicleCustomerToAdapter = new ArrayList<>();

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        imgAddVehicle = view.findViewById(R.id.img_add_vehicle);
        imgEditProfile = view.findViewById(R.id.img_edit_profile);
        tvFullname = view.findViewById(R.id.txt_fullname);
        tvUsername = view.findViewById(R.id.txt_username);
        tvEmail = view.findViewById(R.id.txt_email);
        tvPhone = view.findViewById(R.id.txt_phone);
        rvVehicle = view.findViewById(R.id.rv_vehicle);

        popUpDialog = new Dialog(getContext());
        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();
        Log.d("tag", customer.getId());

        vehicleCustomerAdapter = new VehicleCustomerAdapter(getContext(), listVehicleCustomerToAdapter);
        vehicleCustomerAdapter.notifyDataSetChanged();
        rvVehicle.setHasFixedSize(true);
        rvVehicle.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVehicle.setAdapter(vehicleCustomerAdapter);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                UserPreference mUserPreference = new UserPreference(getContext());
                mUserPreference.logoutUser();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpEditProfile();
            }
        });

        imgAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpAddVehicle();
            }
        });

        getProfile();
    }

    void updateUi(Customer customer) {
        tvFullname.setText(customer.getFullname());
        tvUsername.setText(customer.getUsername());
        tvEmail.setText(customer.getEmail());
        tvPhone.setText(customer.getPhone());
    }

    private void popUpEditProfile() {
        popUpDialog.setContentView(R.layout.pop_up_edit_profile_customer);

        final TextView tvFullname = popUpDialog.findViewById(R.id.txt_fullname);
        final TextView tvUsername = popUpDialog.findViewById(R.id.txt_username);
        final TextView tvEmail = popUpDialog.findViewById(R.id.txt_email);
        final TextView tvPhone = popUpDialog.findViewById(R.id.txt_phone_number);

        tvFullname.setText(customer.getFullname());
        tvUsername.setText(customer.getUsername());
        tvEmail.setText(customer.getEmail());
        tvPhone.setText(customer.getPhone());

        Button btnUpdate = popUpDialog.findViewById(R.id.btn_update);
        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvFullname.getText().toString().trim().isEmpty()) {
                    tvFullname.setError("Full Name Can't be Empty!");
                    tvFullname.requestFocus();
                } else if (tvUsername.getText().toString().trim().isEmpty()) {
                    tvUsername.setError("Username Can't be Empty!");
                    tvUsername.requestFocus();
                } else if (tvEmail.getText().toString().trim().isEmpty()) {
                    tvEmail.setError("Email Field Can't be Empty!");
                    tvEmail.requestFocus();
                } else if (!tvEmail.getText().toString().trim().contains("@") || !tvEmail.getText().toString().trim().contains(".com")) {
                    tvEmail.setError("Email is not valid!");
                    tvEmail.requestFocus();
                } else if (tvPhone.getText().toString().trim().isEmpty()) {
                    tvPhone.setError("Phone Number Field Can't be Empty!");
                    tvPhone.requestFocus();
                } else {
                    Customer newCustomer = new Customer();
                    newCustomer.setId(customer.getId());
                    newCustomer.setFullname(tvFullname.getText().toString().trim());
                    newCustomer.setUsername(tvUsername.getText().toString().trim());
                    newCustomer.setEmail(tvEmail.getText().toString().trim());
                    newCustomer.setPhone(tvPhone.getText().toString().trim());

                    updateProfile(newCustomer);
                    popUpDialog.dismiss();
                }

            }
        });

        if (popUpDialog.getWindow() != null) {
            popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popUpDialog.show();
    }

    private void popUpAddVehicle() {
        popUpDialog.setContentView(R.layout.pop_up_add_vehicle);

        getVehicle();

        final TextView tvName = popUpDialog.findViewById(R.id.txt_vehicle_name);
        final Spinner spBrandModel = popUpDialog.findViewById(R.id.sp_brand_model);
        final TextView tvYear = popUpDialog.findViewById(R.id.txt_year);
        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
        Button btnAddVehicle = popUpDialog.findViewById(R.id.btn_add_vehicle);

        adapterBrandModel = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listVehicleToAdapter);
        adapterBrandModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBrandModel.setAdapter(adapterBrandModel);

        btnAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvName.getText().toString().trim().isEmpty()) {
                    tvName.setError("Name Can't be Empty!");
                    tvName.requestFocus();
                }if (tvYear.getText().toString().trim().isEmpty()) {
                    tvYear.setError("Production Year Can't be Empty!");
                    tvYear.requestFocus();
                } else {
                    VehicleCustomer newVehicleCustomer = new VehicleCustomer();
                    newVehicleCustomer.setName(tvName.getText().toString().trim());
                    newVehicleCustomer.setYear(tvYear.getText().toString().trim());

                    for(int i=0;i<listVehicleAll.size();i++){
                        if (spBrandModel.getSelectedItem().equals(listVehicleAll.get(i).getBrandModel())){
                            newVehicleCustomer.setVehicleId(listVehicleAll.get(i).getId());
                        }
                    }
                    addVehicleCustomer(newVehicleCustomer);
                    popUpDialog.dismiss();
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.dismiss();
                getProfile();
            }
        });

        if (popUpDialog.getWindow() != null) {
            popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popUpDialog.show();
    }

    private void getProfile() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_PROFILE_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json profile", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listVehicleCustomer.clear();
                        JSONObject profile = jo.getJSONArray("PROFILE").getJSONObject(0);
                        Customer customer = new Customer(profile);
                        saveCustomer(customer);
                        updateUi(customer);

                        JSONArray vehicle = jo.getJSONArray("VEHICLE");
                        for (int i = 0; i < vehicle.length(); i++) {
                            JSONObject object = vehicle.getJSONObject(i);
                            VehicleCustomer vehicleCustomer = new VehicleCustomer(object);
                            listVehicleCustomer.add(vehicleCustomer);
                        }
                        updateAdapter(listVehicleCustomer);

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

    private void updateAdapter(ArrayList<VehicleCustomer> list) {
        listVehicleCustomerToAdapter.clear();
        listVehicleCustomerToAdapter.addAll(list);
        vehicleCustomerAdapter.notifyDataSetChanged();
    }

    void saveCustomer(Customer customer) {
        UserPreference userPreference = new UserPreference(getContext());
        customer.setId(customer.getId());
        customer.setPassword(customer.getPassword());
        customer.setFullname(customer.getFullname());
        customer.setUsername(customer.getUsername());
        customer.setEmail(customer.getEmail());
        customer.setPhone(customer.getPhone());

        userPreference.setCustomer(customer);
        userPreference.setType(EXTRA_CUSTOMER);

        this.customer = mUserPreference.getCustomer();
    }

    private void updateProfile(final Customer customer) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPDATE_PROFILE_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json update profile", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        getProfile();
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
                params.put("FULLNAME", customer.getFullname());
                params.put("USERNAME", customer.getUsername());
                params.put("EMAIL", customer.getEmail());
                params.put("PHONE", customer.getPhone());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void addVehicleCustomer(final VehicleCustomer vehicleCustomer) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_ADD_VEHICLE_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json add vehicle", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        getProfile();
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
                params.put("VEHICLE_ID", vehicleCustomer.getVehicleId());
                params.put("CUSTOMER_ID", customer.getId());
                params.put("VEHICLE_NAME", vehicleCustomer.getName());
                params.put("PRODUCTION_YEAR", vehicleCustomer.getYear());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void getVehicle() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.GET, PhpConf.URL_GET_VEHICLE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json vehicle", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listVehicleAll.clear();
                        listVehicleToAdapter.clear();
                        JSONArray service = jo.getJSONArray("DATA");
                        for (int i = 0; i < service.length(); i++) {
                            JSONObject object = service.getJSONObject(i);
                            Vehicle vehicle = new Vehicle(object);
                            String brandModel = vehicle.getBrand() + "-" + vehicle.getModel();
                            vehicle.setBrandModel(brandModel);
                            listVehicleToAdapter.add(brandModel);
                            listVehicleAll.add(vehicle);
                        }
                        adapterBrandModel.notifyDataSetChanged();
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
}
