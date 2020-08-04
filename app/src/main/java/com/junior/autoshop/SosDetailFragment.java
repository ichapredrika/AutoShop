package com.junior.autoshop;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.adapter.VehicleAdapter;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.VehicleCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.junior.autoshop.ChooseAutoshopFragment.EXTRA_AUTOSHOP;

public class SosDetailFragment extends Fragment implements SelectedVehicleCallback {
    public static final String EXTRA_SELF_DELIVERY = "SELF DELIVERY";
    public static final String EXTRA_AUTOSHOP_PICKUP = "AUTOSHOP PICKUP";
    private Autoshop selectedAutoshop;
    private FragmentActivity myContext;
    private RecyclerView rvVehicle;
    private Button btnBook;
    private ImageView imgAutoshop;
    private TextView tvAutoshopName, tvAddress, tvDistance, tvDeliveryFee;
    private DecimalFormat df = new DecimalFormat("#,###.##");
    private String startDate;
    private Dialog popUpDialog;
    private VehicleAdapter vehicleAdapter;
    private ArrayList<VehicleCustomer> listSelectedVehicle = new ArrayList<>();
    private ArrayList<VehicleCustomer> listVehicleCustomer = new ArrayList<>();
    private ArrayList<VehicleCustomer> listVehicleCustomerToAdapter = new ArrayList<>();
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private Customer customer;
    private VehicleCustomer selectedVehicle;
    private String latlong;
    private String transId;
    private String service, pricing;
    private double totalPrice = 0;

    public SosDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sos_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBook = view.findViewById(R.id.btn_book);
        rvVehicle = view.findViewById(R.id.rv_vehicle);
        imgAutoshop = view.findViewById(R.id.img_autoshop);
        tvAutoshopName = view.findViewById(R.id.txt_name);
        tvAddress = view.findViewById(R.id.txt_address);
        tvDistance = view.findViewById(R.id.txt_distance);
        tvDeliveryFee = view.findViewById(R.id.txt_delivery_fee);

        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();

        selectedAutoshop = getArguments().getParcelable(EXTRA_AUTOSHOP);
        latlong = getArguments().getString("LATLONG");

        popUpDialog = new Dialog(getContext());
        tvAutoshopName.setText(selectedAutoshop.getName());
        tvAddress.setText(selectedAutoshop.getAddress());

        double deliveryFee = Double.parseDouble(selectedAutoshop.getDeliveryFee());
        tvDeliveryFee.setText("Delivery Fee/Km: " + getString(R.string.amount_parse, df.format(deliveryFee)));
        tvDistance.setText("Distance: " + df.format(selectedAutoshop.getDistance()) + " Km");
        if (selectedAutoshop.getPhoto() != null) {
            Bitmap profileBitmap = decodeBitmap(selectedAutoshop.getPhoto());
            imgAutoshop.setImageBitmap(profileBitmap);
        }

        vehicleAdapter = new VehicleAdapter(getContext(), listVehicleCustomerToAdapter, this);
        vehicleAdapter.notifyDataSetChanged();
        rvVehicle.setHasFixedSize(true);
        rvVehicle.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVehicle.setAdapter(vehicleAdapter);

        getProfile();


        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service = null;
                pricing = null;

                if (listSelectedVehicle.size() < 1) {
                    Toast.makeText(getContext(), "Please select a vehicle!", Toast.LENGTH_SHORT).show();
                } else if (listSelectedVehicle.size() > 1) {
                    Toast.makeText(getContext(), "Please select only 1 vehicle!", Toast.LENGTH_SHORT).show();
                } else {
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    startDate = dateFormat.format(c);
                    double priceDb = Double.parseDouble(selectedAutoshop.getDeliveryFee()) * selectedAutoshop.getDistance();
                    String price = Double.toString(priceDb);
                    totalPrice = priceDb;
                    UUID uuid = UUID.randomUUID();
                    transId = uuid.toString().replace("-", "").toUpperCase();
                    String sh_id = transId + "serv-" + 0;
                    String pricing_id = transId + "-pickup";
                    service = "('" + sh_id + "','sertow','" + transId + "','SOS', NULL)";
                    pricing = "('" + pricing_id + "','Pickup SOS','" + transId + "','" + price + "')";
                    createTrans();
                }
            }
        });
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
        vehicleAdapter.notifyDataSetChanged();
    }

    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void selectVehicle(VehicleCustomer vehicle) {
        selectedVehicle = vehicle;
        listSelectedVehicle.add(selectedVehicle);
    }

    @Override
    public void deleteVehicle(VehicleCustomer vehicle) {
        for (int i = 0; i < listSelectedVehicle.size(); i++) {
            if (vehicle.getId().equals(listSelectedVehicle.get(i).getId())) {
                listSelectedVehicle.remove(i);
            }
        }
    }

    private void createTrans() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_CREATE_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json create trans", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                    if (response.equals("1")) {
                        List<String> toEmailList = new ArrayList<>();
                        toEmailList.add(selectedAutoshop.getEmail());
                        Log.i("SendMailActivity", "To List: " + toEmailList);
                        String emailSubject = "New SOS Order";
                        String emailBody = "There's a new order as detailed below:\n" +
                                "Customer: "+customer.getFullname()+"\n"+
                                "Order Date: "+startDate+"\n"+
                                "Type : SOS \n\n"+
                                "Check Autoshop App now!";
                        new SendMailTask(getActivity()).execute(getActivity().getString(R.string.autoshop_email),
                                getActivity().getString(R.string.autoshop_password), toEmailList, emailSubject, emailBody);

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_STATE, MainActivity.STATE_ONGOING);
                        FragmentManager mFragmentManager = getFragmentManager();
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        startActivity(intent);

                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), getString(R.string.msg_something_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                params.put("START_DATE", startDate);
                params.put("MOVEMENT_OPTION", EXTRA_AUTOSHOP_PICKUP);
                params.put("VH_ID", selectedVehicle.getId());
                params.put("LATLONG", latlong);
                params.put("CUSTOMER_ID", customer.getId());
                params.put("AUTOSHOP_ID", selectedAutoshop.getId());
                params.put("STATUS", "ON QUEUE");
                params.put("TYPE", "SOS");
                params.put("SERVICE", service);
                params.put("PRICING", pricing);
                params.put("TOTAL_PRICE", Double.toString(totalPrice));
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
