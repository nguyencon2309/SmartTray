package com.datn.smarttray.api;

import android.content.Context;

import com.datn.smarttray.MyApplication;
import com.datn.smarttray.manager.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.Request;
import okhttp3.Response;

public class RetrofitClient {
    private static final String BASE_URL =
            "https://fastapi-1-pkm4.onrender.com/";

    private static Retrofit retrofit;
    public static Retrofit getClient() {
        if (retrofit == null) {
            //tang timeout đối vơi
            Context context = MyApplication.getAppContext();
            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(context) {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request original = chain.request();

                                    // Lấy token từ SharedPref tập trung
                                    String token = SessionManager.getInstance(context).getToken();

                                    if (token != null && !token.isEmpty()) {
                                        Request request = original.newBuilder()
                                                .header("Authorization", "Bearer " + token)
                                                .build();
                                        return chain.proceed(request);
                                    }
                                    return chain.proceed(original);
                                }
                            })
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
//            retrofit =
//                    new Retrofit.Builder()
//                            .baseUrl(BASE_URL)
//                            .addConverterFactory(
//                                    GsonConverterFactory.create()
//                            )
//                            .build();


        }
        return retrofit;
    }

}

