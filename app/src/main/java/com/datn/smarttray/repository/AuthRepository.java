package com.datn.smarttray.repository;
import android.util.Log;

import com.datn.smarttray.api.ApiClient;
import com.datn.smarttray.api.AuthApiService;
import com.datn.smarttray.model.LoginRequest;
import com.datn.smarttray.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {


    private static final AuthApiService api = ApiClient.getAuthApi();

    public static void login(LoginRequest loginRequest, AuthCallback callback) {
        Log.d("AUTH_API", "START LOGIN API");

        api.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("AUTH_API", "CODE: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AUTH_API", "LOGIN SUCCESS");
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Tài khoản hoặc mật khẩu không chính xác";
                    try {
                        if (response.errorBody() != null) {
                            Log.e("AUTH_API", "ERROR BODY: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("AUTH_API", e.getMessage());
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("AUTH_API", "FAIL: " + t.getMessage());
                callback.onError("Lỗi kết nối Server: " + t.getMessage());
            }
        });
    }


    public interface AuthCallback {
        void onSuccess(LoginResponse loginResponse);
        void onError(String error);
    }
}