package com.datn.smarttray.api;

import com.datn.smarttray.model.LoginRequest;
import com.datn.smarttray.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}