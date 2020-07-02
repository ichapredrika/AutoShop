package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.junior.autoshop.R;
import com.junior.autoshop.models.Trans;

import java.util.ArrayList;


public class BookedAdapter extends RecyclerView.Adapter<BookedAdapter.BookedViewHolder> {
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

        if (trans.getStatus().equals("REISSUE")) {
            holder.ll.setBackground(context.getDrawable(R.drawable.bg_orange_gradient));
        }else if (trans.getType().equals("SOS")) {
            holder.ll.setBackground(context.getDrawable(R.drawable.bg_red_gradient));
        } else {
             if (trans.getMovementOption().equals("SELF DELIVERY")) {
                holder.ll.setBackground(context.getDrawable(R.drawable.bg_green_gradient));
            } else {
                holder.ll.setBackground(context.getDrawable(R.drawable.bg_yellow_gradient));
            }
        }

        holder.tvCustomerName.setText(trans.getCustomerName());
        holder.tvStartDate.setText(trans.getStartDate());
        holder.tvBrand.setText(trans.getVehicleBrand());
        holder.tvModel.setText(trans.getVehicleModel());

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
        TextView tvStartDate;
        ImageView imgMap;
        LinearLayout ll;

        BookedViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.txt_name);
            tvBrand = itemView.findViewById(R.id.txt_brand);
            tvModel = itemView.findViewById(R.id.txt_model);
            tvStartDate = itemView.findViewById(R.id.txt_start_date);
            imgMap = itemView.findViewById(R.id.img_map);
            ll = itemView.findViewById(R.id.ll);
        }
    }
}
