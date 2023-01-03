package com.cel.tcm.API;

import com.cel.tcm.Model.Login_response;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    Call<Login_response> userLogin(@Field("appId") String appId,
                                   @Field("loginId") String loginId,
                                   @Field("password") String password);
}
