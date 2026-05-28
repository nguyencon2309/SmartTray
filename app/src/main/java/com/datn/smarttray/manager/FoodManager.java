package com.datn.smarttray.manager;


import androidx.annotation.NonNull;

import com.datn.smarttray.enums.ModelType;
import com.datn.smarttray.model.Food;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FoodManager {
    private static final List<Food> foodList = new ArrayList<>();
    private static boolean isLoaded = false;
    public interface FoodUpdateCallback{
        void onSuccess();
        void onFailed(String error);
    }
    public static void loadFoods(
            FoodLoadCallback callback
    ) {
        String tableName;
        if(AppConfigManager.getCurrentModel()
                == ModelType.FOOD40_MODEL){
            tableName = "food";
        }else{
            tableName = "food_101";
        }
        if (isLoaded) {
            callback.onLoaded(foodList);
            return;
        }
        DatabaseReference foodRef =
                FirebaseDatabase
                        .getInstance()
                        .getReference(tableName);
        foodRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {
                        foodList.clear();
                        for (DataSnapshot data :
                                snapshot.getChildren()) {
                            Food food =
                                    data.getValue(Food.class);
                            if (food != null) {
                                foodList.add(food);
                            }
                        }
                        isLoaded = true;
                        callback.onLoaded(foodList);
                    }
                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {
                        callback.onError(
                                error.getMessage()
                        );
                    }
                }
        );
    }
    public static List<Food> getFoodList() {
        return foodList;
    }
    public static int getSizeFoodList (){
        return foodList.size();
    }
    public static Food getFoodByName(
            String name
    ) {

        for (Food food : foodList) {
            if (food.getNameViet()
                    .equalsIgnoreCase(name)) {
                return food;
            }
        }
        return null;
    }

    public static Food getFoodById(String id)
    {
        for(Food food : foodList){
            if(food.getId().equals(id)){
                return food;
            }
        }
        return null;
    }
    public static void refreshFoods(
            FoodLoadCallback callback
    ) {

        String tableName;
        if(AppConfigManager.getCurrentModel()
                == ModelType.FOOD40_MODEL){
            tableName = "food";
        }else{
            tableName = "food_101";
        }
        DatabaseReference foodRef =
                FirebaseDatabase
                        .getInstance()
                        .getReference(tableName);
        foodRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {
                        foodList.clear();
                        for (DataSnapshot data :
                                snapshot.getChildren()) {
                            Food food =
                                    data.getValue(Food.class);
                            if (food != null) {
                                foodList.add(food);
                            }
                        }
                        isLoaded = true;
                        callback.onLoaded(foodList);
                    }
                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {
                        callback.onError(
                                error.getMessage()
                        );
                    }
                }
        );
    }


    public interface FoodLoadCallback {
        void onLoaded(List<Food> foods);
        void onError(String error);
    }

    public static void updateFood(
            Food food,
            FoodUpdateCallback callback
    ){
        String tableName;
        if(AppConfigManager.getCurrentModel()
                == ModelType.FOOD40_MODEL){
            tableName = "food";
        }else{
            tableName = "food_101";
        }
        FirebaseDatabase
                .getInstance()
                .getReference(tableName)
                .child(food.getId())
                .setValue(food)
                .addOnSuccessListener(unused -> {
                    // update local list
                    for(int i = 0; i < foodList.size(); i++){
                        if(foodList.get(i).getId()
                                .equals(food.getId())){
                            foodList.set(i, food);
                            break;
                        }
                    }
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailed(e.getMessage());
                });
    }
}
