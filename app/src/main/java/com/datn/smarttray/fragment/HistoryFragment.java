package com.datn.smarttray.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.smarttray.DetailHistoryActivity;
import com.datn.smarttray.R;
import com.datn.smarttray.adapter.FoodAdapter;
import com.datn.smarttray.adapter.HistoryAdapter;
import com.datn.smarttray.manager.FoodManager;
import com.datn.smarttray.manager.HistoryManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.History;

import java.io.File;
import java.io.Serializable;
import java.util.List;


public class HistoryFragment extends Fragment {


    RecyclerView recyclerHistory;
    List<History> historyList;
    HistoryAdapter adapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(
                R.layout.fragment_history,
                container,
                false
        );

        initViews(view);
        initRecyclerView();

        return view;
    }
    private void initViews(View view){
        recyclerHistory = view.findViewById(R.id.recyclerHistory);
    }
    private void initRecyclerView() {

        historyList = HistoryManager.getHistoryList();

        adapter = new HistoryAdapter(
                HistoryManager.getHistoryList(),
                new HistoryAdapter.OnHistoryClickListener() {

                    @Override
                    public void onHistoryClick(History history) {

                        openDetailHistory(history);
                    }

                    @Override
                    public void onDeleteClick(History history) {

                        deleteHistory(history);
                    }
                }
        );

        recyclerHistory.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerHistory.setAdapter(adapter);
    }
    private void openDetailHistory(History history) {
        Intent intent =
                new Intent(
                        requireContext(),
                        DetailHistoryActivity.class
                );
        intent.putExtra(
                "history_id",
                 history.getId()
        );
        startActivity(intent);
    }
    private void deleteHistory(History history) {
        if(history.getImagePredict() != null){
            File file =
                    new File(history.getImagePredict());
            if(file.exists()){
                file.delete();
            }
        }
        HistoryManager.deleteHistory(history);
        adapter.notifyDataSetChanged();
    }
}