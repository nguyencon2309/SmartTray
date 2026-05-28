package com.datn.smarttray.api;

import com.datn.smarttray.model.Food;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FoodApiService {

    @GET("{collection}")
    Call<List<Food>> getFoods(
            @Path("collection") String collection
    );

    @GET("{collection}/{id}")
    Call<Food> getFoodById(
            @Path("collection") String collection,
            @Path("id") String id
    );
    @PUT("{collection}/{id}")
    Call<Void> updateFood(
            @Path("collection") String collection,
            @Path("id") String id,
            @Body Food food
    );



}

