package com.cel.tcm.View.Activity;

import static android.media.MediaRecorder.VideoSource.CAMERA;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.cel.tcm.API.APIUtilize;
import com.cel.tcm.API.ApiService;
import com.cel.tcm.Adapter.CoolerListAdapter;
import com.cel.tcm.Adapter.Item_alert_adapter;
import com.cel.tcm.Model.CoolerBasicResponse;
import com.cel.tcm.Model.CoolerPropertiesResponse;
import com.cel.tcm.Model.OutletsResponse;
import com.cel.tcm.Model.POSMAssetResponse;
import com.cel.tcm.Model.RoutesResponse;
import com.cel.tcm.Model.SalesPointsResponse;
import com.cel.tcm.Model.SaveCoolerInfoResponse;
import com.cel.tcm.Network.NetworkChangeReceiver;
import com.cel.tcm.R;
import com.cel.tcm.Sessions.SessionManager;
import com.cel.tcm.Utils.Constants;
import com.cel.tcm.Utils.GlobalActivityResult;
import com.cel.tcm.Utils.ProgressRequestBody;
import com.cel.tcm.Utils.ShowToast;


import com.cel.tcm.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CoolerListAdapter.OnItemClickListener, Item_alert_adapter.OnAlertItemClickListener, ProgressRequestBody.UploadCallbacks, LocationListener {

    ActivityMainBinding binding;
    SessionManager sessionManager;
    String userID, userType, bearToken;

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;
    ApiService apiService;

    List<CoolerBasicResponse.Value> coolerList;

    CoolerListAdapter coolerListAdapter;

    private BroadcastReceiver mNetworkReceiver;

    static Dialog networkErrorAlert;
    static Dialog permissionAlert;

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String mImageCapturePath;
    protected final GlobalActivityResult<Intent, ActivityResult> activityLauncher = GlobalActivityResult.registerActivityForResult(this);

    Bitmap thumbnail;
    ProgressDialog progressDialog;
    Boolean startProgress = false;

    String posmAssetId = "0", customerId, mhNodeId, assetCode;
    String assetProperty1, assetProperty2, assetProperty3;
    String placementDate, imgString = "", remarks, latitude;
    String longitude, status, assetStatus;
    String realPath;

    Item_alert_adapter adapter;

    List<SalesPointsResponse.Value> salesPointsList;
    List<RoutesResponse.Value> routesList;
    List<OutletsResponse.Value> outletsList;
    List<CoolerPropertiesResponse.Value> coolerPropertiesList;

    String salesPointsID, routesID, outletsID;

    Dialog spinnerAlert;
    RecyclerView alertItemView;
    ImageView closeButton;
    TextView titleText;

    private LocationManager manager;
    private Location AddressLocation;
    int check = 0;

    int LOCATION_REFRESH_TIME = 15000; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update
    private LocationManager mLocationManager;


    EditText searchEditText;
    String alertType;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("dataxx", "resultcode:: " + String.valueOf(resultCode));
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            binding.itemImage.setImageBitmap(photo);
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            realPath = getRealPathFromURI(tempUri);

            imgString = imageToString(photo);
            Log.d("gataxx", "image:: " + imgString);

        } else if (requestCode == 12) {
            Log.e("GPS 11", "Called");
            if (resultCode == RESULT_OK) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("GPS 1", "Called");
                    //GPS Permission not Granted

                } else {
                    Log.e("GPS 2", "Called");
                    //GPS Permission Granted
                    checkLocationPermission();
                }
            } else {
                Log.e("GPS 22", "Called");
                //GPS Permission Granted
                checkLocationPermission();
            }
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("GPS 12", "Called" + requestCode);
        switch (requestCode) {
            case 12:
                Log.e("GPS 11", "Called");
                if (resultCode == RESULT_OK) {
                    LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.e("GPS 1", "Called");
                        //GPS Permission not Granted

                    } else {
                        Log.e("GPS 2", "Called");
                        //GPS Permission Granted
                        checkLocationPermission();
                    }
                } else {
                    Log.e("GPS 22", "Called");
                    //GPS Permission Granted
                    checkLocationPermission();
                }


                break;
            case 101:
                checkPermission();
                break;
        }


    }*/


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        inImage = Bitmap.createScaledBitmap(inImage, inImage.getWidth() / 4, inImage.getHeight() / 4, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        int imageSize = byteSizeOf(inImage);
        Log.d("dataxx", "size:: " + String.valueOf(imageSize / 1024) + "kb");
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init_view();

        //requestMultiplePermissions();

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
                    //get_cooler_brand("1");
                    //get_cooler_capacity("2");
                    //get_cooler_shelve("3");


                    disableTextViews(false);
                }

                qr_code_fun();
            }
        });


        if (!checkStoragePermission()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        binding.imageChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {

                    openSettingsDialog();
                } else {
                    showPictureDialog();
                }
            }
        });


        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
                //Log.d("dataxx")
                save_data_to_server();
            }
        });

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();


        binding.distributorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setText("Distributor List");
                getSalesPoints();

            }
        });


        binding.routeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setText("Routes List");
                get_routes(salesPointsID);
            }
        });

        binding.outletsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setText("Outlets List");
                get_outlet(routesID);
            }
        });


        binding.coolerBrandText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_cooler_properties("1", Constants.BRAND);
            }
        });

        binding.coolerCapacityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_cooler_properties("2", Constants.CAPACITY);
            }
        });

        binding.coolerShelveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_cooler_properties("3", Constants.SHELVE);
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerAlert.dismiss();
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                //search
                //Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                /*List<RoutesResponse.Value> searchList = new ArrayList<>();

                if (TextUtils.isEmpty(editable)) {
                    searchList.clear();
                    adapter = new Item_alert_adapter(null, routesList, null, null, Constants.ROUTES);
                    alertItemView.setAdapter(adapter);
                } else {
                    String value = editable.toString();
                    if (alertType.equals(Constants.ROUTES)) {

                        for (int i = 0; i < routesList.size(); i++) {
                            if (routesList.get(i).name.toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT))) {
                                searchList.add(routesList.get(i));
                            }
                        }

                        Log.d("dataxx", String.valueOf(searchList.size()));
                        for (int i = 0; i < searchList.size(); i++) {
                            Log.d("dataxx", "search:: " + searchList.get(i).name);
                        }

                        adapter = new Item_alert_adapter(null, searchList, null, null, Constants.ROUTES);
                        alertItemView.setAdapter(adapter);
                    }
                }*/

            }
        });


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

        Window window = networkErrorAlert.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);


        //networkErrorAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);

        spinnerAlert = new Dialog(MainActivity.this);
        spinnerAlert.setContentView(R.layout.item_alert);
        spinnerAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spinnerAlert.setCancelable(false);

        Window window2 = spinnerAlert.getWindow();
        WindowManager.LayoutParams wlp2 = window2.getAttributes();
        wlp2.gravity = Gravity.CENTER;
        wlp2.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        wlp2.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp2);

        alertItemView = spinnerAlert.findViewById(R.id.alertItemView);
        alertItemView.setHasFixedSize(true);
        alertItemView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        closeButton = spinnerAlert.findViewById(R.id.closeButton);

        titleText = spinnerAlert.findViewById(R.id.titleText);
        searchEditText = spinnerAlert.findViewById(R.id.searchEditText);

        status = String.valueOf(Constants.AUTH_STATUS);
        assetStatus = String.valueOf(Constants.ASSET_STATUS);


        permissionAlert = new Dialog(MainActivity.this);
        permissionAlert.setContentView(R.layout.grant_permission_alert);
        permissionAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        permissionAlert.setCancelable(false);

        Window window3 = permissionAlert.getWindow();
        WindowManager.LayoutParams wlp3 = window3.getAttributes();
        wlp3.gravity = Gravity.CENTER;
        wlp3.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        wlp3.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp3);
    }

    private void save_data_to_server() {
        //Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
        assetProperty1 = binding.coolerBrandText.getText().toString().trim();
        assetProperty2 = binding.coolerCapacityText.getText().toString().trim();
        assetProperty3 = binding.coolerShelveText.getText().toString().trim();
        assetCode = binding.qrCodeText.getText().toString().trim();
        placementDate = binding.dateText.getText().toString().trim();
        status = Constants.AUTH_STATUS;
        assetStatus = Constants.ASSET_STATUS;
        remarks = binding.remarksEditText.getText().toString().trim();
        //latitude = "20.36";
        //longitude = "90.36";

//        Log.d("dataxx", "req data:: " + bearToken + " posmAssetId: " +
//                posmAssetId + "  " + customerId + " " + mhNodeId + " " + assetCode + " " + assetProperty1 + " " +
//                assetProperty2 + " " + assetProperty3 + " " + placementDate + " " + latitude + " " + longitude + " " + status + " " + assetStatus + " " + remarks);
//        Log.d("dataxx", "image" + imgString);

        HashMap<String, String> body = new HashMap<>();
        body.put("bearToken", bearToken);
        body.put("posmID", posmAssetId);
        body.put("customerId", customerId);
        body.put("mhNodeId", mhNodeId);
        body.put("assetCode", assetCode);
        body.put("assetProperty1", assetProperty1);
        body.put("assetProperty2", assetProperty2);
        body.put("assetProperty3", assetProperty3);
        body.put("placementDate", placementDate);
        body.put("latitude", latitude);
        body.put("longitude", longitude);
        body.put("status", status);
        body.put("assetStatus", assetStatus);
        body.put("remarks", remarks);
        //body.put("imgString", imgString);

        Log.d("serverDataxx", body.toString());
        Log.d("serverDataxx", "image" + imgString);

        /*MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("posmAssetId", posmAssetId);
        builder.addFormDataPart("customerId", customerId);
        builder.addFormDataPart("mhNodeId", mhNodeId);
        builder.addFormDataPart("assetCode", assetCode);
        builder.addFormDataPart("assetProperty1", assetProperty1);
        builder.addFormDataPart("assetProperty2", assetProperty2);
        builder.addFormDataPart("assetProperty3", assetProperty3);
        builder.addFormDataPart("placementDate", placementDate);
        builder.addFormDataPart("remarks", remarks);
        builder.addFormDataPart("latitude", latitude);
        builder.addFormDataPart("longitude", longitude);
        builder.addFormDataPart("status", status);
        builder.addFormDataPart("assetStatus", assetStatus);
        File mFile1 = new File(imagePath);

        // RequestBody reqFile = RequestBody.create(okhttp3.MediaType.parse("image/*"), mFile1);
        //builder.addFormDataPart("picture", mFile1.getName(), RequestBody.create(MediaType.parse("image/*"), mFile1));

        //Log.d("dataxx", "fileName:: "+mFile1.getName());
        ProgressRequestBody fileBody1 = new ProgressRequestBody(mFile1, "multipart/form-data", this);
        builder.addFormDataPart("picture", mFile1.getName(), fileBody1);

        progressDialog = new ProgressDialog(this);
        startProgress = true;

        MultipartBody requestBody = builder.build();*/

        apiService.saveCoolerInformation(bearToken, posmAssetId, customerId, mhNodeId, assetCode, assetProperty1, assetProperty2, assetProperty3, placementDate, imgString, remarks, latitude, longitude, status, assetStatus).enqueue(new Callback<SaveCoolerInfoResponse>() {
            @Override
            public void onResponse(Call<SaveCoolerInfoResponse> call, Response<SaveCoolerInfoResponse> response) {
                //Log.d("dataxx", response.body().returnMessage.get(0).toString());
                //startProgress = true;

                if (response.isSuccessful()) {
                    if (response.body().returnStatus == 200) {
                        ShowToast.onSuccess(getApplicationContext(), String.valueOf(response.body().returnStatus) + " " + response.body().returnMessage.get(0).toString());
                    } else {
                        ShowToast.onError(getApplicationContext(), String.valueOf(response.body().returnStatus) + " " + response.body().returnMessage.get(0).toString());
                    }
                } else {

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        ShowToast.onError(getApplicationContext(), jObjError.getString("returnMessage").toString());
                        /*if (jObjError.getString("returnMessage").equals("403")) {
                            ShowToast.onError(getApplicationContext(), "you need to login again");
                            startActivity(new Intent(getApplicationContext(), Login_activity.class));
                            finish();
                        }*/
                    } catch (Exception e) {

                        Log.d("dataxx", "exception:: " + e.getMessage());
                        ShowToast.onError(getApplicationContext(), getString(R.string.something).toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<SaveCoolerInfoResponse> call, Throwable t) {
                // startProgress = true;
                Log.d("dataxx", "f error: " + t.toString());
            }
        });

    }

    public String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();

        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    private void showPermissionsSettings() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                takePhotoFromCamera();
                                break;

                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void qr_code_fun() {

        Dialog qrCodeDialog = new Dialog(MainActivity.this);
        qrCodeDialog.setContentView(R.layout.qr_code_alert);
        qrCodeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        qrCodeDialog.setCancelable(false);
        qrCodeDialog.show();

        Window window = qrCodeDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);


        TextView extractedCode = qrCodeDialog.findViewById(R.id.extractedCode);
        AppCompatButton closeButton = qrCodeDialog.findViewById(R.id.closeButton);

        AppCompatButton saveButton = qrCodeDialog.findViewById(R.id.saveButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeDialog.dismiss();
            }
        });

        CodeScanner mCodeScanner;
        CodeScannerView scannerView = qrCodeDialog.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.startPreview();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowToast.onSuccess(getApplicationContext(), "scan completed");
                        extractedCode.setText(result.getText());
                        saveButton.setEnabled(true);
                        enableTextView(true);
                        binding.remarksEditText.setEnabled(true);
                        posmAssetId = "0";
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.qrCodeText.setText(extractedCode.getText().toString().trim());
                mCodeScanner.releaseResources();
                qrCodeDialog.dismiss();

            }
        });
    }

    private void getSalesPoints() {

        // String token = bearToken
        Log.d("dataxx", "load " + bearToken + " " + userType);
        apiService.getSalesPointByUserID(bearToken, userType).enqueue(new Callback<SalesPointsResponse>() {
            @Override
            public void onResponse(Call<SalesPointsResponse> call, Response<SalesPointsResponse> response) {
                if (response.isSuccessful()) {

                    if (response.body().value.size() > 0 || response.body().value != null) {
                        spinnerAlert.show();
                        salesPointsList = new ArrayList<>();
                        salesPointsList = response.body().value;
                        adapter = new Item_alert_adapter(salesPointsList, null, null, null, Constants.SALES);
                        adapter.setOnAlertItemClickListener(MainActivity.this::onAlertItemClick);
                        alertItemView.setAdapter(adapter);
                    } else {
                        ShowToast.onError(getApplicationContext(), "No item found");
                    }


                    /*List<String> itemList = new ArrayList<>();
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
                                //posmAssetId = response.body().value.get(pos-1).salesPointId.toString()
                                String salesPointID = response.body().value.get(pos - 1).salesPointId.toString();
                                //posmAssetId = salesPointID;
                                get_routes(salesPointID);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });*/
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

                    if (response.body().value.size() > 0 || response.body().value != null) {
                        alertType = Constants.ROUTES;
                        spinnerAlert.show();
                        routesList = new ArrayList<>();
                        routesList = response.body().value;
                        adapter = new Item_alert_adapter(null, routesList, null, null, Constants.ROUTES);
                        adapter.setOnAlertItemClickListener(MainActivity.this::onAlertItemClick);
                        alertItemView.setAdapter(adapter);
                    } else {
                        ShowToast.onError(getApplicationContext(), "No item found");
                    }
                    //Log.d("dataxx", "successs");
                    /*List<String> itemList = new ArrayList<>();
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
                    });*/
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

                    if (response.body().value.size() > 0 || response.body().value != null) {
                        spinnerAlert.show();
                        outletsList = new ArrayList<>();
                        outletsList = response.body().value;
                        adapter = new Item_alert_adapter(null, null, outletsList, null, Constants.OUTLETS);
                        adapter.setOnAlertItemClickListener(MainActivity.this::onAlertItemClick);
                        alertItemView.setAdapter(adapter);
                    } else {
                        ShowToast.onError(getApplicationContext(), "No item found");
                    }

                    //Log.d("dataxx", "successs");
                    /*List<String> itemList = new ArrayList<>();
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
                    });*/
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
                        enableTextView(true);

                    } else {
                        binding.noDataFound.setVisibility(View.VISIBLE);
                        binding.itemView.setVisibility(View.GONE);

                        disableTextViews(false);
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

    private void enableTextView(boolean state) {
        binding.coolerBrandText.setEnabled(state);
        binding.coolerCapacityText.setEnabled(state);
        binding.coolerShelveText.setEnabled(state);
    }

    public void disableTextViews(boolean state) {
        binding.coolerBrandText.setText(getString(R.string.select_none));
        binding.coolerCapacityText.setText(getString(R.string.select_none));
        binding.coolerShelveText.setText(getString(R.string.select_none));
        binding.qrCodeText.setText("");

        binding.coolerBrandText.setEnabled(state);
        binding.coolerCapacityText.setEnabled(state);
        binding.coolerShelveText.setEnabled(state);

        binding.dateText.setText("");
        binding.dateText.setHint(getString(R.string.dateformat));
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

    @Override
    public void onItemClick(int position) {

        //Toast.makeText(this, coolerList.get(position).assetCode.toString(), Toast.LENGTH_SHORT).show();
        binding.qrCodeText.setText(coolerList.get(position).assetCode.toString());
        //get_cooler_capacity("2", 0, coolerList.get(position).assetProperty2);
        //get_cooler_shelve("3", 0, coolerList.get(position).assetProperty3);

        binding.coolerBrandText.setText(coolerList.get(position).assetProperty1);
        binding.coolerCapacityText.setText(coolerList.get(position).assetProperty2);
        binding.coolerShelveText.setText(coolerList.get(position).assetProperty3);

        posmAssetId = coolerList.get(position).posmAssetId.toString();
        get_POSM_asset(posmAssetId);

    }

    private void get_POSM_asset(String posmAssetId) {

        apiService.getPOSMAsset(bearToken, posmAssetId).enqueue(new Callback<POSMAssetResponse>() {
            @Override
            public void onResponse(Call<POSMAssetResponse> call, Response<POSMAssetResponse> response) {
                if (response.isSuccessful()) {

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
                    Log.d("dataxx", "date:: " + formattedDate);
                    binding.dateText.setText(formattedDate);

                    binding.remarksEditText.setEnabled(true);
                    binding.remarksEditText.setText(response.body().remarks);

                    imgString = response.body().picture;
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
                                assetProperty1 = adapterView.getSelectedItem().toString();
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
                                assetProperty2 = adapterView.getSelectedItem().toString();
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
                                assetProperty3 = adapterView.getSelectedItem().toString();
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

    public void get_cooler_properties(String groupType, String type) {
        apiService.getCoolerProperties(bearToken, groupType).enqueue(new Callback<CoolerPropertiesResponse>() {
            @Override
            public void onResponse(Call<CoolerPropertiesResponse> call, Response<CoolerPropertiesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().value.size() > 0 || response.body().value != null) {
                        spinnerAlert.show();
                        coolerPropertiesList = new ArrayList<>();
                        coolerPropertiesList = response.body().value;
                        adapter = new Item_alert_adapter(null, null, null, coolerPropertiesList, type);
                        adapter.setOnAlertItemClickListener(MainActivity.this::onAlertItemClick);
                        alertItemView.setAdapter(adapter);
                    } else {
                        ShowToast.onError(getApplicationContext(), "No item found");
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<CoolerPropertiesResponse> call, Throwable t) {

            }
        });

    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        //Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                        /*Manifest.permission.ACCESS_COARSE_LOCATION*/
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        for (int i = 0; i < report.getDeniedPermissionResponses().size(); i++) {
                            Log.d("dataxx", "permission:: " + report.getGrantedPermissionResponses().get(i).getPermissionName().toString());

                        }
                        if (report.areAllPermissionsGranted()) {  // check if all permissions are granted
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(getApplicationContext(), "sorry2", Toast.LENGTH_SHORT).show();
                            openSettingsDialog();
                        }

                        /*if (report.isAnyPermissionPermanentlyDenied()) { // check for permanent denial of any permission
                            // show alert dialog navigating to Settings

                            Toast.makeText(getApplicationContext(), "sorry", Toast.LENGTH_SHORT).show();

                            //openSettingsDialog();

                        }*/
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    private void openSettingsDialog() {
        permissionAlert.dismiss();
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            permissionAlert.show();
            TextView goToSettingButton = permissionAlert.findViewById(R.id.goToSettingButton);
            goToSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showPermissionsSettings();
                }
            });
            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);

    }

    @Override
    public void onProgressUpdate(int percentage) {
        if (!startProgress) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(percentage);
        //progressDialog.show();
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    public void dialog(boolean value) {

        if (value) {

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("messagexx", "available");
                    networkErrorAlert.dismiss();
                    //getSalesPoints();

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
        //getSalesPoints();
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAlertItemClick(int position, String type) {

        String id = "";
        if (type.equals(Constants.SALES)) {
            SalesPointsResponse.Value response = salesPointsList.get(position);
            salesPointsID = response.salesPointId.toString();
            if (TextUtils.isEmpty(salesPointsID)) {
                binding.routeText.setEnabled(false);
            } else {
                binding.routeText.setEnabled(true);
                binding.distributorText.setText(response.salesPointName);
            }
            id = salesPointsID;

        } else if (type.equals(Constants.ROUTES)) {
            RoutesResponse.Value response = routesList.get(position);
            routesID = response.routeId.toString();

            if (TextUtils.isEmpty(routesID)) {
                binding.outletsText.setEnabled(false);
            } else {
                binding.outletsText.setEnabled(true);
                binding.routeText.setText(response.name);
            }

            id = routesID;

        } else if (type.equals(Constants.OUTLETS)) {
            OutletsResponse.Value response = outletsList.get(position);
            outletsID = response.outletId.toString();
            customerId = outletsID;
            mhNodeId = response.mhNodeId.toString();
            if (TextUtils.isEmpty(routesID)) {
                //binding.outletsText.setEnabled(false);
            } else {
                //binding.outletsText.setEnabled(true);
                binding.outletsText.setText(response.name);
                get_cooler(outletsID);
            }

            id = routesID;

        } else if (type.equals(Constants.BRAND)) {
            CoolerPropertiesResponse.Value response = coolerPropertiesList.get(position);
            binding.coolerBrandText.setText(response.item);
            id = response.item;
        } else if (type.equals(Constants.CAPACITY)) {
            CoolerPropertiesResponse.Value response = coolerPropertiesList.get(position);
            binding.coolerCapacityText.setText(response.item);
            id = response.item;
        } else if (type.equals(Constants.SHELVE)) {
            CoolerPropertiesResponse.Value response = coolerPropertiesList.get(position);
            binding.coolerShelveText.setText(response.item);
            id = response.item;
        }
        spinnerAlert.dismiss();
        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();
        requestMultiplePermissions();
    }

    private void checkLocationPermission() {
        Log.d("checkPermission", "Check");
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("checkPermission", "Not granted");
            openSettingsDialog();
            return;
        }
        Log.d("checkPermission", "Granted");
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 60000, (LocationListener) this);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 60000, (LocationListener) this);

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("onLocationChanged", "Called" + String.valueOf(location.getLatitude()));
        //CustomProgressDialog.showDialog(getActivity(), false);
        manager.removeUpdates((LocationListener) this);
        AddressLocation = location;
        setLocation();

        //Log.d("testLocation", " lat: " + String.valueOf(AddressLocation.getLatitude()) + " lang " + String.valueOf(AddressLocation.getLongitude()));
    }

    private void setLocation() {
        latitude = String.valueOf(new DecimalFormat("##.#####").format(AddressLocation.getLatitude()));
        longitude = String.valueOf(new DecimalFormat("##.#####").format(AddressLocation.getLongitude()));

        Log.d("dataxx", "lat: " + latitude + " long: " + longitude);
    }


//    private final LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(final Location location) {
//            //your code here
//            Log.d("locationxx", "location:"+"lat: "+String.valueOf(location.getLatitude())+"long: "+String.valueOf(location.getLatitude()));
//        }
//    };
}