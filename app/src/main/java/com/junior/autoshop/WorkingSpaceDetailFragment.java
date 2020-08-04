package com.junior.autoshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.adapter.SelectedServiceAdapter;
import com.junior.autoshop.adapter.TransCostAdapter;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Service;
import com.junior.autoshop.models.Trans;
import com.junior.autoshop.models.TransCost;

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

public class WorkingSpaceDetailFragment extends Fragment implements UpdateTotalCallback{
    public static String EXTRA_TRANS_ID = "TRANSACTION_ID";
    private String transId;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private Dialog popUpDialog;
    private Autoshop autoshop;
    private Trans trans;
    private DecimalFormat df = new DecimalFormat("#,###.###");

    private TransCostAdapter transCostAdapter;
    private SelectedServiceAdapter selectedServiceAdapter;
    private ArrayList<TransCost> listTransCost = new ArrayList<>();
    private ArrayList<TransCost> listTransCostToAdapter = new ArrayList<>();
    private ArrayList<Service> listComplaints = new ArrayList<>();
    private ArrayList<Service> listComplaintsToAdapter = new ArrayList<>();

    private TextView tvUsername, tvServiceAct;
    private TextView tvBrand, tvModel, tvProgress;
    private SeekBar sbProgress;
    private TextView tvPrice, tvTotal;
    private Button btnComplaints, btnAdd;
    private Button btnUpdate, btnFinish;
    private RecyclerView rvCost, rvComplaints;

    public WorkingSpaceDetailFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_working_space_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.txt_name);
        tvBrand = view.findViewById(R.id.txt_brand);
        tvModel = view.findViewById(R.id.txt_model);
        tvProgress = view.findViewById(R.id.txt_progress);
        sbProgress = view.findViewById(R.id.sb_progress);
        tvPrice = view.findViewById(R.id.txt_price);
        tvTotal = view.findViewById(R.id.txt_total);
        btnComplaints = view.findViewById(R.id.btn_complaints);
        btnAdd = view.findViewById(R.id.btn_add_cost);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnFinish = view.findViewById(R.id.btn_finish);
        rvCost = view.findViewById(R.id.rv_cost);
        tvServiceAct = view.findViewById(R.id.txt_service_act);

        popUpDialog = new Dialog(getContext());
        mUserPreference = new UserPreference(getContext());
        autoshop = mUserPreference.getAutoshop();
        transId = getArguments().getString(EXTRA_TRANS_ID);

        transCostAdapter = new TransCostAdapter(getContext(), listTransCostToAdapter, true, this);
        transCostAdapter.notifyDataSetChanged();
        rvCost.setHasFixedSize(true);
        rvCost.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCost.setAdapter(transCostAdapter);

        getDetail();

        btnComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getComplaints();
                popUpComplaints();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(trans.getProgress())==100){
                    popUpDialog.setContentView(R.layout.pop_up_confirmation);
                    final TextView tvQuestion = popUpDialog.findViewById(R.id.txt_question);
                    tvQuestion.setText("Are you sure?");
                    Button btnYes = popUpDialog.findViewById(R.id.btn_yes);
                    Button btnNo = popUpDialog.findViewById(R.id.btn_no);


                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishTrans();
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
                } else Toast.makeText(getContext(), "Please update progress first!", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String progress = Integer.toString(sbProgress.getProgress());
                updateProgress(progress);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvPrice.getText().toString().trim() == null || tvPrice.getText().toString().trim().isEmpty() ){
                    Toast.makeText(getContext(), "Please input price!", Toast.LENGTH_SHORT).show();
                }if (tvServiceAct.getText().toString().trim() == null || tvServiceAct.getText().toString().trim().isEmpty() ){
                    Toast.makeText(getContext(), "Please input service act!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String price = tvPrice.getText().toString().trim();
                    addCost(price, tvServiceAct.getText().toString().trim());
                }
            }
        });


        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                tvProgress.setText(progress+ " %");
            }
        });
    }

    private void updateUI(){
        tvUsername.setText(trans.getCustomerName());
        tvBrand.setText(trans.getVehicleBrand());
        tvModel.setText(trans.getVehicleModel());
        tvUsername.setText(trans.getCustomerName());
        if(!trans.getProgress().equals("null")){
            sbProgress.setProgress(Integer.parseInt(trans.getProgress()));
        }

        if(!trans.getTotalPrice().equals("null")){
            double total = Double.parseDouble(trans.getTotalPrice());
            tvTotal.setText(getContext().getString(R.string.amount_parse,df.format(total)));
        }
    }

    private void popUpComplaints() {
        popUpDialog.setContentView(R.layout.pop_up_complaints);

        rvComplaints = popUpDialog.findViewById(R.id.rv_complaints);
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

    private void finishTrans() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_FINISH_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json wfp", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    if(response.equals("1")){
                        List<String> toEmailList = new ArrayList<>();
                        toEmailList.add(trans.getCustomerEmail());
                        Log.i("SendMailActivity", "To List: " + toEmailList);
                        String emailSubject = "Service Finished";
                        String emailBody = "The service for your order has been finished as detailed below:\n" +
                                "Autoshop: "+trans.getAutoshopName()+"\n"+
                                "Order Date: "+trans.getStartDate()+"\n"+
                                "Type : " + trans.getType()+"\n\n"+
                                "Check Autoshop App now!";
                        new SendMailTask(getActivity()).execute(getActivity().getString(R.string.autoshop_email),
                                getActivity().getString(R.string.autoshop_password), toEmailList, emailSubject, emailBody);

                        Intent intent = new Intent(getContext(), MainAdminActivity.class);
                        intent.putExtra(MainAdminActivity.EXTRA_STATE, MainAdminActivity.STATE_WORKING_SPACE);
                        FragmentManager mFragmentManager = getFragmentManager();
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        startActivity(intent);
                    } else Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

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
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = dateFormat.format(c);

                params.put("TRANSACTION_ID", transId);
                params.put("STATUS", "WAITING FOR PAYMENT");
                params.put("AUTOSHOP_ID", trans.getAutoshopId());
                params.put("DATE", date);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    private void addCost(final String price, final String serviceAct) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

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
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    if (response.equals("1")) {
                        getDetail();
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
                double total = 0;
                if (trans.getTotalPrice().equals("null")){
                    total = Double.parseDouble(price);
                }else{
                    total = Double.parseDouble(trans.getTotalPrice()) + Double.parseDouble(price);
                }

                params.put("TRANSACTION_ID", transId);
                params.put("SERVICE_ACT", serviceAct);
                params.put("TOTAL_PRICE", Double.toString(total));
                params.put("PRICE", price);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void getComplaints() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_TRANS_SERVICE, new Response.Listener<String>() {
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

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_TRANS_COST, new Response.Listener<String>() {
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

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_ONGOING_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json trans Detail", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    String response = jo.getString("response");
                    loading.dismiss();
                    getTransCost();
                    if (response.equals("1")) {
                        listTransCost.clear();
                        trans = new Trans(jo);
                        updateUI();
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

    private void updateProgress(final String progress) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPDATE_TRANS_PROGRESS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json update progress", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    String response = jo.getString("response");
                    loading.dismiss();
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    if (response.equals("1")) {
                        getDetail();
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
                params.put("PROGRESS", progress);
                Log.d("param", params.toString());
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

    @Override
    public void delete(String price) {
        getDetail();
    }
}
