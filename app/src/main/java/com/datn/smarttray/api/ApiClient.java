package com.datn.smarttray.api;

import android.content.Context;

public class ApiClient {

    public static FoodApiService getFoodApi() {

        return RetrofitClient
                .getClient()
                .create(FoodApiService.class);
    }
    public static HistoryApiService getHistoryApi() {
        return RetrofitClient
                .getClient()
                .create(HistoryApiService.class);
    }
    public static AuthApiService getAuthApi(){
        return RetrofitClient
                .getClient()
                .create(AuthApiService.class);
    }

}
