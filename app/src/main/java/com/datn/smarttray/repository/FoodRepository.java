package com.datn.smarttray.repository;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.util.Log;
import android.widget.Toast;

import com.datn.smarttray.api.ApiClient;
import com.datn.smarttray.api.FoodApiService;
import com.datn.smarttray.enums.ModelType;
import com.datn.smarttray.manager.AppConfigManager;
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
    private static String getCollectionName() {

        if(AppConfigManager.getCurrentModel()
                == ModelType.FOOD101_MODEL){
            return "food101";
        }
        return "foods";
    }
    public static void getFoods(FoodCallback callback){
        if(isLoaded){
            callback.onSuccess(foodList);
            return;
        }
        Log.d("API_DEBUG", "CALL API START");
        api.getFoods(getCollectionName())
                .enqueue(new Callback<List<Food>>() {

                    @Override
                    public void onResponse(
                            Call<List<Food>> call,
                            Response<List<Food>> response
                    ) {
                        Log.d(
                                "API_DEBUG",
                                "CODE: " + response.code()
                        );

                        Log.d(
                                "API_DEBUG",
                                "BODY NULL: " + (response.body() == null)
                        );
                        if(response.isSuccessful()
                                && response.body() != null){
                            Log.d(
                                    "API_DEBUG",
                                    "SIZE: " + response.body().size()
                            );
                            isLoaded = true;
                            foodList.clear();
                            foodList.addAll(response.body());
                            callback.onSuccess(foodList);
                        }
                        else{
                            Log.e(
                                    "API_DEBUG",
                                    "RESPONSE FAIL"
                            );
                        }
                    }
                    @Override
                    public void onFailure(
                            Call<List<Food>> call,
                            Throwable t
                    ) {
                        Log.e(
                                "API_DEBUG",
                                "FAIL: " + t.getMessage()
                        );
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

        api.getFoodById(getCollectionName(),id)
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
                getCollectionName(),
                food.getId(),
                food
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {
                Log.d(
                        "API_UPDATE",
                        "CODE: " + response.code()
                );

                if(response.isSuccessful()){
                    Log.d(
                            "API_UPDATE",
                            "UPDATE SUCCESS"
                    );
                    for(int i = 0; i < foodList.size(); i++)
                    {
                        if(foodList.get(i).getId().equals(food.getId()))
                        {
                            foodList.set(i, food);
                            break;
                        }
                    }

                    isLoaded = true;
                    callback.onSuccess();
                }
                else{
                    try {

                        Log.e(
                                "API_UPDATE",
                                "ERROR BODY: "
                                        + response.errorBody().string()
                        );

                    } catch (Exception e) {

                        Log.e(
                                "API_UPDATE",
                                e.getMessage()
                        );
                    }

                    callback.onError(
                            "Response fail: "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t
            ) {
                Log.e(
                        "API_UPDATE",
                        "FAIL: " + t.toString()
                );
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


