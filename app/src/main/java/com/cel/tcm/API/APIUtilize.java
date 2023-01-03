package com.cel.tcm.API;

import static com.cel.tcm.Utils.Constants.BASE_URL;

public class APIUtilize {
    public static ApiService apiService() {
        return RetrofitClient.getRetrofit(BASE_URL).create(ApiService.class);
    }
}
