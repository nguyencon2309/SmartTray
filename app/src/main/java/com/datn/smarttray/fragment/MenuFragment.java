package com.datn.smarttray.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.smarttray.R;
import com.datn.smarttray.adapter.FoodAdapter;
import com.datn.smarttray.manager.FoodManager;
import com.datn.smarttray.model.Food;

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
    private void initViews(View view){
        recyclerFood = view.findViewById(R.id.recyclerFood);
    }
    private void initRecyclerView() {

        foodList = FoodManager.getFoodList();

        adapter = new FoodAdapter(foodList);

        recyclerFood.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerFood.setAdapter(adapter);
    }







}