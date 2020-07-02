package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.junior.autoshop.OnGoingDetailFragment;
import com.junior.autoshop.R;
import com.junior.autoshop.models.Trans;

import java.util.ArrayList;


public class OnGoingAdapter extends RecyclerView.Adapter<OnGoingAdapter.OnGoingViewHolder>{
    private ArrayList<Trans> listTransOnGoing;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;

    public OnGoingAdapter(Context context, ArrayList<Trans> listTransOnGoing) {
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
        final Trans transOngoing = listTransOnGoing.get(position);
        holder.tvAutoshopName.setText(transOngoing.getAutoshopName());
        holder.tvVehicleName.setText(transOngoing.getVehicleName());
        holder.tvStatus.setText(transOngoing.getStatus());

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
        TextView tvStatus;
        ImageView imgMap;

        OnGoingViewHolder(View itemView) {
            super(itemView);
            tvAutoshopName = itemView.findViewById(R.id.txt_name);
            tvVehicleName = itemView.findViewById(R.id.txt_vehicle_name);
            tvStatus = itemView.findViewById(R.id.txt_status);
            imgMap = itemView.findViewById(R.id.img_map);
        }
    }

}
