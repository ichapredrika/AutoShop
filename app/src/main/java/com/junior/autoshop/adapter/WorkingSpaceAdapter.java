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
import com.junior.autoshop.WorkingSpaceDetailFragment;
import com.junior.autoshop.models.Trans;

import java.util.ArrayList;


public class WorkingSpaceAdapter extends RecyclerView.Adapter<WorkingSpaceAdapter.WorkingSpaceViewHolder>{
    private ArrayList<Trans> listTrans;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;

    public WorkingSpaceAdapter(Context context, ArrayList<Trans> listTrans) {
        this.context = context;
        this.listTrans = listTrans;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public WorkingSpaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_working_space, viewGroup, false);
        return new WorkingSpaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkingSpaceViewHolder holder, final int position) {
        final Trans trans = listTrans.get(position);

        holder.tvCustomerName.setText(trans.getCustomerName());
        holder.tvBrand.setText(trans.getVehicleBrand());
        holder.tvModel.setText(trans.getVehicleModel());
        holder.tvSpace.setText("Space: " +trans.getSpaceNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkingSpaceDetailFragment workingSpaceDetailFragment = new WorkingSpaceDetailFragment();
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(WorkingSpaceDetailFragment.EXTRA_TRANS_ID, listTrans.get(position).getId());
                workingSpaceDetailFragment.setArguments(mBundle);
                FragmentManager mFragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                if (mFragmentManager != null) {
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.container_layout, workingSpaceDetailFragment, OnGoingDetailFragment.class.getSimpleName());
                    mFragmentTransaction.addToBackStack(OnGoingDetailFragment.class.getSimpleName());
                    mFragmentTransaction.commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    class WorkingSpaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName;
        TextView tvBrand;
        TextView tvModel;
        TextView tvSpace;

        WorkingSpaceViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.txt_name);
            tvBrand = itemView.findViewById(R.id.txt_brand);
            tvModel = itemView.findViewById(R.id.txt_model);
            tvSpace = itemView.findViewById(R.id.txt_space);
        }
    }
}
