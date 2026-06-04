package com.datn.smarttray;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.smarttray.manager.SessionManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.FoodShortReponse;
import com.datn.smarttray.repository.FoodRepository;
import com.google.gson.Gson;

import java.util.List;

public class DetailFoodActivity extends AppCompatActivity {

    ImageView imgFood;

    TextView txtFoodName,txtFoodNameClass,txtIngredients,txtRecipe, txtTips;


    EditText editCategory,editCalories,editPrice,editDescription;

    Button btnUpdate;
    boolean isUpdateMode = false;
    Food food;
    List<FoodShortReponse> foodList = FoodRepository.getCachedFoods();

//tools:context=".DetailFoodActivity"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);
        imgFood = findViewById(R.id.imgFood);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodNameClass = findViewById(R.id.txtFoodNameClass);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtRecipe = findViewById(R.id.txtRecipe);
        txtTips = findViewById(R.id.txtTips);
        editPrice = findViewById(R.id.edtPrice);
        editDescription = findViewById(R.id.edtDescription);
        editCategory = findViewById(R.id.edtCategory);
        editCalories = findViewById(R.id.edtCalories);
        btnUpdate = findViewById(R.id.btnUpdate);
        String userRole = SessionManager.getInstance(this).getUserRole();

        if (userRole != null && userRole.equalsIgnoreCase("admin")) {
            btnUpdate.setVisibility(View.VISIBLE);
        } else {
            btnUpdate.setVisibility(View.GONE);
        }
        String foodId =
                getIntent().getStringExtra("food_id");
        FoodRepository.getFoodById(foodId, new FoodRepository.SingleFoodCallback() {
            @Override
            public void onSuccess(Food food1) {
                food = food1;
                initDataToUI();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(DetailFoodActivity.this, "Không thể tải dữ liệu: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btnUpdate.setOnClickListener(v -> {
            if (food == null) return;
            if(!isUpdateMode){
                updateFood();
            }
            else{
                saveUpdateFood();
            }
        });
    }
    private void initDataToUI() {
        txtFoodName.setText(food.getNameViet());
        txtFoodNameClass.setText(food.getClassName());
        if(food.getIngredients() != null){
            txtIngredients.setText(
                    "• " + TextUtils.join("\n• ", food.getIngredients())
            );
        }
        if(food.getTips() != null){
            txtTips.setText(
                    "• " + TextUtils.join("\n• ", food.getTips())
            );
        }
        if(food.getRecipe() != null){
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < food.getRecipe().size(); i++) {
                sb.append(i + 1)
                        .append(". ")
                        .append(food.getRecipe().get(i))
                        .append("\n\n");
            }

            txtRecipe.setText(sb.toString());
        }


        editPrice.setText(food.getPrice()+"");
        editDescription.setText(food.getDescription());
        editCalories.setText(food.getCalories()+"");
        editCategory.setText(food.getCategory());
        Glide.with(this)
                .load(food.getImageUrl())
                .placeholder(R.drawable.ic_baseline_fastfood_24)
                .error(R.drawable.ic_baseline_fastfood_24)
                .into(imgFood);
    }
    public void updateFood(){
        isUpdateMode = true;
        btnUpdate.setText("SAVE");
        editPrice.setEnabled(isUpdateMode);
        editDescription.setEnabled(isUpdateMode);
        editCalories.setEnabled(isUpdateMode);
        editCategory.setEnabled(isUpdateMode);
    }
    public void saveUpdateFood(){
        isUpdateMode = false;
        editPrice.setEnabled(isUpdateMode);
        editDescription.setEnabled(isUpdateMode);
        editCalories.setEnabled(isUpdateMode);
        editCategory.setEnabled(isUpdateMode);

        String description =
                editDescription.getText().toString().trim();
        String category =
                editCategory.getText().toString().trim();

        int price =
                Integer.parseInt(
                        editPrice.getText().toString()
                );
        int calories =
                Integer.parseInt(
                        editCalories.getText().toString()
                );

        if(!description.equals(food.getDescription()) || (price!=food.getPrice()) || (calories!=food.getCalories()) || !category.equals(food.getCategory()) ){
            callUpdateFood(description,price,calories,category);
        }
        else{
            Toast.makeText(
                    DetailFoodActivity.this,
                    "Không có gì thay đổi",
                    Toast.LENGTH_SHORT
            ).show();
        }
        btnUpdate.setText("UPDATE");

    }
    public void callUpdateFood(String description,int price,int calories,String category){
        food.setDescription(description);
        food.setPrice(price);
        food.setCalories(calories);
        food.setCategory(category);
        FoodRepository.updateFood(food, new FoodRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                for(int i = 0; i < foodList.size(); i++){
                    if(foodList.get(i).getId()
                            .equals(food.getId())){
                        foodList.get(i).setPrice(food.getPrice());
                        break;
                    }
                }
                Toast.makeText(
                        DetailFoodActivity.this,
                        "Update thành công",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(
                        DetailFoodActivity.this,
                        "Error "+ error,
                        Toast.LENGTH_SHORT
                ).show();
                Log.d(
                        "UI_ERR",
                        error
                );
            }
        });
    }

}