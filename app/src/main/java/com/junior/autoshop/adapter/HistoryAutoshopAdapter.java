package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.R;
import com.junior.autoshop.models.Service;
import com.junior.autoshop.models.Trans;
import com.junior.autoshop.PhpConf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class HistoryAutoshopAdapter extends RecyclerView.Adapter<HistoryAutoshopAdapter.HistoryAutoshopViewHolder>{
    private ArrayList<Trans> listTrans;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    private RecyclerView rvComplaints;
    private  SelectedServiceAdapter selectedServiceAdapter;
    private ArrayList<Service> listComplaints = new ArrayList<>();
    private ArrayList<Service> listComplaintsToAdapter = new ArrayList<>();
    public HistoryAutoshopAdapter(Context context, ArrayList<Trans> listTrans) {
        this.context = context;
        this.listTrans = listTrans;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public HistoryAutoshopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history_autoshop, viewGroup, false);
        return new HistoryAutoshopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAutoshopViewHolder holder, final int position) {
        final Trans trans = listTrans.get(position);

        holder.tvCustomerName.setText(trans.getCustomerName());
        holder.tvBrand.setText(trans.getVehicleBrand());
        holder.tvModel.setText(trans.getVehicleModel());
        if(trans.getTotalPrice().equals("null") || trans.getTotalPrice().isEmpty()){
            holder.tvTotal.setText("Rp. 0");
        }else{
            double price = Double.parseDouble(trans.getTotalPrice());
            holder.tvTotal.setText(context.getString(R.string.amount_parse,df.format(price)));
        }

        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpComplaints(trans.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    private void getComplaints(final String transId) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

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
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, context.getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
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

    private void updateComplaintsAdapter(ArrayList<Service> list) {
        listComplaintsToAdapter.clear();
        listComplaintsToAdapter.addAll(list);
        selectedServiceAdapter.notifyDataSetChanged();
    }

    private void popUpComplaints(String transId) {
        popUpDialog.setContentView(R.layout.pop_up_edit_service);
        getComplaints(transId);
        rvComplaints = popUpDialog.findViewById(R.id.rv_services);
        selectedServiceAdapter = new SelectedServiceAdapter(context, listComplaintsToAdapter);
        selectedServiceAdapter.notifyDataSetChanged();
        rvComplaints.setHasFixedSize(true);
        rvComplaints.setLayoutManager(new LinearLayoutManager(context));
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


    class HistoryAutoshopViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName;
        TextView tvBrand;
        TextView tvModel;
        TextView tvTotal;
        ImageView imgInfo;


        HistoryAutoshopViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.txt_name);
            tvBrand = itemView.findViewById(R.id.txt_brand);
            tvModel = itemView.findViewById(R.id.txt_model);
            tvTotal = itemView.findViewById(R.id.txt_total);
            imgInfo = itemView.findViewById(R.id.img_info);
        }
    }
}
