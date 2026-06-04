package com.datn.smarttray;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.datn.smarttray.fragment.HistoryFragment;
import com.datn.smarttray.fragment.HomeFragment;
import com.datn.smarttray.fragment.MenuFragment;
import com.datn.smarttray.fragment.ScanFragment;

import com.datn.smarttray.manager.ModelManager;
import com.datn.smarttray.manager.SessionManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.FoodShortReponse;
import com.datn.smarttray.model.History;
import com.datn.smarttray.model.HistoryShortReponse;
import com.datn.smarttray.repository.FoodRepository;
import com.datn.smarttray.repository.HistoryRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;



import java.util.List;

public class MainActivity extends AppCompatActivity {



    BottomNavigationView bottomNav;
    ImageView imageView;
    private static boolean appReady = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNav = findViewById(R.id.bottom_navigation);
        imageView = findViewById(R.id.imgProfile);

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
        imageView.setOnClickListener(v->showProfileDialog());
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
                            List<FoodShortReponse> foods
                    ) {
                        Log.d(
                                "MAIN_DEBUG",
                                "LOAD FOOD SUCCESS: "
                                        + foods.size()
                        );
                        HistoryRepository.getHistorys(new HistoryRepository.HistoryCallback() {
                            @Override
                            public void onSuccess(List<HistoryShortReponse> historys) {
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
    /*
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
                        HistoryRepository.getHistorys(new HistoryRepository.HistoryCallback() {
                            @Override
                            public void onSuccess(List<History> historys) {
                        new Thread(() -> {
                                    /*ModelManager.initYolo(MainActivity.this);
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
    }*/
    /*
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
                                Log.e(
                                        "MAIN_DEBUG",
                                        "LOAD History ERROR: "
                                                + error
                                );

                            }
                });
    }*/

    public void showProfileDialog(){
        String userRole = SessionManager.getInstance(this).getUserRole();
        String userName = SessionManager.getInstance(this).getUserName();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account Info");
        builder.setMessage("Username :"+ userName+ "\n\nRole :" + userRole.toUpperCase() + "\n\nBạn có muốn đăng xuất khỏi hệ thống?");

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 1. Gọi hàm xóa sạch dữ liệu trong bộ nhớ máy
                SessionManager.getInstance(MainActivity.this).clear();

                Toast.makeText(MainActivity.this, "Logout success", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onDestroy() {
        //yolOv11Detector.close();
        super.onDestroy();

    }
}

