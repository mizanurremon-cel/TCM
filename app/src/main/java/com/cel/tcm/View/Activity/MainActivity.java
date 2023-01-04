package com.cel.tcm.View.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.cel.tcm.API.APIUtilize;
import com.cel.tcm.API.ApiService;
import com.cel.tcm.Adapter.CoolerListAdapter;
import com.cel.tcm.Model.CoolerBasicResponse;
import com.cel.tcm.Model.CoolerPropertiesResponse;
import com.cel.tcm.Model.OutletsResponse;
import com.cel.tcm.Model.POSMAssetResponse;
import com.cel.tcm.Model.RoutesResponse;
import com.cel.tcm.Model.SalesPointsResponse;
import com.cel.tcm.Network.NetworkChangeReceiver;
import com.cel.tcm.R;
import com.cel.tcm.Sessions.SessionManager;
import com.cel.tcm.Utils.Constants;
import com.cel.tcm.Utils.ShowToast;
import com.cel.tcm.databinding.ActivityMainBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CoolerListAdapter.OnItemClickListener {

    ActivityMainBinding binding;
    SessionManager sessionManager;
    String userID, userType, bearToken;

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;
    ApiService apiService;

    List<CoolerBasicResponse.Value> coolerList;

    CoolerListAdapter coolerListAdapter;

    private BroadcastReceiver mNetworkReceiver;

    static Dialog networkErrorAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init_view();


        binding.logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.removeUserID();
                startActivity(new Intent(getApplicationContext(), Login_activity.class));
                finish();
            }
        });

        binding.pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDateFun();
            }
        });


        binding.qrScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "qr", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(binding.qrCodeText.getText().toString().trim())) {
                    get_cooler_brand("1", 0, "");
                    get_cooler_capacity("2", 0, "");
                    get_cooler_shelve("3", 0, "");

                }
            }
        });


        //Toast.makeText(this, userID+" "+userType, Toast.LENGTH_SHORT).show();
        //Log.d("dataxx", userID + " " + userType + " " + bearToken);

        getSalesPoints();

        if (!checkStoragePermission()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        mNetworkReceiver = new NetworkChangeReceiver();
       registerNetworkBroadcastForNougat();

    }

    public static void dialog(boolean value) {

        if (value) {

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("messagexx", "available");
                    networkErrorAlert.dismiss();

                }
            };
            handler.postDelayed(delayrunnable, 3000);
        } else {

            Log.d("messagexx", "networkError");
            networkErrorAlert.show();
        }
    }


    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void getSalesPoints() {

        // String token = bearToken
        //Log.d("dataxx", "load " + token + " " + userType);
        apiService.getSalesPointByUserID(bearToken, userType).enqueue(new Callback<SalesPointsResponse>() {
            @Override
            public void onResponse(Call<SalesPointsResponse> call, Response<SalesPointsResponse> response) {
                if (response.isSuccessful()) {

                    //Log.d("dataxx", "successs");
                    List<String> itemList = new ArrayList<>();
                    itemList.add("Select Distributor");
                    for (int i = 0; i < response.body().value.size(); i++) {
                        itemList.add(response.body().value.get(i).salesPointName);
                    }

                    ArrayAdapter<String> aa = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, itemList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.distributorSpinner.setAdapter(aa);

                    binding.distributorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            int pos = adapterView.getSelectedItemPosition();
                            //Toast.makeText(MainActivity.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                            if (pos > 0) {
                                String salesPointID = response.body().value.get(pos - 1).salesPointId.toString();
                                get_routes(salesPointID);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Log.d("dataxx", "error" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<SalesPointsResponse> call, Throwable t) {
                Log.d("dataxx", "error" + t.getMessage());
            }
        });
    }

    private void get_routes(String salesPointID) {

        apiService.getRoutesBySalesUserID(bearToken, salesPointID, Constants.AUTH_STATUS).enqueue(new Callback<RoutesResponse>() {
            @Override
            public void onResponse(Call<RoutesResponse> call, Response<RoutesResponse> response) {
                if (response.isSuccessful()) {

                    //Log.d("dataxx", "successs");
                    List<String> itemList = new ArrayList<>();
                    itemList.add("Select Route");
                    for (int i = 0; i < response.body().value.size(); i++) {
                        itemList.add(response.body().value.get(i).name);
                    }

                    ArrayAdapter<String> aa = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, itemList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.routeSpinner.setAdapter(aa);

                    binding.routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            int pos = adapterView.getSelectedItemPosition();
                            //Toast.makeText(MainActivity.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                            if (pos > 0) {
                                String routesID = response.body().value.get(pos - 1).routeId.toString();
                                get_outlet(routesID);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Log.d("dataxx", "error" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<RoutesResponse> call, Throwable t) {

            }
        });
    }

    private void get_outlet(String routesID) {

        apiService.getOutletsByRoutesID(bearToken, routesID, Constants.AUTH_STATUS).enqueue(new Callback<OutletsResponse>() {
            @Override
            public void onResponse(Call<OutletsResponse> call, Response<OutletsResponse> response) {
                if (response.isSuccessful()) {

                    //Log.d("dataxx", "successs");
                    List<String> itemList = new ArrayList<>();
                    itemList.add("Select Outlet");
                    for (int i = 0; i < response.body().value.size(); i++) {
                        itemList.add(response.body().value.get(i).name);
                    }

                    ArrayAdapter<String> aa = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, itemList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.outletSpinner.setAdapter(aa);

                    binding.outletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            int pos = adapterView.getSelectedItemPosition();
                            //Toast.makeText(MainActivity.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                            if (pos > 0) {
                                String outletID = response.body().value.get(pos - 1).outletId.toString();
                                get_cooler(outletID);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Log.d("dataxx", "error" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<OutletsResponse> call, Throwable t) {
                Log.d("dataxx", "error" + t.getMessage());
            }
        });
    }

    private void get_cooler(String id) {
        //Toast.makeText(this, outletID, Toast.LENGTH_SHORT).show();
        binding.qrScanButton.setEnabled(true);
        apiService.getCoolerList(bearToken, id, Constants.AUTH_STATUS, Constants.ASSET_STATUS).enqueue(new Callback<CoolerBasicResponse>() {
            @Override
            public void onResponse(Call<CoolerBasicResponse> call, Response<CoolerBasicResponse> response) {
                if (response.isSuccessful()) {
                    coolerList = new ArrayList<>();
                    //coolerList.add(new CoolerBasicResponse.Value(0, "Asset Code", "Brand", "Capacity(L)", "Shelf No"));
                    coolerList = response.body().value;

                    if (coolerList != null && coolerList.size() > 0) {
                        binding.itemView.setVisibility(View.VISIBLE);
                        binding.noDataFound.setVisibility(View.GONE);
                        coolerListAdapter = new CoolerListAdapter(coolerList);
                        coolerListAdapter.setOnItemClickListener(MainActivity.this::onItemClick);
                        binding.itemView.setAdapter(coolerListAdapter);
                    } else {
                        binding.noDataFound.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.d("dataxx", "error" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CoolerBasicResponse> call, Throwable t) {
                Log.d("dataxx", "error" + t.getMessage());
            }
        });
    }

    private void pickDateFun() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);


                        binding.dateText.setText(changeDateFormat(calendar.getTime()));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();


    }

    private String changeDateFormat(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = format.format(time);

        return dateString;
    }

    public boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void init_view() {
        apiService = APIUtilize.apiService();
        sessionManager = new SessionManager(getApplicationContext());
        userID = sessionManager.getUserID().toString();
        userType = sessionManager.getUserType();
        bearToken = "Bearer " + sessionManager.getToken().replace("\n", "").trim();

        binding.itemView.setHasFixedSize(true);
        binding.itemView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        networkErrorAlert = new Dialog(MainActivity.this);
        networkErrorAlert.setContentView(R.layout.network_alert);
        networkErrorAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        networkErrorAlert.setCancelable(false);
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(this, coolerList.get(position).assetCode.toString(), Toast.LENGTH_SHORT).show();
        binding.qrCodeText.setText(coolerList.get(position).assetCode.toString());
        get_cooler_brand("1", 0, coolerList.get(position).assetProperty1);
        get_cooler_capacity("2", 0, coolerList.get(position).assetProperty2);
        get_cooler_shelve("3", 0, coolerList.get(position).assetProperty3);

        get_POSM_asset(coolerList.get(position).posmAssetId.toString());

    }

    private void get_POSM_asset(String posmAssetId) {

        apiService.getPOSMAsset(bearToken, posmAssetId).enqueue(new Callback<POSMAssetResponse>() {
            @Override
            public void onResponse(Call<POSMAssetResponse> call, Response<POSMAssetResponse> response) {
                if (response.isSuccessful()) {
                    /*SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        Date date = inputFormat.parse(response.body().placementDate);

                        binding.dateText.setText(changeDateFormat(date));
                    } catch (Exception e) {
                        Log.d("dataxx", "date error "+e.getMessage());
                    }*/

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = null;
                    try {
                        //date = inputFormat.parse("2015-03-04T00:00:00");
                        date = inputFormat.parse(response.body().placementDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String formattedDate = outputFormat.format(date);
                    Log.d("dataxx", "date:: "+formattedDate);
                    binding.dateText.setText(formattedDate);

                    binding.remarksEditText.setEnabled(true);
                    binding.remarksEditText.setText(response.body().remarks);

                    binding.itemImage.setImageBitmap(StringToBitMap(response.body().picture));

                } else {
                    Log.d("dataxx", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<POSMAssetResponse> call, Throwable t) {

            }
        });
    }


    public void get_cooler_brand(String groupType, int item, String value) {
        final int[] selectedItem = {item};
        apiService.getCoolerProperties(bearToken, groupType).enqueue(new Callback<CoolerPropertiesResponse>() {
            @Override
            public void onResponse(Call<CoolerPropertiesResponse> call, Response<CoolerPropertiesResponse> response) {
                if (response.isSuccessful()) {

                    //Log.d("dataxx", "successs");
                    List<String> itemList = new ArrayList<>();
                    itemList.add("Select Brand");
                    for (int i = 0; i < response.body().value.size(); i++) {
                        itemList.add(response.body().value.get(i).item);
                    }

                    ArrayAdapter<String> aa = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, itemList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.cBrandSpinner.setAdapter(aa);


                    binding.cBrandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            int pos = adapterView.getSelectedItemPosition();
                            //Toast.makeText(MainActivity.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                            if (pos > 0) {
                                //String outletID = response.body().value.get(pos - 1).outletId.toString();

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                    if (TextUtils.isEmpty(value)) {
                        binding.cBrandSpinner.setSelection(selectedItem[0]);
                    } else {
                        for (int i = 0; i < itemList.size(); i++) {
                            if (itemList.get(i).equals(value)) {
                                selectedItem[0] = i;
                            }
                        }
                        Log.d("dataxx", value + " " + String.valueOf(selectedItem[0]));
                        binding.cBrandSpinner.setSelection(selectedItem[0]);
                    }
                } else {
                    Log.d("dataxx", "error" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CoolerPropertiesResponse> call, Throwable t) {

            }
        });
    }


    public void get_cooler_capacity(String groupType, int item, String value) {
        final int[] selectedItem = {item};
        apiService.getCoolerProperties(bearToken, groupType).enqueue(new Callback<CoolerPropertiesResponse>() {
            @Override
            public void onResponse(Call<CoolerPropertiesResponse> call, Response<CoolerPropertiesResponse> response) {
                if (response.isSuccessful()) {

                    //Log.d("dataxx", "successs");
                    List<String> itemList = new ArrayList<>();
                    itemList.add("Select Brand");
                    for (int i = 0; i < response.body().value.size(); i++) {
                        itemList.add(response.body().value.get(i).item);
                    }

                    ArrayAdapter<String> aa = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, itemList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.cCapacitySpinner.setAdapter(aa);


                    binding.cCapacitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            int pos = adapterView.getSelectedItemPosition();
                            //Toast.makeText(MainActivity.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                            if (pos > 0) {
                                //String outletID = response.body().value.get(pos - 1).outletId.toString();

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    if (TextUtils.isEmpty(value)) {
                        binding.cCapacitySpinner.setSelection(selectedItem[0]);
                    } else {
                        for (int i = 0; i < itemList.size(); i++) {
                            if (itemList.get(i).equals(value)) {
                                selectedItem[0] = i;
                            }
                        }
                        Log.d("dataxx", value + " " + String.valueOf(selectedItem[0]));
                        binding.cCapacitySpinner.setSelection(selectedItem[0]);
                    }
                } else {
                    Log.d("dataxx", "error" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CoolerPropertiesResponse> call, Throwable t) {

            }
        });
    }


    public void get_cooler_shelve(String groupType, int item, String value) {
        final int[] selectedItem = {item};
        apiService.getCoolerProperties(bearToken, groupType).enqueue(new Callback<CoolerPropertiesResponse>() {
            @Override
            public void onResponse(Call<CoolerPropertiesResponse> call, Response<CoolerPropertiesResponse> response) {
                if (response.isSuccessful()) {

                    //Log.d("dataxx", "successs");
                    List<String> itemList = new ArrayList<>();
                    itemList.add("Select Brand");
                    for (int i = 0; i < response.body().value.size(); i++) {
                        itemList.add(response.body().value.get(i).item);
                    }

                    ArrayAdapter<String> aa = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, itemList);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.cShelveSpinner.setAdapter(aa);

                    binding.cShelveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            int pos = adapterView.getSelectedItemPosition();
                            //Toast.makeText(MainActivity.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                            if (pos > 0) {
                                //String outletID = response.body().value.get(pos - 1).outletId.toString();

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    if (TextUtils.isEmpty(value)) {
                        binding.cShelveSpinner.setSelection(selectedItem[0]);
                    } else {
                        for (int i = 0; i < itemList.size(); i++) {
                            if (itemList.get(i).equals(value)) {
                                selectedItem[0] = i;
                            }
                        }
                        Log.d("dataxx", value + " " + String.valueOf(selectedItem[0]));
                        binding.cShelveSpinner.setSelection(selectedItem[0]);
                    }
                } else {
                    Log.d("dataxx", "error" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CoolerPropertiesResponse> call, Throwable t) {

            }
        });
    }


    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }


}