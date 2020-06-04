package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.junior.autoshop.SelectedServiceCallback;
import com.junior.autoshop.models.Service;

import java.util.ArrayList;


public class SelectedServiceAdapter extends RecyclerView.Adapter<SelectedServiceAdapter.SelectedServiceViewHolder> {
    private ArrayList<Service> listService;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;

    public SelectedServiceAdapter(Context context, ArrayList<Service> listService) {
        this.context = context;
        this.listService = listService;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public SelectedServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_selected_service, viewGroup, false);
        return new SelectedServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectedServiceViewHolder holder, final int position) {
        final Service service = listService.get(position);
        holder.tvName.setText(service.getType());
        holder.tvNote.setText(service.getNote());
    }

    @Override
    public int getItemCount() {
        return listService.size();
    }

    class SelectedServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvNote;

        SelectedServiceViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvNote = itemView.findViewById(R.id.txt_note);
        }
    }

}
