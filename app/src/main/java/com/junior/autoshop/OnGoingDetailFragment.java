package com.junior.autoshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.junior.autoshop.adapter.SelectedServiceAdapter;
import com.junior.autoshop.adapter.TransCostAdapter;
import com.junior.autoshop.models.Customer;
import com.junior.autoshop.models.Service;
import com.junior.autoshop.models.Trans;
import com.junior.autoshop.models.TransCost;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;
import static com.junior.autoshop.ChooseAutoshopFragment.EXTRA_AUTOSHOP;

public class OnGoingDetailFragment extends Fragment {
    public static String EXTRA_TRANS_ID = "TRANSACTION_ID";
    public static String EXTRA_AUTOSHOP_DELIVERY = "AUTOSHOP DELIVERY";
    public static String EXTRA_SELF_PICKUP = "SELF PICKUP";
    private static final int PIC_ID = 123;
    private static final int REQUEST_CAMERA = 1;
    private String transId;
    private ProgressDialog loading;
    private UserPreference mUserPreference;
    private RecyclerView rvCost;
    private RecyclerView rvInvoice;
    private TransCostAdapter transCostAdapter, transCostAdapterInvoice;
    private ArrayList<TransCost> listTransCost = new ArrayList<>();
    private ArrayList<TransCost> listTransCostToAdapter = new ArrayList<>();
    private ArrayList<Service> listComplaints = new ArrayList<>();
    private ArrayList<Service> listComplaintsToAdapter = new ArrayList<>();
    private Dialog popUpDialog;
    private Customer customer;
    private Trans trans;
    private TextView tvDate;

    private ImageView imgCancel, imgProof;
    private String imageProof;
    private TextView tvName, tvVehicleName;
    private Button btnContact, btnComplaints, btnPickupOption, btnFinish;
    private Button btnIssue, btnBrowse;
    private TextView tvTotal, tvProgress, tvPickupOption;
    private ImageView imgQrcode;
    private RecyclerView rvComplaints;
    private TextView txtRefresh;
    private SelectedServiceAdapter selectedServiceAdapter;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    PieChart pieChart;
    ArrayList<Entry> listTotalPie = new ArrayList<Entry>();

    public OnGoingDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_on_going_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgCancel = view.findViewById(R.id.img_cancel);
        tvName = view.findViewById(R.id.txt_name);
        tvVehicleName = view.findViewById(R.id.txt_vehicle_name);
        btnContact = view.findViewById(R.id.btn_contact);
        btnComplaints = view.findViewById(R.id.btn_complaints);
        btnPickupOption = view.findViewById(R.id.btn_pickup_option);
        btnIssue = view.findViewById(R.id.btn_issue);
        rvCost = view.findViewById(R.id.rv_cost);
        imgQrcode = view.findViewById(R.id.img_qr);
        pieChart = view.findViewById(R.id.piechart);
        tvProgress = view.findViewById(R.id.txt_progress);
        tvTotal = view.findViewById(R.id.txt_total);
        txtRefresh = view.findViewById(R.id.txt_refresh);
        tvPickupOption = view.findViewById(R.id.txt_pickup_option);
        btnFinish = view.findViewById(R.id.btn_finish);

        popUpDialog = new Dialog(getContext());
        mUserPreference = new UserPreference(getContext());
        customer = mUserPreference.getCustomer();

        transCostAdapter = new TransCostAdapter(getContext(), listTransCostToAdapter, false);
        transCostAdapter.notifyDataSetChanged();
        rvCost.setHasFixedSize(true);
        rvCost.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCost.setAdapter(transCostAdapter);

        transId = getArguments().getString(EXTRA_TRANS_ID);

        //getDetail();
        txtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetail();
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone;
                if (trans.getMovementOption().equals(BookingDetailFragment.EXTRA_SELF_DELIVERY)) {
                    phone = trans.getAdminContact();
                } else {
                    phone = trans.getPickupContact();
                }
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone number", phone);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(), "Phone Number Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        btnComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getComplaints();
                popUpComplaints();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpCancelTrans();
            }
        });

        btnPickupOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpPickupMovement();
            }
        });

        btnIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trans.getPickupOption().equals(EXTRA_AUTOSHOP_DELIVERY)) {
                    if (!trans.getLatlongDelivery().equals("null")) {
                        popUpPayment();
                    } else {
                        Intent changeLoc = new Intent(getContext(), MapActivity.class);
                        changeLoc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        changeLoc.putExtra(EXTRA_AUTOSHOP, trans);
                        changeLoc.putExtra(MapActivity.EXTRA_ORIGIN, MapActivity.EXTRA_PICKUP);
                        getContext().startActivity(changeLoc);
                    }
                } else if (trans.getPickupOption().equals(EXTRA_SELF_PICKUP)) {
                    popUpInvoice();
                }
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
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
                        finishTrans();
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

    private void updateUI() {
        tvName.setText(trans.getAutoshopName());
        tvVehicleName.setText(trans.getVehicleName());
        if (!trans.getTotalPrice().equals("null")) {
            double total = Double.parseDouble(trans.getTotalPrice());
            tvTotal.setText(getContext().getString(R.string.amount_parse, df.format(total)));
        }

        imgQrcode.setVisibility(View.GONE);
        imgCancel.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);
        btnIssue.setVisibility(View.GONE);
        btnPickupOption.setVisibility(View.GONE);
        tvPickupOption.setVisibility(View.VISIBLE);

        if (trans.getStatus().equals("ON QUEUE")) {
            imgQrcode.setVisibility(View.VISIBLE);
            imgCancel.setVisibility(View.VISIBLE);
        } if (trans.getStatus().equals("REISSUE")) {
            imgQrcode.setVisibility(View.VISIBLE);
            imgCancel.setVisibility(View.VISIBLE);
        }else if (trans.getStatus().equals("WAITING FOR PAYMENT")) {
            if (trans.getPickupOption().equals("null")) {
                btnPickupOption.setVisibility(View.VISIBLE);
            } else {
                tvPickupOption.setVisibility(View.VISIBLE);
                tvPickupOption.setText(trans.getPickupOption());
                btnIssue.setVisibility(View.VISIBLE);
                if (trans.getPickupOption().equals(EXTRA_SELF_PICKUP)) {
                    btnIssue.setText("Invoice");
                } else if (trans.getPickupOption().equals(EXTRA_AUTOSHOP_DELIVERY)) {
                    if (trans.getLatlongDelivery().equals("null") || trans.getLatlongDelivery().equals("")) {
                        btnIssue.setText("Set Delivery Location");
                    } else btnIssue.setText("Payment");
                }
            }
        } else if (trans.getStatus().equals("ON DELIVERY")) {
            btnFinish.setVisibility(View.VISIBLE);
            btnIssue.setVisibility(View.VISIBLE);
            btnIssue.setText("Payment Info");
        }

        listTotalPie.clear();
        if (trans.getProgress().equals("null")) {
            float progress = 0;
            tvProgress.setText(progress + " %");
            listTotalPie.add(new Entry(progress, 0));
            listTotalPie.add(new Entry(100, 1));
        } else {
            float progress = Float.parseFloat(trans.getProgress());
            tvProgress.setText(progress + " %");
            listTotalPie.add(new Entry(progress, 0));
            listTotalPie.add(new Entry(100 - progress, 1));
        }

        createPieChart();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(transId, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgQrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void popUpPickupMovement() {
        popUpDialog.setContentView(R.layout.pop_up_pickup_movement);
        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
        Button btnSetMovement = popUpDialog.findViewById(R.id.btn_set_movement);
        final RadioGroup rgMovement = popUpDialog.findViewById(R.id.rg_movement);

        btnSetMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rgMovement.getCheckedRadioButtonId() == R.id.rb_self_pickup) {
                    changePickupOption(EXTRA_SELF_PICKUP);
                } else changePickupOption(EXTRA_AUTOSHOP_DELIVERY);
                popUpDialog.dismiss();
            }
        });

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

    private void popUpComplaints() {
        popUpDialog.setContentView(R.layout.pop_up_complaints);

        rvComplaints = popUpDialog.findViewById(R.id.rv_complaints);
        selectedServiceAdapter = new SelectedServiceAdapter(getContext(), listComplaintsToAdapter);
        selectedServiceAdapter.notifyDataSetChanged();
        rvComplaints.setHasFixedSize(true);
        rvComplaints.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComplaints.setAdapter(selectedServiceAdapter);

        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
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

    private void popUpInvoice() {
        popUpDialog.setContentView(R.layout.pop_up_invoice);
        rvInvoice = popUpDialog.findViewById(R.id.rv_cost);
        TextView tvTotal = popUpDialog.findViewById(R.id.txt_total);
        transCostAdapterInvoice = new TransCostAdapter(getContext(), listTransCostToAdapter, false);
        transCostAdapterInvoice.notifyDataSetChanged();
        rvInvoice.setHasFixedSize(true);
        rvInvoice.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInvoice.setAdapter(transCostAdapterInvoice);
        tvDate = popUpDialog.findViewById(R.id.txt_date);
        final TextView tvTime = popUpDialog.findViewById(R.id.txt_time);
        Button btnConfirm = popUpDialog.findViewById(R.id.btn_confirm);
        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);

        if (!trans.getPickupDate().equals("null")) {
            btnConfirm.setVisibility(View.GONE);
            tvDate.setText(trans.getPickupDate());
            tvTime.setText(trans.getPickupTime());
            tvTime.setFocusable(false);
            tvDate.setFocusable(false);
        } else {
            btnConfirm.setVisibility(View.VISIBLE);
            tvTime.setFocusable(true);
            tvDate.setFocusable(true);
        }

        double total = Double.parseDouble(trans.getTotalPrice());
        tvTotal.setText(getContext().getString(R.string.amount_parse, df.format(total)));

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(!trans.getPickupDate().equals("null")){
                    DatePickerFragment datePickerFragment = new DatePickerFragment();
                    datePickerFragment.show(getActivity().getSupportFragmentManager(), "DATE_TAG");
                }*/
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(tvDate.getText())) {
                    tvDate.setError("Date Can't be Empty!");
                }
                if (TextUtils.isEmpty(tvTime.getText())) {
                    tvTime.setError("Time Can't be Empty!");
                } else if (tvTime.getText().toString().trim().length() != 8) {
                    tvDate.setError("Time Format should be 'hh:mm:ss'");
                }else if (tvDate.getText().toString().trim().length() != 10) {
                    tvDate.setError("Date Format should be 'yyyy:MM:dd'");
                }
                else {
                    if (!TextUtils.isEmpty(tvDate.getText())) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat formatSlash = new SimpleDateFormat("yyyy/MM/dd");
                        try {
                            //JodaTimeAndroid.init(getContext());
                            Date currentDate = new Date();
                            Date dateFromTvDt = formatSlash.parse(tvDate.getText().toString().trim());
                            DateTime dtCur = new DateTime(currentDate);
                            DateTime dtFin = new DateTime(dateFromTvDt);
                            Days d = Days.daysBetween(dtCur, dtFin);
                            int days = d.getDays();

                            if (days < 0) {
                                tvDate.setError("Can't use past dates!");
                            }  else {
                                String date = tvDate.getText().toString().trim().replace('/', '-');
                                if(days==0){
                                    confirmPickupTime(date, tvTime.getText().toString().trim());
                                }else{
                                    double price = Double.parseDouble(trans.getOvernightFee())* (double) days;
                                    addCost(Double.toString(price), "Overnight Fee", date, tvTime.getText().toString().trim());
                                }

                                popUpDialog.dismiss();
                            }
                        } catch (ParseException e) {
                            tvDate.setError("Date format should be 'yyyy/MM/dd' !");
                        }
                    }
                }
            }
        });

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

    private void popUpPayment() {
        popUpDialog.setContentView(R.layout.pop_up_payment);

        TextView tvBank = popUpDialog.findViewById(R.id.txt_bank);
        TextView tvAccNumber = popUpDialog.findViewById(R.id.txt_account_number);
        TextView tvContact = popUpDialog.findViewById(R.id.txt_contact);
        TextView tvTotal = popUpDialog.findViewById(R.id.txt_total);
        TextView tvC = popUpDialog.findViewById(R.id.txt_c);
        TextView tvT = popUpDialog.findViewById(R.id.txt_t);
        TextView tvAn = popUpDialog.findViewById(R.id.txt_an);
        imgProof = popUpDialog.findViewById(R.id.img_proof);
        btnBrowse = popUpDialog.findViewById(R.id.btn_browse);
        Button btnUpload = popUpDialog.findViewById(R.id.btn_upload);

        tvBank.setText(trans.getAutoshopBank());
        tvAccNumber.setText(trans.getAutoshopAccountNumber());
        tvContact.setText(trans.getAdminContact());
        double total = Double.parseDouble(trans.getTotalPrice());
        tvTotal.setText(getContext().getString(R.string.amount_parse, df.format(total)));

        if (!trans.getPaymentProof().equals("null")) {
            Bitmap profileBitmap = decodeBitmap(trans.getPaymentProof());
            imgProof.setImageBitmap(profileBitmap);
            imgProof.setVisibility(View.VISIBLE);
            btnBrowse.setVisibility(View.GONE);
            btnUpload.setVisibility(View.GONE);
        }

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageProof != null) {
                    uploadPaymentProof();
                    popUpDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Add the image first!", Toast.LENGTH_SHORT).show();
            }
        });

        tvAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("account number", trans.getAutoshopAccountNumber());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(), "Account Number Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        tvT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("total price", trans.getTotalPrice());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(), "Total Price Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        tvC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone number", trans.getAdminContact());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(), "Phone Number Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView imgClose = popUpDialog.findViewById(R.id.img_close);
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

    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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
                    imgProof.setImageBitmap(photoBitmap);
                    imgProof.setVisibility(View.VISIBLE);
                    btnBrowse.setVisibility(View.GONE);
                    imageProof = getStringImage(photoBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PIC_ID) {
                photoBitmap = (Bitmap) data.getExtras().get("data");
                btnBrowse.setVisibility(View.GONE);
                imgProof.setImageBitmap(photoBitmap);
                imageProof = getStringImage(photoBitmap);
                imgProof.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addCost(final String price, final String serviceAct,final  String date, final String time) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_ADD_TRANS_COST, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json add trans cost", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    if (response.equals("1")) {
                        confirmPickupTime(date, time);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", String.valueOf(error));
                Toast.makeText(getContext(), getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                double total = 0;
                if (trans.getTotalPrice().equals("null")){
                    total = Double.parseDouble(price);
                }else{
                    total = Double.parseDouble(trans.getTotalPrice()) + Double.parseDouble(price);
                }

                params.put("TRANSACTION_ID", transId);
                params.put("SERVICE_ACT", serviceAct);
                params.put("TOTAL_PRICE", Double.toString(total));
                params.put("PRICE", price);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    private void uploadPaymentProof() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPLOAD_PAYMENT_PROOF, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json upload proof", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        getDetail();
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
                params.put("TRANSACTION_ID", trans.getId());
                params.put("PHOTO", imageProof);
                return params;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }


    private void confirmPickupTime(final String date, final String time) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_UPDATE_PICKUP_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json update p time", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                    if (response.equals("1")) {
                        getDetail();
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
                params.put("TRANSACTION_ID", trans.getId());
                params.put("DATE", date);
                params.put("TIME", time);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    private void popUpCancelTrans() {
        popUpDialog.setContentView(R.layout.pop_up_confirmation);
        Button btnYes = popUpDialog.findViewById(R.id.btn_yes);
        Button btnNo = popUpDialog.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTrans();
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

    private void createPieChart() {
        ArrayList<String> listTransaction = new ArrayList<>();
        listTransaction.add("");
        listTransaction.add("");
        PieDataSet dataSet = new PieDataSet(listTotalPie, "");
        PieData data = new PieData(listTransaction, dataSet);
        pieChart.setData(data);
        dataSet.setColors(new int[]{R.color.colorBlack, R.color.colorDark5}, getContext());
        pieChart.setContentDescription(null);
        pieChart.setDescription(null);
        pieChart.animateXY(3000, 3000);
    }

    private void getComplaints() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_TRANS_SERVICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json trans service", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listComplaints.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            Service service = new Service(object);
                            listComplaints.add(service);
                        }
                        updateComplaintsAdapter(listComplaints);
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
                params.put("TRANSACTION_ID", transId);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void getTransCost() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_TRANS_COST, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json trans cost", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);
                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listTransCost.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            TransCost transCost = new TransCost(object);
                            listTransCost.add(transCost);
                        }
                        updateAdapter(listTransCost);
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
                params.put("TRANSACTION_ID", transId);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void finishTrans() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
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
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    if (response.equals("1")) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_STATE, MainActivity.STATE_HISTORY);
                        FragmentManager mFragmentManager = getFragmentManager();
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        startActivity(intent);
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
                Toast.makeText(getContext(), getContext().getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = dateFormat.format(c);

                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                params.put("STATUS", "FINISHED");
                params.put("DATE", date);
                params.put("AUTOSHOP_ID", trans.getAutoshopId());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    private void getDetail() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_GET_ONGOING_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("Json trans Detail", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    String response = jo.getString("response");

                    loading.dismiss();

                    if (response.equals("1")) {
                        listTransCost.clear();
                        trans = new Trans(jo);
                        updateUI();
                        getTransCost();
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
                params.put("TRANSACTION_ID", transId);
                Log.d("param", params.toString());
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void cancelTrans() {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_CANCEL_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json cancel trans", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    loading.dismiss();

                    if (response.equals("1")) {
                        Toast.makeText(getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else
                        Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getContext(), getContext().getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void changePickupOption(final String option) {
        loading = ProgressDialog.show(getContext(), "Loading Data...", "Please Wait...", false, false);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, PhpConf.URL_CHANGE_PICKUP_OPTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {

                    Log.d("Json change pickup", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");
                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String response = jo.getString("response");
                    String message = jo.getString("message");
                    loading.dismiss();
                    if (response.equals("1")) {
                        getDetail();
                    }
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getContext(), getContext().getString(R.string.msg_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("TRANSACTION_ID", transId);
                params.put("PICKUP_OPTION", option);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void updateAdapter(ArrayList<TransCost> list) {
        listTransCostToAdapter.clear();
        listTransCostToAdapter.addAll(list);
        transCostAdapter.notifyDataSetChanged();
    }

    private void updateComplaintsAdapter(ArrayList<Service> list) {
        listComplaintsToAdapter.clear();
        listComplaintsToAdapter.addAll(list);
        selectedServiceAdapter.notifyDataSetChanged();
    }

    /*@Override
    public void onDialogDateSet(String tag, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        tvDate.setText(dateFormat.format(calendar.getTime()));
        //startDate = dateFormat.format(calendar.getTime());
    }*/

    @Override
    public void onResume() {
        super.onResume();
        getDetail();
    }
}
