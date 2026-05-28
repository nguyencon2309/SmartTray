package com.datn.smarttray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.datn.smarttray.fragment.HistoryFragment;
import com.datn.smarttray.fragment.HomeFragment;
import com.datn.smarttray.fragment.MenuFragment;
import com.datn.smarttray.fragment.ScanFragment;

import com.datn.smarttray.manager.ModelManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.History;
import com.datn.smarttray.repository.FoodRepository;
import com.datn.smarttray.repository.HistoryRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;



import java.util.List;

public class MainActivity extends AppCompatActivity {



    BottomNavigationView bottomNav;
    private static boolean appReady = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNav = findViewById(R.id.bottom_navigation);

        loadFragment(new HomeFragment());


        bottomNav.setOnItemSelectedListener(item -> {
            if (!appReady) {
                Toast.makeText(
                        this,
                        "Đang tải dữ liệu...",
                        Toast.LENGTH_SHORT
                    ).show();
                return false;
            }

            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_scan) {
                fragment = new ScanFragment();
            } else if (item.getItemId() == R.id.nav_menu) {
                fragment = new MenuFragment();
            } else if (item.getItemId() == R.id.nav_history) {
                fragment = new HistoryFragment();
            }
            /*if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_history) {
                fragment = new HistoryFragment();
            }*/
            return loadFragment(fragment);
        });
        preload();
        //call api food





    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }

        return false;
    }
    private void preload(){
        FoodRepository.getFoods(
                new FoodRepository.FoodCallback() {
                    @Override
                    public void onSuccess(
                            List<Food> foods
                    ) {
                        Log.d(
                                "MAIN_DEBUG",
                                "LOAD FOOD SUCCESS: "
                                        + foods.size()
                        );
                        HistoryRepository.getHistorys(new HistoryRepository.HistoryCallback() {
                            @Override
                            public void onSuccess(List<History> historys) {
                                new Thread(() -> {
                                    ModelManager.initYolo(MainActivity.this);
                                    ModelManager.initClassifier(MainActivity.this);
                                    runOnUiThread(() -> {
                                        appReady = true;
                                        loadFragment(
                                                new HomeFragment()
                                        );
                                    });
                                }).start();
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });


                    }
                    @Override
                    public void onError(String error) {
                        Log.e(
                                "MAIN_DEBUG",
                                "LOAD FOOD ERROR: "
                                        + error
                        );
                    }
                }
        );
    }
    private void preload2(){
        FoodRepository.getFoods(
                new FoodRepository.FoodCallback() {
                    @Override
                    public void onSuccess(
                            List<Food> foods
                    ) {
                        Log.d(
                                "MAIN_DEBUG",
                                "LOAD FOOD SUCCESS: "
                                        + foods.size()
                        );
                        /*HistoryRepository.getHistorys(new HistoryRepository.HistoryCallback() {
                            @Override
                            public void onSuccess(List<History> historys) {*/
                        new Thread(() -> {
                                    /*ModelManager.initYolo(MainActivity.this);
                                    ModelManager.initClassifier(MainActivity.this);*/
                            runOnUiThread(() -> {
                                appReady = true;
                                loadFragment(
                                        new HomeFragment()
                                );
                            });
                        }).start();/*
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });

                         */
                    }
                    @Override
                    public void onError(String error) {
                        Log.e(
                                "MAIN_DEBUG",
                                "LOAD FOOD ERROR: "
                                        + error
                        );
                    }
                }
        );
    }
    private void preload1(){
        HistoryRepository.getHistorys(new HistoryRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<History> historys) {
                Log.d(
                        "MAIN_DEBUG",
                        "LOAD FOOD SUCCESS: "
                                + historys.size()
                );
                new Thread(() -> {
                                    /*ModelManager.initYolo(MainActivity.this);
                                    ModelManager.initClassifier(MainActivity.this);*/
                    runOnUiThread(() -> {
                        appReady = true;
                        loadFragment(
                                new HomeFragment()
                        );
                    });
                }).start();
                            }

                @Override
                public void onError(String error) {
                                Log.e(
                                        "MAIN_DEBUG",
                                        "LOAD History ERROR: "
                                                + error
                                );

                            }
                });
    }
    @Override
    protected void onDestroy() {
        //yolOv11Detector.close();
        super.onDestroy();

    }
}

