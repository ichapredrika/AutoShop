package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.OnGoingDetailFragment;
import com.junior.autoshop.R;
import com.junior.autoshop.models.Trans;
import com.junior.autoshop.models.TransOngoing;
import com.junior.autoshop.phpConf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class BookedAdapter extends RecyclerView.Adapter<BookedAdapter.BookedViewHolder>{
    private ArrayList<Trans> listTrans;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;

    public BookedAdapter(Context context, ArrayList<Trans> listTrans) {
        this.context = context;
        this.listTrans = listTrans;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public BookedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_booked, viewGroup, false);
        return new BookedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookedViewHolder holder, final int position) {
        final Trans trans = listTrans.get(position);

        holder.tvCustomerName.setText(trans.getCustomerName());
        holder.tvBrand.setText(trans.getVehicleBrand());
        holder.tvModel.setText(trans.getVehicleModel());
        holder.tvAddress.setText(trans.getLocation());

        holder.imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.google.com/maps/dir/?api=1&destination=" + trans.getLatlong() + "&travelmode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    class BookedViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName;
        TextView tvBrand;
        TextView tvModel;
        TextView tvAddress;
        ImageView imgMap;

        BookedViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.txt_name);
            tvBrand = itemView.findViewById(R.id.txt_brand);
            tvModel = itemView.findViewById(R.id.txt_model);
            tvAddress = itemView.findViewById(R.id.txt_address);
            imgMap = itemView.findViewById(R.id.img_map);
        }
    }
}
