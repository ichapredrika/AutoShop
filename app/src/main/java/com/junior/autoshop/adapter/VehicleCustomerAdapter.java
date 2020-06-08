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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.R;
import com.junior.autoshop.models.ServiceAutoshop;
import com.junior.autoshop.models.VehicleCustomer;
import com.junior.autoshop.phpConf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class VehicleCustomerAdapter extends RecyclerView.Adapter<VehicleCustomerAdapter.VehicleCustomerViewHolder>{
    private ArrayList<VehicleCustomer> listVehicleCustomer;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;

    public VehicleCustomerAdapter(Context context, ArrayList<VehicleCustomer> listVehicleCustomer) {
        this.context = context;
        this.listVehicleCustomer = listVehicleCustomer;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public VehicleCustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vehicle, viewGroup, false);
        return new VehicleCustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleCustomerViewHolder holder, final int position) {
        final VehicleCustomer vehicleCustomer = listVehicleCustomer.get(position);
        holder.tvName.setText(vehicleCustomer.getName());
        holder.tvModel.setText(vehicleCustomer.getModel());
        holder.tvBrand.setText(vehicleCustomer.getBrand());
        holder.tvYear.setText(vehicleCustomer.getYear());

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.setContentView(R.layout.pop_up_confirmation);
                final TextView tvQuestion = popUpDialog.findViewById(R.id.txt_question);
                tvQuestion.setText("Are you sure?");
                Button btnYes = popUpDialog.findViewById(R.id.btn_yes);
                Button btnNo = popUpDialog.findViewById(R.id.btn_no);


                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteService(position);
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
        });
    }

    @Override
    public int getItemCount() {
        return listVehicleCustomer.size();
    }

    class VehicleCustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvBrand;
        TextView tvModel;
        TextView tvYear;
        ImageView imgDelete;

        VehicleCustomerViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvBrand = itemView.findViewById(R.id.txt_brand);
            tvModel = itemView.findViewById(R.id.txt_model);
            tvYear = itemView.findViewById(R.id.txt_year);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    private void deleteService(final int position) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_DELETE_VEHICLE_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json delete vehicle", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    listVehicleCustomer.remove(position);
                    notifyDataSetChanged();

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
                Toast.makeText(context, context.getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("VH_ID", listVehicleCustomer.get(position).getId());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
