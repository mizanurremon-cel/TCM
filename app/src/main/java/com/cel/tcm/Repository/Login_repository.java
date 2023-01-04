package com.cel.tcm.Repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cel.tcm.API.APIUtilize;
import com.cel.tcm.API.ApiService;
import com.cel.tcm.Model.LoginPost;
import com.cel.tcm.Model.Login_response;
import com.cel.tcm.Utils.Constants;

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

        LoginPost loginPost = new LoginPost();
        loginPost.setAppId(appID);
        loginPost.setLoginId(userName);
        loginPost.setPassword(password);

        Log.d("dataxx", userName+" "+password);
        Call<Login_response> call = apiService.userLogin(loginPost);
        call.enqueue(new Callback<Login_response>() {
            @Override
            public void onResponse(Call<Login_response> call, Response<Login_response> response) {
                if (response.isSuccessful()) {
                    Log.d("dataxx", "userID:: "+String.valueOf(response.body().id));
                    datas.postValue(response.body());
                }else {
                    Log.d("dataxx", "error ");
                    Login_response response1 = new Login_response();
                    response1.id = -1;
                    datas.postValue(response1);
                }
            }

            @Override
            public void onFailure(Call<Login_response> call, Throwable t) {
                Log.d("dataxx", "error " + t.getMessage());
                Login_response response = new Login_response();
                response.id = -1;
                datas.postValue(response);
            }
        });

        return datas;
    }
}
