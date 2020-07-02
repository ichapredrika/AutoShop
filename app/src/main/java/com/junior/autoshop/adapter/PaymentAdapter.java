package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.MainAdminActivity;
import com.junior.autoshop.R;
import com.junior.autoshop.UpdateTransCallback;
import com.junior.autoshop.models.Trans;
import com.junior.autoshop.PhpConf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>{
    private ArrayList<Trans> listTrans;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    private UpdateTransCallback callback;

    public PaymentAdapter(Context context, ArrayList<Trans> listTrans, UpdateTransCallback callback) {
        this.context = context;
        this.listTrans = listTrans;
        popUpDialog = new Dialog(context);
        this.callback = callback;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_payment, viewGroup, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, final int position) {
        final Trans trans = listTrans.get(position);
        if (!trans.getPickupOption().equals("null")) {
            holder.tvPickupOption.setText(trans.getPickupOption());
            if (trans.getPickupOption().equals("SELF PICKUP")){
                holder.ll.setBackground(context.getDrawable(R.drawable.bg_green_gradient));
                holder.btnConfirm.setText("Finish Order");
            }else{
                holder.ll.setBackground(context.getDrawable(R.drawable.bg_yellow_gradient));
                holder.btnConfirm.setText("Confirm Payment");
                if(trans.getPaymentProof().equals("null")){
                    holder.btnConfirm.setVisibility(View.GONE);
                }else{
                    holder.btnConfirm.setVisibility(View.VISIBLE);
                }
            }
        }else{
            holder.tvPickupOption.setText("Pickup option has not been set by customer");
            holder.ll.setBackground(context.getDrawable(R.drawable.bg_black_gradient));
            holder.btnConfirm.setVisibility(View.GONE);
        }

        if(!trans.getPaymentProof().equals("null")){
            holder.tvProof.setVisibility(View.VISIBLE);
            holder.imgProof.setVisibility(View.VISIBLE);
            Bitmap profileBitmap = decodeBitmap(trans.getPaymentProof());
            holder.imgProof.setImageBitmap(profileBitmap);
        }else{
            holder.tvProof.setVisibility(View.GONE);
            holder.imgProof.setVisibility(View.GONE);
        }
        holder.tvCustomerName.setText(trans.getCustomerName());
        holder.tvBrand.setText(trans.getVehicleBrand());
        holder.tvModel.setText(trans.getVehicleModel());
        holder.tvStatus.setText(trans.getStatus());
        if(!trans.getLatlongDelivery().equals("null") && trans.getStatus().equals("ON DELIVERY")){
            holder.imgMap.setVisibility(View.VISIBLE);
            holder.btnConfirm.setVisibility(View.GONE);
        }else holder.imgMap.setVisibility(View.GONE);
        if(trans.getTotalPrice().equals("null") || trans.getTotalPrice().isEmpty()){
            holder.tvTotal.setText("Rp. 0");
        }else{
            double price = Double.parseDouble(trans.getTotalPrice());
            holder.tvTotal.setText(context.getString(R.string.amount_parse,df.format(price)));
        }

        holder.imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.google.com/maps/dir/?api=1&destination=" + trans.getLatlongDelivery() + "&travelmode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });

        holder.imgProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.setContentView(R.layout.pop_up_image);
                ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
                ImageView imgProof = popUpDialog.findViewById(R.id.img_proof);

                Bitmap profileBitmap = decodeBitmap(trans.getPaymentProof());
                imgProof.setImageBitmap(profileBitmap);

                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUpDialog.dismiss();
                    }
                });

                if (popUpDialog.getWindow() != null) {
                    popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                popUpDialog.show();
            }
        });

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.setContentView(R.layout.pop_up_confirmation);
                final TextView tvQuestion = popUpDialog.findViewById(R.id.txt_question);
                tvQuestion.setText("Are you sure?");
                Button btnYes = popUpDialog.findViewById(R.id.btn_yes);
                Button btnNo = popUpDialog.findViewById(R.id.btn_no);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(trans.getPickupOption().equals("SELF PICKUP")){
                            finishTrans(position,trans.getId());
                        }else{
                            changeStatus(position, trans.getId());
                        }

                        popUpDialog.dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUpDialog.dismiss();
                    }
                });
                if (popUpDialog.getWindow() != null) {
                    popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                popUpDialog.show();
            }
        });

        holder.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = trans.getCustomerContact();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone number", phone);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context.getApplicationContext(),"Phone Number Copied to Clipboard!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private void changeStatus(final int position, final String transId) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_CHANGE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json on delivery", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    if(response.equals("1")){
                        listTrans.get(position).setStatus("ON DELIVERY");
                        notifyDataSetChanged();
                    } else Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(context,context.getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                params.put("STATUS", "ON DELIVERY");
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    private void finishTrans(final int position, final String transId) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_FINISH_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json finish", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    loading.dismiss();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if (response.equals("1")){
                        listTrans.remove(position);
                        notifyDataSetChanged();
                        callback.update();
                    }
                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(context, context.getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = dateFormat.format(c);

                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                params.put("STATUS", "FINISHED");
                params.put("DATE", date);
                params.put("AUTOSHOP_ID", listTrans.get(position).getAutoshopId());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName;
        TextView tvBrand;
        TextView tvModel;
        TextView tvTotal;
        Button btnContact;
        Button btnConfirm;
        LinearLayout ll;
        ImageView imgProof;
        TextView tvProof;
        TextView tvPickupOption;
        TextView tvStatus;
        ImageView imgMap;

        PaymentViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.txt_name);
            tvBrand = itemView.findViewById(R.id.txt_brand);
            tvModel = itemView.findViewById(R.id.txt_model);
            tvTotal = itemView.findViewById(R.id.txt_total);
            ll = itemView.findViewById(R.id.ll);
            btnContact = itemView.findViewById(R.id.btn_contact);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            imgProof = itemView.findViewById(R.id.img_proof);
            tvProof = itemView.findViewById(R.id.txt_proof);
            tvStatus = itemView.findViewById(R.id.txt_status);
            tvPickupOption = itemView.findViewById(R.id.txt_pickup_option);
            imgMap = itemView.findViewById(R.id.img_map);

        }
    }
}
