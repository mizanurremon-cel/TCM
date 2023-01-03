package com.cel.tcm.Repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cel.tcm.API.APIUtilize;
import com.cel.tcm.API.ApiService;
import com.cel.tcm.Model.Login_response;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_repository {
    public static Login_repository repository;
    ApiService apiService;
    MutableLiveData<Login_response> datas;

    private Login_repository() {
        apiService = APIUtilize.apiService();
    }

    public synchronized static Login_repository getInstance() {
        if (repository == null) {
            return new Login_repository();
        }

        return repository;
    }

    public @NonNull
    MutableLiveData<Login_response> getLoginToken(String appID, String userName, String password) {
        if (datas == null) {
            datas = new MutableLiveData<>();
        }

//        Call<Login_response> call = apiService.userLogin(appID, userName, password);
//        call.enqueue(new Callback<Login_response>() {
//            @Override
//            public void onResponse(Call<Login_response> call, Response<Login_response> response) {
//                if (response.isSuccessful()) {
//                    Log.d("dataxx", response.body().toString());
//                    datas.postValue(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Login_response> call, Throwable t) {
//                Log.d("dataxx", "error "+t.getMessage());
//                Login_response response = new Login_response();
//                //response.id = -1;
//                datas.postValue(response);
//            }
//        });

        return datas;
    }
}
