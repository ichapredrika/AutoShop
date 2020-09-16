package com.junior.autoshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junior.autoshop.adapter.AddServiceAutoshopAdapter;
import com.junior.autoshop.adapter.ServiceAutoshopAdapter;
import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.ServiceAutoshop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;

public class ProfileAutoshopFragment extends Fragment implements OnMapReadyCallback {
    private static final String EXTRA_AUTOSHOP = "AUTOSHOP";
    private static final int PIC_ID = 123;
    private static final int REQUEST_CAMERA = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private DecimalFormat df = new DecimalFormat("#,###.###");

    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private AddServiceAutoshopAdapter addServiceAutoshopAdapterEdit;
    private Autoshop autoshop;
    private ImageView imgEditProfile, imgEditService, imgAutoshop, imgAutoshopEdit;
    private TextView tvName, tvUsername, tvEmail, tvPickerContact, tvAdminContact;
    private TextView tvAddress, tvLatlong, tvSpace, tvBank, tvAccount, tvOpenHours, tvCloseHours, tvDeliveryFee, tvOvernightFee;

    private String imageAutoshop;
    private Dialog popUpDialog;

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    MarkerOptions markerOptions;

    private RecyclerView rvServices, rvService;
    private ServiceAutoshopAdapter serviceAutoshopAdapter;
    private ArrayList<ServiceAutoshop> listServiceAutoshop = new ArrayList<>();
    private ArrayList<ServiceAutoshop> listServiceAutoshopToAdapter = new ArrayList<>();
    private ArrayList<ServiceAutoshop> listServiceAutoshopAll = new ArrayList<>();
    private ArrayList<ServiceAutoshop> listServiceAutoshopToAdapterAll = new ArrayList<>();


    public ProfileAutoshopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_autoshop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserPreference = new UserPreference(getContext());
        autoshop = mUserPreference.getAutoshop();

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        markerOptions = new MarkerOptions();

        initProfile(view);

        popUpDialog = new Dialog(getContext());
        Button btnLogout = view.findViewById(R.id.btn_logout);
        final Button btnChangeLoc = view.findViewById(R.id.btn_change_loc);
        rvServices = view.findViewById(R.id.rv_services);

        serviceAutoshopAdapter = new ServiceAutoshopAdapter(getContext(), listServiceAutoshopToAdapter);
        serviceAutoshopAdapter.notifyDataSetChanged();
        rvServices.setHasFixedSize(true);
        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvServices.setAdapter(serviceAutoshopAdapter);

        //getProfile();

        imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpEditProfile();
            }
        });

        imgEditService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpEditService();
            }
        });

        btnChangeLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeLoc = new Intent(getContext(), MapActivity.class);
                changeLoc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                changeLoc.putExtra(MapActivity.EXTRA_ORIGIN, MapActivity.EXTRA_PROFILE);
                getContext().startActivity(changeLoc);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                UserPreference mUserPreference = new UserPreference(getContext());
                mUserPreference.logoutUser();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initProfile(View view) {
        imgAutoshop = view.findViewById(R.id.img_autoshop);
        imgEditProfile = view.findViewById(R.id.img_edit_profile);
        imgEditService = view.findViewById(R.id.img_edit_service);
        tvName = view.findViewById(R.id.txt_name);
        tvUsername = view.findViewById(R.id.txt_username);
        tvEmail = view.findViewById(R.id.txt_email);
        tvPickerContact = view.findViewById(R.id.txt_phone_picker);
        tvAdminContact = view.findViewById(R.id.txt_phone_admin);
        tvAddress = view.findViewById(R.id.txt_address);
        tvLatlong = view.findViewById(R.id.txt_latlong);
        tvSpace = view.findViewById(R.id.txt_space);
        tvBank = view.findViewById(R.id.txt_bank);
        tvAccount = view.findViewById(R.id.txt_account_number);
        tvOpenHours = view.findViewById(R.id.txt_open_hours);
        tvCloseHours = view.findViewById(R.id.txt_close_hours);
        tvDeliveryFee = view.findViewById(R.id.txt_delivery_fee);
        tvOvernightFee = view.findViewById(R.id.txt_overnight_fee);
    }

    void updateUi(Autoshop autoshop) {
        tvName.setText(autoshop.getName());
        tvUsername.setText(autoshop.getUsername());
        tvEmail.setText(autoshop.getEmail());
        tvPickerContact.setText(autoshop.getPickerContact());
        tvAdminContact.setText(autoshop.getAdminContact());
        tvAddress.setText(autoshop.getAddress());
        tvLatlong.setText(autoshop.getLatlong());
        tvBank.setText(autoshop.getBank());
        tvSpace.setText(autoshop.getSpace());
        tvAccount.setText(autoshop.getAccountNumber());
        tvOpenHours.setText(autoshop.getOpenHours());
        tvCloseHours.setText(autoshop.getCloseHours());

        double overnightFee = Double.parseDouble(autoshop.getOvernightFee());
        double deliveryFee = Double.parseDouble(autoshop.getDeliveryFee());

        tvDeliveryFee.setText(getString(R.string.amount_parse, df.format(deliveryFee)));
        tvOvernightFee.setText(getString(R.string.amount_parse, df.format(overnightFee)));

        if(!autoshop.getPhoto().equals("null")){
            Bitmap profileBitmap = decodeBitmap(autoshop.getPhoto());
            imgAutoshop.setImageBitmap(profileBitmap);
        }

        /*String[] arrSplit = autoshop.getLatlong().split(",");

        LatLng latLng = new LatLng(Double.parseDouble(arrSplit[0]), Double.parseDouble(arrSplit[1]));
        mCurrLocationMarker.setPosition(latLng);*/
        LatLng latLng;
        if (autoshop.getLatlong().equals("null")) {
            latLng = new LatLng(0, 0);
        } else {
            String[] arrSplit = autoshop.getLatlong().split(",");
            latLng = new LatLng(Double.parseDouble(arrSplit[0]), Double.parseDouble(arrSplit[1]));
        }
        markerOptions.position(latLng);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
        markerOptions.icon(icon);
        markerOptions.anchor(0.5f, 1.0f);
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    private void popUpEditProfile() {
        popUpDialog.setContentView(R.layout.pop_up_edit_profile_autoshop);
        imgAutoshopEdit = popUpDialog.findViewById(R.id.img_autoshop);
        final TextView tvName = popUpDialog.findViewById(R.id.txt_name);
        final TextView tvUsername = popUpDialog.findViewById(R.id.txt_username);
        final TextView tvEmail = popUpDialog.findViewById(R.id.txt_email);
        final TextView tvPickerContact = popUpDialog.findViewById(R.id.txt_phone_picker);
        final TextView tvAdminContact = popUpDialog.findViewById(R.id.txt_phone_admin);
        final TextView tvAddress = popUpDialog.findViewById(R.id.txt_address);
        final TextView tvLatlong = popUpDialog.findViewById(R.id.txt_latlong);
        final TextView tvSpace = popUpDialog.findViewById(R.id.txt_space);
        final TextView tvBank = popUpDialog.findViewById(R.id.txt_bank);
        final TextView tvAccount = popUpDialog.findViewById(R.id.txt_account_number);
        final TextView tvOpenHours = popUpDialog.findViewById(R.id.txt_open_hours);
        final TextView tvCloseHours = popUpDialog.findViewById(R.id.txt_close_hours);
        final CheckBox cb24hrs = popUpDialog.findViewById(R.id.cb_24hrs);
        final TextView tvDeliveryFee = popUpDialog.findViewById(R.id.txt_delivery_fee);
        final TextView tvOvernightFee = popUpDialog.findViewById(R.id.txt_overnight_fee);

        tvName.setText(autoshop.getName());
        tvUsername.setText(autoshop.getUsername());
        tvEmail.setText(autoshop.getEmail());
        tvPickerContact.setText(autoshop.getPickerContact());
        tvAdminContact.setText(autoshop.getAdminContact());
        tvAddress.setText(autoshop.getAddress());
        tvLatlong.setText(autoshop.getLatlong());
        tvBank.setText(autoshop.getBank());
        tvSpace.setText(autoshop.getSpace());
        tvAccount.setText(autoshop.getAccountNumber());
        tvOpenHours.setText(autoshop.getOpenHours());
        tvCloseHours.setText(autoshop.getCloseHours());
        tvDeliveryFee.setText(autoshop.getDeliveryFee());
        tvOvernightFee.setText(autoshop.getOvernightFee());

        Button btnUpdate = popUpDialog.findViewById(R.id.btn_update);
        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);

        if(!autoshop.getPhoto().equals("null")){
            Bitmap profileBitmap = decodeBitmap(autoshop.getPhoto());
            imageAutoshop = autoshop.getPhoto();
            imgAutoshopEdit.setImageBitmap(profileBitmap);
        }

        cb24hrs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if (isChecked) {
                       tvOpenHours.setText("00:00:00");
                       tvOpenHours.setFocusable(false);
                       tvCloseHours.setText("00:00:00");
                       tvCloseHours.setFocusable(false);
                   } else {
                       tvOpenHours.setFocusable(true);
                       tvCloseHours.setFocusable(true);
                   }
               }
           }
        );


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.dismiss();
            }
        });

        imgAutoshopEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvName.getText().toString().trim().isEmpty()) {
                    tvName.setError("Autoshop Name Can't be Empty!");
                    tvName.requestFocus();
                } else if (tvUsername.getText().toString().trim().isEmpty()) {
                    tvUsername.setError("Username Can't be Empty!");
                    tvUsername.requestFocus();
                } else if (tvEmail.getText().toString().trim().isEmpty()) {
                    tvEmail.setError("Email Field Can't be Empty!");
                    tvEmail.requestFocus();
                } else if (!tvEmail.getText().toString().trim().contains("@") || !tvEmail.getText().toString().trim().contains(".com")) {
                    tvEmail.setError("Email is not valid!");
                    tvEmail.requestFocus();
                } else if (tvPickerContact.getText().toString().trim().isEmpty()) {
                    tvPickerContact.setError("Picker Contact Field Can't be Empty!");
                    tvPickerContact.requestFocus();
                } else if (tvAdminContact.getText().toString().isEmpty()) {
                    tvAdminContact.setError("Admin Contact Field Can't be Empty!");
                    tvAdminContact.requestFocus();
                } else if (tvAddress.getText().toString().isEmpty()) {
                    tvAddress.setError("Address Field Can't be Empty!");
                    tvAddress.requestFocus();
                } else if (tvLatlong.getText().toString().isEmpty()) {
                    tvLatlong.setError("Latlong Field Can't be Empty!");
                    tvLatlong.requestFocus();
                } else if (tvBank.getText().toString().isEmpty()) {
                    tvBank.setError("Bank Field Can't be Empty!");
                    tvBank.requestFocus();
                } else if (tvAccount.getText().toString().isEmpty()) {
                    tvAccount.setError("Account Number Field Can't be Empty!");
                    tvAccount.requestFocus();
                } else if (tvOpenHours.getText().toString().isEmpty()) {
                    tvOpenHours.setError("Open Hours Field Can't be Empty!");
                    tvOpenHours.requestFocus();
                } else if (tvCloseHours.getText().toString().isEmpty()) {
                    tvCloseHours.setError("Close Hours Field Can't be Empty!");
                    tvCloseHours.requestFocus();
                } else if (tvDeliveryFee.getText().toString().isEmpty()) {
                    tvDeliveryFee.setError("Delivery Fee Field Can't be Empty!");
                    tvDeliveryFee.requestFocus();
                } else if (tvOvernightFee.getText().toString().isEmpty()) {
                    tvOvernightFee.setError("Overnight Fee Field Can't be Empty!");
                    tvOvernightFee.requestFocus();
                } else {
                    Autoshop newAutoshop = new Autoshop();
                    newAutoshop.setId(autoshop.getId());
                    newAutoshop.setName(tvName.getText().toString().trim());
                    newAutoshop.setUsername(tvUsername.getText().toString().trim());
                    newAutoshop.setEmail(tvEmail.getText().toString().trim());
                    newAutoshop.setPickerContact(tvPickerContact.getText().toString().trim());
                    newAutoshop.setAdminContact(tvAdminContact.getText().toString().trim());
                    newAutoshop.setAddress(tvAddress.getText().toString().trim());
                    newAutoshop.setLatlong(tvLatlong.getText().toString().trim());
                    newAutoshop.setBank(tvBank.getText().toString().trim());
                    newAutoshop.setSpace(tvSpace.getText().toString().trim());
                    newAutoshop.setAccountNumber(tvAccount.getText().toString().trim());
                    newAutoshop.setOpenHours(tvOpenHours.getText().toString().trim());
                    newAutoshop.setCloseHours(tvCloseHours.getText().toString().trim());
                    newAutoshop.setDeliveryFee(tvDeliveryFee.getText().toString().trim());
                    newAutoshop.setOvernightFee(tvOvernightFee.getText().toString().trim());
                    newAutoshop.setPhoto(imageAutoshop);

                    updateProfile(newAutoshop);
                    popUpDialog.dismiss();
                }
            }
        });

        if (popUpDialog.getWindow() != null) {
            popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popUpDialog.show();
    }

    private void popUpEditService() {
        popUpDialog.setContentView(R.layout.pop_up_edit_service);

        getService();
        rvService = popUpDialog.findViewById(R.id.rv_services);
        addServiceAutoshopAdapterEdit = new AddServiceAutoshopAdapter(getContext(), listServiceAutoshopToAdapterAll);
        addServiceAutoshopAdapterEdit.notifyDataSetChanged();
        rvService.setHasFixedSize(true);
        rvService.setLayoutManager(new LinearLayoutManager(getContext()));
        rvService.setAdapter(addServiceAutoshopAdapterEdit);

        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.dismiss();
                getProfile();
            }
        });

        if (popUpDialog.getWindow() != null) {
            popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popUpDialog.show();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 0);
    }

    private void takePhotoFromCamera() {
        if (checkPermission()) {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, PIC_ID);
        } else {
            Toast.makeText(getContext(), "Please allow camera permission!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photoBitmap;
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 0) {
                Uri selectedImage = data.getData();
                try {
                    photoBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage));
                    imgAutoshopEdit.setImageBitmap(photoBitmap);
                    //BitmapHelper.getInstance().setBitmap(photoBitmap);
                    imageAutoshop = getStringImage(photoBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PIC_ID) {
                photoBitmap = (Bitmap) data.getExtras().get("data");
                imgAutoshopEdit.setImageBitmap(photoBitmap);
                //BitmapHelper.getInstance().setBitmap(photoBitmap);
                imageAutoshop = getStringImage(photoBitmap);
            }
        }
    }

    private void updateAdapter(ArrayList<ServiceAutoshop> list) {
        listServiceAutoshopToAdapter.clear();
        listServiceAutoshopToAdapter.addAll(list);
        serviceAutoshopAdapter.notifyDataSetChanged();
    }

    private void updateAdapterAll(ArrayList<ServiceAutoshop> list) {
        listServiceAutoshopToAdapterAll.clear();
        listServiceAutoshopToAdapterAll.addAll(list);
        addServiceAutoshopAdapterEdit.notifyDataSetChanged();
    }

    private void getService() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.GET, PhpConf.URL_GET_SERVICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json service", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listServiceAutoshopAll.clear();
                        JSONArray service = jo.getJSONArray("DATA");
                        for (int i = 0; i < service.length(); i++) {
                            JSONObject object = service.getJSONObject(i);
                            ServiceAutoshop serviceAutoshop = new ServiceAutoshop(object);
                            listServiceAutoshop.add(serviceAutoshop);
                            for (int j = 0; j < listServiceAutoshopToAdapter.size(); j++) {
                                if (serviceAutoshop.getServiceId().equals(listServiceAutoshopToAdapter.get(j).getServiceId())) {
                                    serviceAutoshop.setChecked(true);
                                    serviceAutoshop.setId(listServiceAutoshopToAdapter.get(j).getId());
                                }
                            }
                            listServiceAutoshopAll.add(serviceAutoshop);
                        }
                        updateAdapterAll(listServiceAutoshopAll);

                    } else {
                        String message = jo.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(mStringRequest);
    }

    private void getProfile() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_PROFILE_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json profile", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listServiceAutoshop.clear();
                        JSONObject profile = jo.getJSONArray("PROFILE").getJSONObject(0);
                        Autoshop autoshop = new Autoshop(profile);
                        saveAdmin(autoshop);
                        updateUi(autoshop);

                        JSONArray service = jo.getJSONArray("SERVICE");
                        for (int i = 0; i < service.length(); i++) {
                            JSONObject object = service.getJSONObject(i);
                            ServiceAutoshop serviceAutoshop = new ServiceAutoshop(object);
                            listServiceAutoshop.add(serviceAutoshop);
                        }
                        updateAdapter(listServiceAutoshop);

                    } else {
                        String message = jo.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("AUTOSHOP_ID", autoshop.getId());

                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void updateProfile(final Autoshop newAutoshop) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPDATE_PROFILE_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json update profile", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        getProfile();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("AUTOSHOP_ID", newAutoshop.getId());
                params.put("NAME", newAutoshop.getName());
                params.put("ADDRESS", newAutoshop.getAddress());
                params.put("LATLONG", newAutoshop.getLatlong());
                params.put("ADMIN_CONTACT", newAutoshop.getAdminContact());
                params.put("PICKUP_CONTACT", newAutoshop.getPickerContact());
                params.put("SPACE", newAutoshop.getSpace());
                params.put("BANK", newAutoshop.getBank());
                params.put("ACCOUNT_NUMBER", newAutoshop.getAccountNumber());
                params.put("PHOTO", newAutoshop.getPhoto());
                params.put("USERNAME", newAutoshop.getUsername());
                params.put("EMAIL", newAutoshop.getEmail());
                params.put("OPEN_HOURS", newAutoshop.getOpenHours());
                params.put("CLOSE_HOURS", newAutoshop.getCloseHours());
                params.put("OVERNIGHT_FEE", newAutoshop.getOvernightFee());
                params.put("DELIVERY_FEE", newAutoshop.getDeliveryFee());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void updateLoc(final String latlong) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPDATE_LOCATION_AUTOSHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json update LOC", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        getProfile();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("AUTOSHOP_ID", autoshop.getId());
                params.put("LATLONG", latlong);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    void saveAdmin(Autoshop autoshop) {
        UserPreference userPreference = new UserPreference(getContext());
        autoshop.setId(autoshop.getId());
        autoshop.setName(autoshop.getName());
        autoshop.setPassword(autoshop.getPassword());
        autoshop.setUsername(autoshop.getUsername());
        autoshop.setEmail(autoshop.getEmail());
        autoshop.setAdminContact(autoshop.getAdminContact());
        autoshop.setPickerContact(autoshop.getPickerContact());
        autoshop.setAddress(autoshop.getAddress());
        autoshop.setLatlong(autoshop.getLatlong());
        autoshop.setSpace(autoshop.getSpace());
        autoshop.setBank(autoshop.getBank());
        autoshop.setAccountNumber(autoshop.getAccountNumber());
        autoshop.setPhoto(autoshop.getPhoto());
        autoshop.setOpenHours(autoshop.getOpenHours());
        autoshop.setCloseHours(autoshop.getCloseHours());

        userPreference.setAutoshop(autoshop);
        userPreference.setType(EXTRA_AUTOSHOP);

        this.autoshop = mUserPreference.getAutoshop();
    }

    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMap!=null)
            mMap.clear();
        getProfile();

    }


}
