package com.datn.smarttray;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.smarttray.manager.FoodManager;
import com.datn.smarttray.manager.HistoryManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.History;
import com.datn.smarttray.repository.FoodRepository;

import java.io.File;
import java.util.List;

public class DetailFoodActivity extends AppCompatActivity {

    ImageView imgFood;

    TextView txtFoodName,txtFoodNameClass;
    EditText txtFoodPrice;

    EditText txtDescription;

    Button btnUpdate;
    boolean isUpdateMode = false;
    Food food;
    List<Food> foodList = FoodRepository.getCachedFoods();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);
        imgFood = findViewById(R.id.imgFood);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodNameClass = findViewById(R.id.txtFoodNameClass);
        txtFoodPrice = findViewById(R.id.txtFoodPrice);
        txtDescription = findViewById(R.id.txtDescription);
        btnUpdate = findViewById(R.id.btnUpdate);

        String foodId =
                getIntent().getStringExtra("food_id");
        food = FoodManager.getFoodById(foodId);

        if(food == null) {

            finish();

            return;
        }


        txtFoodName.setText(food.getNameViet());
        txtFoodNameClass.setText(food.getClassName());
        txtFoodPrice.setText(food.getPrice()+"");
        txtDescription.setText(food.getDescription());
        Glide.with(this)
                .load(food.getImageUrl())
                .placeholder(R.drawable.ic_baseline_fastfood_24)
                .error(R.drawable.ic_baseline_fastfood_24)
                .into(imgFood);
        btnUpdate.setOnClickListener(v -> {
            if(!isUpdateMode){
                updateFood();
            }
            else{
                saveUpdateFood();
            }
        });
    }
    public void updateFood(){
        isUpdateMode = true;
        btnUpdate.setText("SAVE");
        txtFoodPrice.setEnabled(isUpdateMode);
        txtDescription.setEnabled(isUpdateMode);
    }
    public void saveUpdateFood(){
        isUpdateMode = false;
        txtFoodPrice.setEnabled(isUpdateMode);
        txtDescription.setEnabled(isUpdateMode);

        String description =
                txtDescription.getText().toString().trim();

        int price =
                Integer.parseInt(
                        txtFoodPrice.getText().toString()
                );

        if(!description.equals(food.getDescription()) || (price!=food.getPrice())){
            callUpdateFood(description,price);
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
    public void callUpdateFood(String description,int price){
        food.setDescription(description);
        food.setPrice(price);

        /*FoodManager.updateFood(
                food,
                new FoodManager.FoodUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                DetailFoodActivity.this,
                                "Update thành công",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }
                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(
                                DetailFoodActivity.this,
                                error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );*/
        FoodRepository.updateFood(food, new FoodRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                for(int i = 0; i < foodList.size(); i++){
                    if(foodList.get(i).getId()
                            .equals(food.getId())){
                        foodList.set(i, food);
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
            }
        });
    }

}