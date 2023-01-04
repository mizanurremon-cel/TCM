package com.cel.tcm.View.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cel.tcm.Model.LoginPost;
import com.cel.tcm.Model.Login_response;
import com.cel.tcm.R;
import com.cel.tcm.Sessions.SessionManager;
import com.cel.tcm.Utils.CapitalizeUtils;
import com.cel.tcm.Utils.Constants;
import com.cel.tcm.Utils.CryptUtil;
import com.cel.tcm.Utils.ShowToast;
import com.cel.tcm.ViewModel.ViewModelUtil;
import com.cel.tcm.databinding.ActivityLoginBinding;

import java.util.Locale;

public class Login_activity extends AppCompatActivity implements View.OnClickListener {

    ActivityLoginBinding binding;
    ViewModelUtil viewModelUtil;
    Dialog loader;
    SessionManager sessionManager;

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init_view();


        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.usernameEditText.getText().toString().trim()) || TextUtils.isEmpty(binding.passwordEditText.getText().toString().trim())) {
                    if (TextUtils.isEmpty(binding.usernameEditText.getText().toString().trim())) {
                        ShowToast.onError(getApplicationContext(), "empty username");
                    } else {
                        ShowToast.onError(getApplicationContext(), "empty password");
                    }


                } else {
                    String userName = binding.usernameEditText.getText().toString().trim().toUpperCase(Locale.ROOT);
                    String password = binding.passwordEditText.getText().toString().trim();

                    String userPassMerge = CapitalizeUtils.doCapFirst(userName.toLowerCase(Locale.ROOT)) + ":" + password;
                    try {
                        userPassMerge = CryptUtil.encrypt(userPassMerge, "AmlangikarBD2@23", "AmlangikarBD2@23");
                        Log.d("dataxx", userPassMerge);
                    } catch (Exception e) {
                        Log.d("dataxx", e.getMessage());
                    }

                    try {
                        userName = CryptUtil.encrypt(userName, "Rss_BD_2@@0_CebD", "Rss_BD_2@@0_CebD");

                    } catch (Exception e) {
                        Log.d("dataxx", e.getMessage());
                    }


                    try {
                        password = CryptUtil.encrypt(password, "Rss_BD_2@@0_CebD", "Rss_BD_2@@0_CebD");

                    } catch (Exception e) {
                        Log.d("dataxx", e.getMessage());
                    }


                    LoginPost loginPost = new LoginPost();
                    loginPost.setAppId(Constants.APP_ID);
                    loginPost.setLoginId(userName);
                    loginPost.setPassword(password);


                    loader.show();
                    Log.d("dataxx", "merge:: "+userPassMerge);
                    String finalUserPassMerge = userPassMerge;
                    viewModelUtil.getLoginToken(Constants.APP_ID, userName, password).observe(Login_activity.this, new Observer<Login_response>() {
                        @Override
                        public void onChanged(Login_response login_response) {
                            loader.dismiss();
                            if (login_response.id == -1) {
                                ShowToast.onError(getApplicationContext(), "invalid username/password");
                            } else {
                                sessionManager.saveUserID(String.valueOf(login_response.id));
                                sessionManager.saveUserType(String.valueOf(login_response.userType));
                                sessionManager.saveToken(String.valueOf(finalUserPassMerge));
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }
                    });

//                    ApiService apiService = APIUtilize.apiService();
//                        apiService.userLogin(loginPost).enqueue(new Callback<Login_response>() {
//                        @Override
//                        public void onResponse(Call<Login_response> call, Response<Login_response> response) {
//                            loader.dismiss();
//                            Log.d("sdfghj", "onResponse: "+response.body().toString());
//                            if (response.isSuccessful()) {
//                               //Toast.makeText(Login_activity.this, String.valueOf(response.body().id), Toast.LENGTH_SHORT).show();
//                                sessionManager.saveToken(String.valueOf(response.body().id));
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                finish();
//                           }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Login_response> call, Throwable t) {
//                            loader.dismiss();
//                            Log.d("dataxx", t.getMessage());
//                        }
//                    });

                }

            }
        });

        if (!checkStoragePermission()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

        loader = new Dialog(Login_activity.this);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loader.setCancelable(false);

        viewModelUtil = new ViewModelProvider(this).get(ViewModelUtil.class);

        sessionManager = new SessionManager(getApplicationContext());

    }

    @Override
    public void onClick(View view) {

    }

}