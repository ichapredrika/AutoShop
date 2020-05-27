package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.junior.autoshop.UserPreference;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Service;
import com.junior.autoshop.models.ServiceAutoshop;
import com.junior.autoshop.phpConf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class AddServiceAutoshopAdapter extends RecyclerView.Adapter<AddServiceAutoshopAdapter.AddServiceAutoshopViewHolder>{
    private ArrayList<ServiceAutoshop> listServiceAutoshop;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private Autoshop autoshop;
    private UserPreference mUserPreference;
    private DecimalFormat df = new DecimalFormat("#,###.###");

    public AddServiceAutoshopAdapter(Context context, ArrayList<ServiceAutoshop> listServiceAutoshop) {
        this.context = context;
        this.listServiceAutoshop = listServiceAutoshop;
        popUpDialog = new Dialog(context);
        mUserPreference = new UserPreference(context);
        autoshop = mUserPreference.getAutoshop();
    }


    @NonNull
    @Override
    public AddServiceAutoshopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_service_check, viewGroup, false);
        return new AddServiceAutoshopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddServiceAutoshopViewHolder holder, final int position) {
        final ServiceAutoshop serviceAutoshop = listServiceAutoshop.get(position);
        holder.tvName.setText(serviceAutoshop.getType());
        holder.tvDetail.setText(serviceAutoshop.getDetail());
        holder.cbService.setChecked(serviceAutoshop.isChecked());
        /*holder.cbService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if (isChecked){
                       addService(position);
                   }else{
                       deleteService(position);
                   }
               }
           }
        );*/

        holder.cbService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox)v).isChecked();
                if (checked){
                    addService(position);
                }else deleteService(position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return listServiceAutoshop.size();
    }

    class AddServiceAutoshopViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDetail;
        CheckBox cbService;

        AddServiceAutoshopViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvDetail = itemView.findViewById(R.id.txt_detail);
            cbService = itemView.findViewById(R.id.cb_service);
        }
    }

    private void deleteService(final int position) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_DELETE_SERVICE_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json delete service", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    listServiceAutoshop.get(position).setChecked(false);
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
                params.put("SA_ID", listServiceAutoshop.get(position).getId());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void addService(final int position) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, phpConf.URL_ADD_SERVICE_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json add service", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    listServiceAutoshop.get(position).setChecked(true);
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
                params.put("SERVICE_ID", listServiceAutoshop.get(position).getServiceId());
                params.put("AUTOSHOP_ID", autoshop.getId());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

}
