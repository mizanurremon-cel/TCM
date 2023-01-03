package com.cel.tcm.API;

import com.cel.tcm.Model.LoginPost;
import com.cel.tcm.Model.Login_response;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @POST("users/login")
    Call<Login_response> userLogin(@Body LoginPost loginPost);
}
