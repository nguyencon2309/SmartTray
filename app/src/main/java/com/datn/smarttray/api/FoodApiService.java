package com.datn.smarttray.api;

import com.datn.smarttray.model.Food;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FoodApiService {

    @GET("foods")
    Call<List<Food>> getFoods();

    @GET("foods/{id}")
    Call<Food> getFoodById(
            @Path("id") String id
    );
    @PUT("foods/{id}")
    Call<Void> updateFood(
            @Path("id") String id,
            @Body Food food
    );



}

