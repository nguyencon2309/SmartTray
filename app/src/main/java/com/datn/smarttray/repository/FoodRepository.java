package com.datn.smarttray.repository;

import com.datn.smarttray.api.ApiClient;
import com.datn.smarttray.api.FoodApiService;
import com.datn.smarttray.model.Food;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodRepository {
    private static final FoodApiService api =ApiClient.getFoodApi();
    private static final List<Food> foodList = new ArrayList<>();
    private static boolean isLoaded = false;
    public static void getFoods(FoodCallback callback){
        if(isLoaded){
            callback.onSuccess(foodList);
            return;
        }
        api.getFoods()
                .enqueue(new Callback<List<Food>>() {

                    @Override
                    public void onResponse(
                            Call<List<Food>> call,
                            Response<List<Food>> response
                    ) {
                        if(response.isSuccessful()
                                && response.body() != null){
                            foodList.clear();
                            foodList.addAll(response.body());
                            callback.onSuccess(foodList);
                            isLoaded = true;
                        }
                    }
                    @Override
                    public void onFailure(
                            Call<List<Food>> call,
                            Throwable t
                    ) {
                        callback.onError(
                                t.getMessage()
                        );
                    }
                });
    }
    public static void refreshFoods( FoodCallback callback )
    {   isLoaded = false;
        getFoods(callback);
    }
    public static List<Food> getCachedFoods()
    {
        return foodList;
    }
    public static Food getFoodLocalById( String id )
    {
        for(Food food : foodList)
            { if(food.getId().equals(id))
                { return food; }
            }
        return null;
    }

    public static void getFoodById(
            String id,
            SingleFoodCallback callback
    ){

        api.getFoodById(id)
                .enqueue(new Callback<Food>() {

                    @Override
                    public void onResponse(
                            Call<Food> call,
                            Response<Food> response
                    ) {

                        if(response.isSuccessful()
                                && response.body() != null){

                            callback.onSuccess(
                                    response.body()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Food> call,
                            Throwable t
                    ) {

                        callback.onError(
                                t.getMessage()
                        );
                    }
                });
    }


    public static void updateFood(
            Food food,
            SimpleCallback callback
    ){

        api.updateFood(
                food.getId(),
                food
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {
                if(response.isSuccessful()){
                    for(int i = 0; i < foodList.size(); i++)
                    {
                        if(foodList.get(i).getId().equals(food.getId()))
                        {
                            foodList.set(i, food);
                            break;
                        }
                    }
                    isLoaded = false;
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t
            ) {

                callback.onError(
                        t.getMessage()
                );
            }
        });
    }

    public interface FoodCallback {

        void onSuccess(List<Food> foods);

        void onError(String error);
    }

    public interface SingleFoodCallback {

        void onSuccess(Food food);

        void onError(String error);
    }

    public interface SimpleCallback {

        void onSuccess();

        void onError(String error);
    }


}


