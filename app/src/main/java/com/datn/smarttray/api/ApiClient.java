package com.datn.smarttray.api;

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

}
