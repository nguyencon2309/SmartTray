package com.datn.smarttray.api;


import android.content.Context;

import com.datn.smarttray.manager.SessionManager;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Lấy token từ SharedPref
        String token = SessionManager.getInstance(context).getToken();

        // Nếu không có token (ví dụ ở màn Đăng nhập/Đăng ký), cứ gửi request gốc đi
        if (token == null) {
            return chain.proceed(originalRequest);
        }

        // Nếu có token, tiến hành build thêm Header Authorization đúng chuẩn FastAPI cần
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}