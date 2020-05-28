package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.junior.autoshop.models.Service;
import com.junior.autoshop.phpConf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private ArrayList<Service> listService;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;

    public ServiceAdapter(Context context, ArrayList<Service> listService) {
        this.context = context;
        this.listService = listService;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_booking, viewGroup, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, final int position) {
        final Service service = listService.get(position);
        holder.tvName.setText(service.getType());
        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, service.getDetail(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listService.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvNote;
        ImageView imgInfo;
        CheckBox cbBook;

        ServiceViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvNote = itemView.findViewById(R.id.txt_note);
            imgInfo = itemView.findViewById(R.id.img_info);
            cbBook = itemView.findViewById(R.id.cb_book);
        }
    }

}
