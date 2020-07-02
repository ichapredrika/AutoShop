package com.junior.autoshop.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.junior.autoshop.FavoriteAutoshopCallback;
import com.junior.autoshop.PhpConf;
import com.junior.autoshop.R;
import com.junior.autoshop.SelectedAutoshopCallback;
import com.junior.autoshop.UserPreference;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AutoshopAdapter extends RecyclerView.Adapter<AutoshopAdapter.AutoshopViewHolder> {
    private ArrayList<Autoshop> listAutoshop;
    private Context context;
    private ProgressDialog loading;
    private Dialog popUpDialog;
    private SelectedAutoshopCallback selectedAutoshopCallback;
    private FavoriteAutoshopCallback favoriteAutoshopCallback;
    private Customer customer;
    private UserPreference userPreference;
    private DecimalFormat df = new DecimalFormat("#,###.##");
    public AutoshopAdapter(Context context, ArrayList<Autoshop> listAutoshop, SelectedAutoshopCallback selectedAutoshopCallback, FavoriteAutoshopCallback favoriteAutoshopCallback) {
        this.context = context;
        this.listAutoshop = listAutoshop;
        popUpDialog = new Dialog(context);
        this.selectedAutoshopCallback = selectedAutoshopCallback;
        this.favoriteAutoshopCallback = favoriteAutoshopCallback;

        userPreference =new UserPreference(context);
        customer = userPreference.getCustomer();
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
        if (autoshop.isSelected()){
            holder.imgCheckbox.setImageDrawable(context.getDrawable(R.drawable.ic_box_checked));
            //holder.ll.setBackground(context.getDrawable(R.drawable.bg_gray_gradient));
        }else {
            holder.imgCheckbox.setImageDrawable(context.getDrawable(R.drawable.ic_box));
            //holder.ll.setBackground(context.getDrawable(R.drawable.bg_black_gradient));
        }
        if(autoshop.isFavorite()){
            holder.imgFavorite.setImageDrawable(context.getDrawable(R.drawable.ic_favorited));
        }else holder.imgFavorite.setImageDrawable(context.getDrawable(R.drawable.ic_favorite));

        if(autoshop.isInFavorite()){
            holder.tvAvailable.setVisibility(View.VISIBLE);
            if (autoshop.isAvailable()==true){
                holder.tvAvailable.setText("Available");
            }else holder.tvAvailable.setText("Not Available");
        }else {
            holder.tvAvailable.setVisibility(View.GONE);
        }
        holder.tvName.setText(autoshop.getName());
        holder.tvAddress.setText(autoshop.getAddress());
        holder.tvDistance.setText("Distance: "+ df.format(autoshop.getDistance())+" Km");

        if (autoshop.getPhoto()!=null ){
            Bitmap profileBitmap = decodeBitmap(autoshop.getPhoto());
            holder.imgAutoshop.setImageBitmap(profileBitmap);
        }

        holder.imgCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoshop.isInFavorite()){
                    if(autoshop.isAvailable()){
                        if(autoshop.isSelected()){
                            selectedAutoshopCallback.deleteAutoshop(listAutoshop.get(position));
                            listAutoshop.get(position).setSelected(false);
                            notifyDataSetChanged();
                        }else{
                            selectedAutoshopCallback.selectAutoshop(listAutoshop.get(position));
                            listAutoshop.get(position).setSelected(true);
                            notifyDataSetChanged();
                        }
                    }else Toast.makeText(context, "The autoshop is not available!", Toast.LENGTH_SHORT).show();
                }else{
                    if(autoshop.isSelected()){
                        selectedAutoshopCallback.deleteAutoshop(listAutoshop.get(position));
                        listAutoshop.get(position).setSelected(false);
                        notifyDataSetChanged();
                    }else{
                        selectedAutoshopCallback.selectAutoshop(listAutoshop.get(position));
                        listAutoshop.get(position).setSelected(true);
                        notifyDataSetChanged();
                    }
                }

            }
        });

        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoshop.isFavorite()){
                    unfavAutoshop(position,autoshop.getFavoriteId());
                }else{
                    String favId = customer.getId()+"-"+autoshop.getId();
                    favAutoshop(position,autoshop.getId(),customer.getId(), favId);
                }
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
        LinearLayout ll;
        ImageView imgFavorite, imgCheckbox;
        TextView tvAvailable;

        AutoshopViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txt_name);
            tvAddress = itemView.findViewById(R.id.txt_address);
            tvDistance = itemView.findViewById(R.id.txt_distance);
            imgAutoshop = itemView.findViewById(R.id.img_autoshop);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
            imgCheckbox = itemView.findViewById(R.id.img_checkbox);
            tvAvailable = itemView.findViewById(R.id.txt_available);
            ll= itemView.findViewById(R.id.ll);
        }
    }

    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private void favAutoshop(final int position, final String autoshopId, final String customerId, final String favoriteId ) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_ADD_FAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json favorite", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(response.equals("1")){
                        favoriteAutoshopCallback.favoriteAutoshop(listAutoshop.get(position), favoriteId);
                        listAutoshop.get(position).setFavorite(true);
                        notifyDataSetChanged();
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
                java.util.Map<String, String> params = new HashMap<>();
                params.put("AUTOSHOP_ID", autoshopId);
                params.put("CUSTOMER_ID",customerId);
                params.put("FAVORITE_ID",favoriteId);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void unfavAutoshop(final int position, final String favId) {
        loading = ProgressDialog.show(context, "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_DELETE_FAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json delete favorite", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    if (response.equals("1")){
                        favoriteAutoshopCallback.unfavoriteAutoshop(listAutoshop.get(position));
                        listAutoshop.get(position).setFavorite(false);
                        notifyDataSetChanged();
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
                java.util.Map<String, String> params = new HashMap<>();
                params.put("FAVORITE_ID", favId);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
