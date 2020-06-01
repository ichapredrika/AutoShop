package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.junior.autoshop.R;
import com.junior.autoshop.SelectedAutoshopCallback;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AutoshopAdapter extends RecyclerView.Adapter<AutoshopAdapter.AutoshopViewHolder> {
    private ArrayList<Autoshop> listAutoshop;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private SelectedAutoshopCallback callback;
    private DecimalFormat df = new DecimalFormat("#,###.##");
    public AutoshopAdapter(Context context, ArrayList<Autoshop> listAutoshop, SelectedAutoshopCallback callback) {
        this.context = context;
        this.listAutoshop = listAutoshop;
        popUpDialog = new Dialog(context);
        this.callback = callback;

    }

    @NonNull
    @Override
    public AutoshopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_autoshop, viewGroup, false);
        return new AutoshopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AutoshopViewHolder holder, final int position) {
        final Autoshop autoshop = listAutoshop.get(position);
        holder.tvName.setText(autoshop.getName());
        holder.tvAddress.setText(autoshop.getAddress());
        holder.tvDistance.setText("Distance: "+ df.format(autoshop.getDistance())+" Km");

        if (autoshop.getPhoto()!=null ){
            Bitmap profileBitmap = decodeBitmap(autoshop.getPhoto());
            holder.imgAutoshop.setImageBitmap(profileBitmap);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.selectAutoshop(listAutoshop.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listAutoshop.size();
    }

    class AutoshopViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;
        ImageView imgAutoshop;
        TextView tvDistance;

        AutoshopViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvAddress = itemView.findViewById(R.id.txt_address);
            tvDistance = itemView.findViewById(R.id.txt_distance);
            imgAutoshop = itemView.findViewById(R.id.img_autoshop);
        }
    }

    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
