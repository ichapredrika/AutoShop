package com.junior.autoshop.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.MainActivity;
import com.junior.autoshop.R;
import com.junior.autoshop.SendMailTask;
import com.junior.autoshop.models.Trans;
import com.junior.autoshop.PhpConf;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{
    private ArrayList<Trans> listTrans;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    Activity activity ;

    public HistoryAdapter(Context context, ArrayList<Trans> listTrans) {
        this.context = context;
        this.activity = (Activity) context;
        this.listTrans = listTrans;
        popUpDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history, viewGroup, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {
        final Trans trans = listTrans.get(position);

        holder.tvName.setText(trans.getAutoshopName());
        holder.tvVehicleName.setText(trans.getVehicleBrand());
        holder.tvStatus.setText(trans.getStatus());
        if(trans.getTotalPrice().equals("null")){
            holder.tvTotal.setText("Rp. 0");
        }else{
            double price = Double.parseDouble(trans.getTotalPrice());
            holder.tvTotal.setText(context.getString(R.string.amount_parse,df.format(price)));
        }

        if(!trans.getFinishDate().equals("null") && !trans.getFinishDate().equals("") && !trans.getStatus().equals("CANCELLED") ){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date();
            Date finishDate = null;
            try {
                JodaTimeAndroid.init(context);
                finishDate = format.parse(trans.getFinishDate());
                DateTime dtCur = new DateTime(currentDate);
                DateTime dtFin = new DateTime(finishDate);
                Days d = Days.daysBetween(dtCur, dtFin);
                int days = d.getDays();
                if (days==0 || days==-1){
                    holder.btnReissue.setVisibility(View.VISIBLE);
                }else holder.btnReissue.setVisibility(View.GONE);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else holder.btnReissue.setVisibility(View.GONE);

        holder.btnReissue.setOnClickListener(new View.OnClickListener() {
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
                        reIssue(position);
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

    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvVehicleName;
        TextView tvTotal;
        Button btnReissue;
        TextView tvStatus;

        HistoryViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvVehicleName = itemView.findViewById(R.id.txt_vehicle_name);
            tvTotal = itemView.findViewById(R.id.txt_total);
            tvStatus = itemView.findViewById(R.id.txt_status);
            btnReissue = itemView.findViewById(R.id.btn_reissue);
        }
    }

 /*   private void reIssue(final int position) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_CHANGE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json reissue", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    if(response.equals("1")){
                        listTrans.remove(position);
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
                Toast.makeText(context, context.getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", listTrans.get(position).getId());
                params.put("STATUS", "REISSUE");
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
*/
    private void reIssue(final int position) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_REISSUE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json reissue", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    if(response.equals("1")){
                        List<String> toEmailList = new ArrayList<>();
                        toEmailList.add(listTrans.get(position).getAutoshopEmail());
                        Log.i("SendMailActivity", "To List: " + toEmailList);
                        String emailSubject = "New Reissued Order";
                        String emailBody = "There's a reissued order as detailed below:\n" +
                                "Customer: "+listTrans.get(position).getCustomerName()+"\n"+
                                "Movement Option: AUTOSHOP PICKUP\n"+
                                "Type : Reissue \n\n"+
                                "Check Autoshop App now!";
                        new SendMailTask(activity).execute(context.getString(R.string.autoshop_email),
                                context.getString(R.string.autoshop_password), toEmailList, emailSubject, emailBody);

                        listTrans.remove(position);
                        notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, context.getString(R.string.msg_something_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, context.getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String startDate = dateFormat.format(c);

                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", listTrans.get(position).getId());
                params.put("MOVEMENT_OPTION", "AUTOSHOP PICKUP");
                params.put("STATUS", "REISSUE");
                Log.d("ct param", params.toString());
                return params;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

}
