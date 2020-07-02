package com.junior.autoshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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
import com.junior.autoshop.adapter.SelectedServiceAdapter;
import com.junior.autoshop.adapter.VehicleAdapter;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.Service;
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
import java.util.Locale;
import java.util.UUID;

import static com.junior.autoshop.ChooseAutoshopFragment.EXTRA_AUTOSHOP;
import static com.junior.autoshop.ChooseAutoshopFragment.EXTRA_SERVICE;

public class BookingDetailFragment extends Fragment implements SelectedVehicleCallback {
    public static final String EXTRA_SELF_DELIVERY = "SELF DELIVERY";
    public static final String EXTRA_AUTOSHOP_PICKUP = "AUTOSHOP PICKUP";
    private ArrayList<Service> listSelectedService;
    private Autoshop selectedAutoshop;
    private ImageButton btnStartDate, btnMovement;
    private TextView tvStartDate, tvMovement;
    private RecyclerView rvVehicle, rvService;
    private Button btnBook;
    private ImageView imgAutoshop;
    private TextView tvAutoshopName, tvAddress, tvDistance, tvDeliveryFee;
    private DecimalFormat df = new DecimalFormat("#,###.##");
    final String START_DATE_TAG = "StartDate";
    private String startDate;
    private Date date;
    private Dialog popUpDialog;
    private String movement;
    private SelectedServiceAdapter selectedServiceAdapter;
    private VehicleAdapter vehicleAdapter;
    private ArrayList<VehicleCustomer> listSelectedVehicle = new ArrayList<>();
    private ArrayList<VehicleCustomer> listVehicleCustomer = new ArrayList<>();
    private ArrayList<VehicleCustomer> listVehicleCustomerToAdapter = new ArrayList<>();
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private Customer customer;
    private FragmentActivity myContext;
    private VehicleCustomer selectedVehicle;
    private String latlong;
    private String transId;
    private String service, pricing;
    private double totalPrice=0;

    public BookingDetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStartDate = view.findViewById(R.id.btn_start_date);
        btnMovement = view.findViewById(R.id.btn_movement);
        btnBook = view.findViewById(R.id.btn_book);
        tvStartDate = view.findViewById(R.id.txt_start_date);
        tvMovement = view.findViewById(R.id.txt_movement);
        rvVehicle = view.findViewById(R.id.rv_vehicle);
        rvService = view.findViewById(R.id.rv_service);
        imgAutoshop = view.findViewById(R.id.img_autoshop);
        tvAutoshopName = view.findViewById(R.id.txt_name);
        tvAddress = view.findViewById(R.id.txt_address);
        tvDistance = view.findViewById(R.id.txt_distance);
        tvDeliveryFee = view.findViewById(R.id.txt_delivery_fee);

        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();

        listSelectedService = (ArrayList<Service>) getArguments().getSerializable(EXTRA_SERVICE);
        selectedAutoshop = getArguments().getParcelable(EXTRA_AUTOSHOP);
        latlong = getArguments().getString("LATLONG");
        Log.d("selected service", listSelectedService.toString());
        Log.d("selected autoshop", selectedAutoshop.toString());

        popUpDialog = new Dialog(getContext());
        tvAutoshopName.setText(selectedAutoshop.getName());
        double deliveryFee = Double.parseDouble(selectedAutoshop.getDeliveryFee());
        tvDeliveryFee.setText("Delivery Fee/Km : "+ getString(R.string.amount_parse, df.format(deliveryFee)));
        tvAddress.setText(selectedAutoshop.getAddress());
        tvDistance.setText("Distance: "+ df.format(selectedAutoshop.getDistance())+" Km");
        if (selectedAutoshop.getPhoto() != null) {
            Bitmap profileBitmap = decodeBitmap(selectedAutoshop.getPhoto());
            imgAutoshop.setImageBitmap(profileBitmap);
        }

        selectedServiceAdapter = new SelectedServiceAdapter(getContext(), listSelectedService);
        selectedServiceAdapter.notifyDataSetChanged();
        rvService.setHasFixedSize(true);
        rvService.setLayoutManager(new LinearLayoutManager(getContext()));
        rvService.setAdapter(selectedServiceAdapter);

        vehicleAdapter = new VehicleAdapter(getContext(), listVehicleCustomerToAdapter, this);
        vehicleAdapter.notifyDataSetChanged();
        rvVehicle.setHasFixedSize(true);
        rvVehicle.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVehicle.setAdapter(vehicleAdapter);

        getProfile();

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new DatePickerFragment();
                dFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        btnMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpMovement();
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvStartDate.getText().toString().equals("Pick booking date")) {
                    Toast.makeText(getContext(), "Please select start date!", Toast.LENGTH_SHORT).show();
                } else if (movement == null) {
                    Toast.makeText(getContext(), "Please select movement!", Toast.LENGTH_SHORT).show();
                } else if (listSelectedVehicle.size() < 1) {
                    Toast.makeText(getContext(), "Please select a vehicle!", Toast.LENGTH_SHORT).show();
                } else if (listSelectedVehicle.size() > 1) {
                    Toast.makeText(getContext(), "Please select only 1 vehicle!", Toast.LENGTH_SHORT).show();
                } else {
                    startDate = tvStartDate.getText().toString();

                    UUID uuid = UUID.randomUUID();
                    transId = uuid.toString().replace("-", "").toUpperCase();

                    service=null;
                    pricing="";

                    for(int i=0; i<listSelectedService.size();i++){
                        String sh_id = transId+"serv-"+i;
                        if (service == null) {
                            service="('"+ sh_id +"','"+ listSelectedService.get(i).getId() +"','"+ transId +"','"+listSelectedService.get(i).getNote()+"', NULL)";
                        }else{
                            service=service + "('"+ sh_id +"','"+ listSelectedService.get(i).getId() +"','"+ transId +"','"+listSelectedService.get(i).getNote()+"', NULL)";
                        }
                        if(i!=listSelectedService.size()-1){
                            service=service+",";
                        }
                    }

                    if (movement.equals(EXTRA_AUTOSHOP_PICKUP)){
                        String pricing_id = transId + "-pickup";
                        double priceDb;
                        if (selectedAutoshop.getDeliveryFee().equals("") || selectedAutoshop.getDeliveryFee().equals("null")){
                            priceDb=0;
                        }else{
                            priceDb = Double.parseDouble(selectedAutoshop.getDeliveryFee())* selectedAutoshop.getDistance();
                        }

                        totalPrice = priceDb;
                        String price = Double.toString(priceDb);
                        pricing = "('" + pricing_id + "','Pickup Fee','" + transId + "','"+ price +"')";
                    }

                    createTrans();
                }
            }
        });
    }

    private void popUpMovement() {
        popUpDialog.setContentView(R.layout.pop_up_movement);
        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
        Button btnSetMovement = popUpDialog.findViewById(R.id.btn_set_movement);
        final RadioGroup rgMovement = popUpDialog.findViewById(R.id.rg_movement);

        btnSetMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rgMovement.getCheckedRadioButtonId() == R.id.rb_self_delivery) {
                    movement = EXTRA_SELF_DELIVERY;
                } else movement = EXTRA_AUTOSHOP_PICKUP;
                tvMovement.setText(movement);
                popUpDialog.dismiss();
            }
        });

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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_DARK, this, year, month, day);
            dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
            calendar.add(Calendar.DATE, 2);
            dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView tv = (TextView) getActivity().findViewById(R.id.txt_start_date);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chosenDate = cal.getTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(chosenDate);

            tv.setText(formattedDate);
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
                params.put("MOVEMENT_OPTION", movement);
                params.put("VH_ID", selectedVehicle.getId());
                params.put("LATLONG", latlong);
                params.put("CUSTOMER_ID", customer.getId());
                params.put("AUTOSHOP_ID", selectedAutoshop.getId());
                params.put("STATUS", "ON QUEUE");
                params.put("TYPE", "REGULAR");
                params.put("SERVICE", service);
                params.put("PRICING", pricing);
                params.put("TOTAL_PRICE", Double.toString(totalPrice));
                Log.d("ct param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
