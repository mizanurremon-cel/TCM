package com.cel.tcm.API;

import com.cel.tcm.Model.CoolerBasicResponse;
import com.cel.tcm.Model.CoolerPropertiesResponse;
import com.cel.tcm.Model.LoginPost;
import com.cel.tcm.Model.Login_response;
import com.cel.tcm.Model.OutletsResponse;
import com.cel.tcm.Model.POSMAssetResponse;
import com.cel.tcm.Model.RoutesResponse;
import com.cel.tcm.Model.SalesPointsResponse;
import com.cel.tcm.Model.SaveCoolerInfoResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("users/login")
    Call<Login_response> userLogin(@Body LoginPost loginPost);


    @GET("salespoints/getSalespointsByUserId")
    Call<SalesPointsResponse> getSalesPointByUserID(@Header("Authorization") String authHeader,
                                                    @Query("userType") String userType);


    @GET("routes/getRoutesBySalesPointId")
    Call<RoutesResponse> getRoutesBySalesUserID(@Header("Authorization") String authHeader,
                                                @Query("salespointId") String salesPointId,
                                                @Query("status") String status);

    @GET("outlets/getOutletsByRouteId")
    Call<OutletsResponse> getOutletsByRoutesID(@Header("Authorization") String authHeader,
                                               @Query("routeId") String salesPointId,
                                               @Query("status") String status);


    @GET("coolerRegistration/getCoolersBasic")
    Call<CoolerBasicResponse> getCoolerList(@Header("Authorization") String authHeader,
                                            @Query("customerId") String customerId,
                                            @Query("status") String status,
                                            @Query("assetStatus") String assetStatus);

    @GET("coolerRegistration/getCoolerProperties")
    Call<CoolerPropertiesResponse> getCoolerProperties(@Header("Authorization") String authHeader,
                                                       @Query("GroupType") String GroupType);


    @GET("coolerRegistration/getPosmAsset")
    Call<POSMAssetResponse> getPOSMAsset(@Header("Authorization") String authHeader,
                                         @Query("posmAssetId") String posmAssetId);


    @POST("coolerRegistration/saveCoolerInformation")
    Call<SaveCoolerInfoResponse> saveCoolerInformation(@Header("Authorization")String token,@Body RequestBody file);
}
