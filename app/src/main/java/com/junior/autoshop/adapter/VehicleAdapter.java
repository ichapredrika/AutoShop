package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.junior.autoshop.R;
import com.junior.autoshop.SelectedAutoshopCallback;
import com.junior.autoshop.SelectedServiceCallback;
import com.junior.autoshop.SelectedVehicleCallback;
import com.junior.autoshop.models.VehicleCustomer;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    private ArrayList<VehicleCustomer> listVehicle;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private SelectedVehicleCallback callback;
    private DecimalFormat df = new DecimalFormat("#,###.##");

    public VehicleAdapter(Context context, ArrayList<VehicleCustomer> listVehicle, SelectedVehicleCallback callback) {
        this.context = context;
        this.listVehicle = listVehicle;
        popUpDialog = new Dialog(context);
        this.callback = callback;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vehicle_booking, viewGroup, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VehicleViewHolder holder, final int position) {
        final VehicleCustomer vehicle = listVehicle.get(position);
        if (vehicle.isSelected()){
            holder.ll.setBackground(context.getDrawable(R.drawable.bg_gray_gradient));
        }else holder.ll.setBackground(context.getDrawable(R.drawable.bg_black_gradient));
        holder.tvName.setText(vehicle.getName());
        holder.tvModel.setText(vehicle.getModel());
        holder.tvBrand.setText(vehicle.getBrand());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vehicle.isSelected()){
                    callback.deleteVehicle(listVehicle.get(position));
                    listVehicle.get(position).setSelected(false);
                    notifyDataSetChanged();

                }else{
                    callback.selectVehicle(listVehicle.get(position));
                    listVehicle.get(position).setSelected(true);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listVehicle.size();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvBrand;
        TextView tvModel;
        LinearLayout ll;

        VehicleViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvBrand = itemView.findViewById(R.id.txt_brand);
            tvModel = itemView.findViewById(R.id.txt_model);
            ll= itemView.findViewById(R.id.ll);
        }
    }
}
