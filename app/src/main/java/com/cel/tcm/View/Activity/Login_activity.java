package com.cel.tcm.View.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cel.tcm.API.APIUtilize;
import com.cel.tcm.API.ApiService;
import com.cel.tcm.Model.Login_response;
import com.cel.tcm.Utils.Constants;
import com.cel.tcm.Utils.CryptUtil;
import com.cel.tcm.Utils.ShowToast;
import com.cel.tcm.ViewModel.ViewModelUtil;
import com.cel.tcm.databinding.ActivityLoginBinding;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_activity extends AppCompatActivity implements View.OnClickListener {

    ActivityLoginBinding binding;
    ViewModelUtil viewModelUtil;

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

                    String userPassMerge = userName + ":" + password;
                    try {
                        userPassMerge = CryptUtil.encrypt(userPassMerge, "AmlangikarBD2@23", "AmlangikarBD2@23");

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

                    Log.d("dataxx", Constants.APP_ID + " user:: " + userName + " pass:: " + password);

                    /*viewModelUtil.getLoginToken(Constants.APP_ID, userName, password).observe(Login_activity.this, new Observer<Login_response>() {
                        @Override
                        public void onChanged(Login_response login_response) {
                            Toast.makeText(Login_activity.this, String.valueOf(login_response.id), Toast.LENGTH_SHORT).show();
                        }
                    });*/


                    ApiService apiService = APIUtilize.apiService();
                    apiService.userLogin(Constants.APP_ID, userName, password).enqueue(new Callback<Login_response>() {
                        @Override
                        public void onResponse(Call<Login_response> call, Response<Login_response> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(Login_activity.this, String.valueOf(response.body().id), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Login_response> call, Throwable t) {

                            Log.d("dataxx", t.getMessage());
                        }
                    });

                }

            }
        });

    }

    private void init_view() {
        viewModelUtil = new ViewModelProvider(this).get(ViewModelUtil.class);

    }

    @Override
    public void onClick(View view) {

    }

}