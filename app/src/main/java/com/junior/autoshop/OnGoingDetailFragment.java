package com.junior.autoshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.print.PageRange;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.junior.autoshop.adapter.AddServiceAutoshopAdapter;
import com.junior.autoshop.adapter.SelectedServiceAdapter;
import com.junior.autoshop.adapter.TransCostAdapter;
import com.junior.autoshop.adapter.VehicleCustomerAdapter;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.Service;
import com.junior.autoshop.models.Trans;
import com.junior.autoshop.models.TransCost;
import com.junior.autoshop.models.Vehicle;
import com.junior.autoshop.models.VehicleCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class OnGoingDetailFragment extends Fragment {
    public static String EXTRA_TRANS_ID = "TRANSACTION_ID";
    public static String EXTRA_DELIVERY = "DELIVERY";
    public static String EXTRA_SELF_PICKUP = "SELF PICKUP";
    private String transId;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private RecyclerView rvCost;
    private RecyclerView rvInvoice;
    private TransCostAdapter transCostAdapter;
    private ArrayList<TransCost> listTransCost = new ArrayList<>();
    private ArrayList<TransCost> listTransCostToAdapter = new ArrayList<>();
    private ArrayList<Service> listComplaints = new ArrayList<>();
    private ArrayList<Service> listComplaintsToAdapter = new ArrayList<>();
    private Dialog popUpDialog;
    private Customer customer;
    private Trans trans;

    private ImageView imgCancel;
    private TextView tvName, tvVehicleName;
    private Button btnContact, btnComplaints;
    private RadioGroup rgPickup;
    private Button btnIssue;
    private TextView tvTotal, tvProgress;
    private ImageView imgQrcode;
    private RecyclerView rvComplaints;
    private SelectedServiceAdapter selectedServiceAdapter;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    PieChart pieChart;
    ArrayList<Entry> listTotalPie = new ArrayList<Entry>();

    public OnGoingDetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_on_going_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgCancel = view.findViewById(R.id.img_cancel);
        tvName = view.findViewById(R.id.txt_name);
        tvVehicleName = view.findViewById(R.id.txt_vehicle_name);
        btnContact = view.findViewById(R.id.btn_contact);
        btnComplaints = view.findViewById(R.id.btn_complaints);
        rgPickup = view.findViewById(R.id.rg_pickup);
        btnIssue = view.findViewById(R.id.btn_issue);
        rvCost = view.findViewById(R.id.rv_cost);
        imgQrcode = view.findViewById(R.id.img_qr);
        pieChart = view.findViewById(R.id.piechart);
        tvProgress = view.findViewById(R.id.txt_progress);
        tvTotal = view.findViewById(R.id.txt_total);

        popUpDialog = new Dialog(getContext());
        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();

        transCostAdapter = new TransCostAdapter(getContext(), listTransCostToAdapter, false);
        transCostAdapter.notifyDataSetChanged();
        rvCost.setHasFixedSize(true);
        rvCost.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCost.setAdapter(transCostAdapter);

        transId = getArguments().getString(EXTRA_TRANS_ID);

        getDetail();

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone;
                if (trans.getMovementOption().equals(BookingDetailFragment.EXTRA_SELF_DELIVERY)){
                    phone = trans.getAdminContact();
                }else {
                    phone = trans.getPickupContact();
                }
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone number", phone);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(),"Phone Number Copied to Clipboard!",Toast.LENGTH_SHORT).show();
            }
        });

        btnComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getComplaints();
                popUpComplaints();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpCancelTrans();
            }
        });

        rgPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rgPickup.getCheckedRadioButtonId() == R.id.rb_delivery){
                    changePickupOption(EXTRA_DELIVERY);
                }else changePickupOption(EXTRA_SELF_PICKUP);
            }
        });

        btnIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trans.getPickupOption().equals("null")){
                    Toast.makeText(getContext(), "Please choose pickup option first!", Toast.LENGTH_SHORT).show();
                }else if (trans.getPickupOption().equals(EXTRA_DELIVERY)){
                    popUpPayment();
                }else if (trans.getPickupOption().equals(EXTRA_SELF_PICKUP)){
                    popUpInvoice();
                }
            }
        });
    }

    private void popUpComplaints() {
        popUpDialog.setContentView(R.layout.pop_up_edit_service);

        rvComplaints = popUpDialog.findViewById(R.id.rv_services);
        selectedServiceAdapter = new SelectedServiceAdapter(getContext(), listComplaintsToAdapter);
        selectedServiceAdapter.notifyDataSetChanged();
        rvComplaints.setHasFixedSize(true);
        rvComplaints.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComplaints.setAdapter(selectedServiceAdapter);

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

    private void popUpInvoice() {
        popUpDialog.setContentView(R.layout.pop_up_invoice);

        rvInvoice = popUpDialog.findViewById(R.id.rv_cost);
        transCostAdapter = new TransCostAdapter(getContext(), listTransCostToAdapter, false);
        transCostAdapter.notifyDataSetChanged();
        rvInvoice.setHasFixedSize(true);
        rvInvoice.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInvoice.setAdapter(transCostAdapter);

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

    private void popUpPayment() {
        popUpDialog.setContentView(R.layout.pop_up_payment);

        TextView tvBank = popUpDialog.findViewById(R.id.txt_bank);
        TextView tvAccNumber = popUpDialog.findViewById(R.id.txt_account_number);
        TextView tvContact = popUpDialog.findViewById(R.id.txt_contact);
        TextView tvTotal = popUpDialog.findViewById(R.id.txt_total);

        tvBank.setText(trans.getAutoshopBank());
        tvAccNumber.setText(trans.getAutoshopAccountNumber());
        tvContact.setText(trans.getAdminContact());
        double total = Double.parseDouble(trans.getTotalPrice());
        tvTotal.setText(getContext().getString(R.string.amount_parse,df.format(total)));

        tvTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("total price", trans.getTotalPrice());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(),"Total Price Copied to Clipboard!",Toast.LENGTH_SHORT).show();
            }
        });

        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone number", trans.getAdminContact());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(),"Phone Number Copied to Clipboard!",Toast.LENGTH_SHORT).show();
            }
        });

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

    private void popUpCancelTrans(){
        popUpDialog.setContentView(R.layout.pop_up_confirmation);
        Button btnYes = popUpDialog.findViewById(R.id.btn_yes);
        Button btnNo = popUpDialog.findViewById(R.id.btn_no);


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTrans();
                popUpDialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
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


    private void updateUI(){
        tvName.setText(trans.getAutoshopName());
        tvVehicleName.setText(trans.getVehicleName());
        if(!trans.getTotalPrice().equals("null")){
            double total = Double.parseDouble(trans.getTotalPrice());
            tvTotal.setText(getContext().getString(R.string.amount_parse,df.format(total)));
        }
        if(trans.getStatus().equals("WAITING FOR PAYMENT")){
            btnIssue.setVisibility(View.VISIBLE);
            rgPickup.setVisibility(View.GONE);
        }else{
            btnIssue.setVisibility(View.GONE);
            rgPickup.setVisibility(View.VISIBLE);
        }

        listTotalPie.clear();
        if (trans.getProgress().equals("null")){
            float progress = 0;
            tvProgress.setText(progress+" %");
            listTotalPie.add(new Entry(progress, 0));
            listTotalPie.add(new Entry(100, 1));
        }else{
            float progress = Float.parseFloat(trans.getProgress());
            tvProgress.setText(progress+" %");
            listTotalPie.add(new Entry(progress, 0));
            listTotalPie.add(new Entry(100-progress, 1));
        }

        createPieChart();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(transId, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgQrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void createPieChart() {
        ArrayList<String> listTransaction = new ArrayList<>();
        listTransaction.add("");
        listTransaction.add("");
        PieDataSet dataSet = new PieDataSet(listTotalPie, "");
        PieData data = new PieData(listTransaction, dataSet);
        pieChart.setData(data);
        dataSet.setColors(new int[]{R.color.colorBlack , R.color.colorDark5} , getContext());
        pieChart.setContentDescription(null);
        pieChart.setDescription(null);
        pieChart.animateXY(3000, 3000);
    }

    private void getComplaints() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_GET_TRANS_SERVICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json trans service", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listComplaints.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            Service service = new Service(object);
                            listComplaints.add(service);
                        }
                        updateComplaintsAdapter(listComplaints);
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
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void getTransCost() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_GET_TRANS_COST, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json trans cost", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listTransCost.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            TransCost transCost = new TransCost(object);
                            listTransCost.add(transCost);
                        }
                        updateAdapter(listTransCost);
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
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void getDetail() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_GET_ONGOING_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json trans Detail", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listTransCost.clear();

                            trans = new Trans(jo);

                        updateUI();
                        getTransCost();
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
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void cancelTrans() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_CANCEL_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json cancel trans", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    loading.dismiss();

                    if (response.equals("1")){
                        Toast.makeText(getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }else Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();

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
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void changePickupOption(final String option) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_CHANGE_PICKUP_OPTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json change pickup", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
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
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                params.put("PICKUP_OPTION", option);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void updateAdapter(ArrayList<TransCost> list) {
        listTransCostToAdapter.clear();
        listTransCostToAdapter.addAll(list);
        transCostAdapter.notifyDataSetChanged();
    }

    private void updateComplaintsAdapter(ArrayList<Service> list) {
        listComplaintsToAdapter.clear();
        listComplaintsToAdapter.addAll(list);
        selectedServiceAdapter.notifyDataSetChanged();
    }


}
