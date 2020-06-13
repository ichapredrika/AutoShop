package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.junior.autoshop.ChooseAutoshopFragment;
import com.junior.autoshop.OnGoingDetailFragment;
import com.junior.autoshop.R;
import com.junior.autoshop.models.TransOngoing;
import com.junior.autoshop.phpConf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class OnGoingAdapter extends RecyclerView.Adapter<OnGoingAdapter.OnGoingViewHolder>{
    private ArrayList<TransOngoing> listTransOnGoing;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;

    public OnGoingAdapter(Context context, ArrayList<TransOngoing> listTransOnGoing) {
        this.context = context;
        this.listTransOnGoing = listTransOnGoing;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public OnGoingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ongoing, viewGroup, false);
        return new OnGoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnGoingViewHolder holder, final int position) {
        final TransOngoing transOngoing = listTransOnGoing.get(position);
        holder.tvAutoshopName.setText(transOngoing.getAutoshopName());
        holder.tvVehicleName.setText(transOngoing.getVehicleName());

        holder.imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.google.com/maps/dir/?api=1&destination=" + transOngoing.getLatlong() + "&travelmode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingDetailFragment onGoingDetailFragment = new OnGoingDetailFragment();
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(onGoingDetailFragment.EXTRA_TRANS_ID, listTransOnGoing.get(position).getId());
                onGoingDetailFragment.setArguments(mBundle);
                FragmentManager mFragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                if (mFragmentManager != null) {
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container_layout, onGoingDetailFragment, OnGoingDetailFragment.class.getSimpleName());
                    mFragmentTransaction.addToBackStack(OnGoingDetailFragment.class.getSimpleName());
                    mFragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTransOnGoing.size();
    }

    class OnGoingViewHolder extends RecyclerView.ViewHolder {
        TextView tvAutoshopName;
        TextView tvVehicleName;
        ImageView imgMap;

        OnGoingViewHolder(View itemView) {
            super(itemView);
            tvAutoshopName = itemView.findViewById(R.id.txt_name);
            tvVehicleName = itemView.findViewById(R.id.txt_vehicle_name);
            imgMap = itemView.findViewById(R.id.img_map);
        }
    }

}
