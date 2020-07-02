package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
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


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private ArrayList<Service> listService;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private SelectedServiceCallback callback;

    public ServiceAdapter(Context context, ArrayList<Service> listService, SelectedServiceCallback callback) {
        this.context = context;
        this.listService = listService;
        popUpDialog = new Dialog(context);
        this.callback = callback;

    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_booking, viewGroup, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ServiceViewHolder holder, final int position) {
        final Service service = listService.get(position);
        holder.tvName.setText(service.getType());
        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, service.getDetail(), Toast.LENGTH_SHORT).show();
            }
        });

        if (service.isSelected()){
            holder.imgCheckbox.setImageDrawable(context.getDrawable(R.drawable.ic_box_checked));
        }else {
            holder.imgCheckbox.setImageDrawable(context.getDrawable(R.drawable.ic_box));
        }

        holder.imgCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                service.setSelected(!service.isSelected());
                if (service.isSelected()==true) {
                    popUpDialog.setContentView(R.layout.pop_up_note);
                    popUpDialog.setCanceledOnTouchOutside(false);
                    final TextView tvNote = popUpDialog.findViewById(R.id.txt_note);
                    Button btnAdd = popUpDialog.findViewById(R.id.btn_add_note);
                    Button btnNoNote = popUpDialog.findViewById(R.id.btn_no_note);
                    ImageView imgClose = popUpDialog.findViewById(R.id.img_close);

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String note = tvNote.getText().toString().trim();
                            listService.get(position).setNote(note);
                            listService.get(position).setSelected(true);
                            callback.selectService(listService.get(position));
                            //notifyDataSetChanged();
                            popUpDialog.dismiss();
                        }
                    });

                    btnNoNote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String note = "";
                            listService.get(position).setNote(note);
                            listService.get(position).setSelected(true);
                            callback.selectService(listService.get(position));
                            //notifyDataSetChanged();
                            popUpDialog.dismiss();
                        }
                    });

                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String note = "";
                            listService.get(position).setNote(note);
                            listService.get(position).setSelected(true);
                            callback.selectService(listService.get(position));
                            //notifyDataSetChanged();
                            popUpDialog.dismiss();
                        }
                    });
                    if (popUpDialog.getWindow() != null) {
                        popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    popUpDialog.show();

                } else {
                    String note = "";
                    listService.get(position).setNote(note);
                    listService.get(position).setSelected(false);
                    callback.deleteService(listService.get(position));
                    //notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listService.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgInfo;
        ImageView imgCheckbox;

        ServiceViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            imgInfo = itemView.findViewById(R.id.img_info);
            imgCheckbox = itemView.findViewById(R.id.img_checkbox);
        }
    }

}
