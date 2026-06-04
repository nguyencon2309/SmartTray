package com.datn.smarttray.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.datn.smarttray.DetailFoodActivity;
import com.datn.smarttray.R;
import com.datn.smarttray.adapter.FoodAdapter;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.FoodShortReponse;
import com.datn.smarttray.repository.FoodRepository;

import java.util.List;


public class MenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    RecyclerView recyclerFood;
    List<FoodShortReponse> foodList;
    FoodAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_menu,
                container,
                false
        );

        initViews(view);
        initRecyclerView();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }
    private void initViews(View view){
        recyclerFood = view.findViewById(R.id.recyclerFood);
    }
    private void initRecyclerView() {

        foodList = FoodRepository.getCachedFoods();
        adapter = new FoodAdapter(
                foodList,
                new FoodAdapter.OnFoodClickListener() {
                    @Override
                    public void onFoodClick(FoodShortReponse food) {
                        openDetailFood(food);
                    }
                }
        );

        recyclerFood.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerFood.setAdapter(adapter);
    }
    public void openDetailFood(FoodShortReponse food){
        if (food == null) {
            Toast.makeText(requireContext(), "Dữ liệu món ăn bị rỗng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🌟 1. Log thử xem ID của món ăn lấy từ Backend về có bị null hoặc rỗng không
        Log.d("DEBUG_DETAIL", "Click vào món: " + food.getNameViet() + " | ID nhận được = " + food.getId());

        // 🌟 2. Đặt bảo hiểm: Nếu ID bị null, chặn lại luôn không cho mở màn hình Detail để tránh gây sập Retrofit
        if (food.getId() == null || food.getId().isEmpty()) {
            Toast.makeText(requireContext(), "Món ăn này không có ID hợp lệ trên database!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent =
                new Intent(
                        requireContext(),
                        DetailFoodActivity.class
                );
        intent.putExtra(
                "food_id",
                food.getId()
        );
        startActivity(intent);
    }

}