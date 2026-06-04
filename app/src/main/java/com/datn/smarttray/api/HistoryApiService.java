package com.datn.smarttray.api;

import com.datn.smarttray.model.History;
import com.datn.smarttray.model.HistoryShortReponse;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface HistoryApiService {
    @GET("history/all")
    Call<List<HistoryShortReponse>> getHistorys();

    @GET("history/{id}")
    Call<History> getHistoryById(
            @Path("id") String id
    );
    @Multipart
    @POST("history/")
    Call<Void> addHistory(
            @Part MultipartBody.Part file,
            @Part("data") RequestBody data
    );



    @DELETE("history/{id}")
    Call<Void> deleteHistory(
            @Path("id") String id
    );
}

