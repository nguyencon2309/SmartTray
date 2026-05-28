package com.datn.smarttray.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.smarttray.DetailFoodActivity;
import com.datn.smarttray.R;
import com.datn.smarttray.adapter.FoodAdapter;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.repository.FoodRepository;

import java.util.List;


public class MenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    RecyclerView recyclerFood;
    List<Food> foodList;
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
                    public void onFoodClick(Food food) {
                        openDetailFood(food);
                    }
                }
        );

        recyclerFood.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerFood.setAdapter(adapter);
    }
    public void openDetailFood(Food food){
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