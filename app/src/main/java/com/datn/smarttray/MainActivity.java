package com.datn.smarttray;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.datn.smarttray.adapter.FoodAdapter;
import com.datn.smarttray.fragment.HistoryFragment;
import com.datn.smarttray.fragment.HomeFragment;
import com.datn.smarttray.fragment.MenuFragment;
import com.datn.smarttray.fragment.ScanFragment;

import com.datn.smarttray.manager.FoodManager;
import com.datn.smarttray.manager.ModelManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.utils.LabelUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {



    BottomNavigationView bottomNav;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNav = findViewById(R.id.bottom_navigation);

        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {

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

            return loadFragment(fragment);
        });



        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:137213055582:android:fe8956bd0751427b5b9acf")
                    .setApiKey("AIzaSyCDMD2iiIg5gRfkw-qEkhrG4ZNTJDNJVVk")
                    .setDatabaseUrl("https://smarttray-95dc4-default-rtdb.firebaseio.com")
                    .setProjectId("smarttray-95dc4")
                    .build();

            FirebaseApp.initializeApp(this, options);
        }
        FoodManager.loadFoods(
                new FoodManager.FoodLoadCallback() {

                    @Override
                    public void onLoaded(
                            List<Food> foods
                    ) {

                        Log.d(
                                "FOOD_MANAGER",
                                "Loaded: " + foods.size()
                        );
                    }

                    @Override
                    public void onError(String error) {

                        Log.e(
                                "FOOD_MANAGER",
                                error
                        );
                    }
                }
        );

        ModelManager.init(this);
        //initFirebase();

    }
    public void initFirebase(){
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:137213055582:android:fe8956bd0751427b5b9acf")
                    .setApiKey("AIzaSyCDMD2iiIg5gRfkw-qEkhrG4ZNTJDNJVVk")
                    .setDatabaseUrl("https://smarttray-95dc4-default-rtdb.firebaseio.com")
                    .setProjectId("smarttray-95dc4")
                    .build();

            FirebaseApp.initializeApp(this, options);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference("food");




        /*
        List<String> danhSachMonAn = LabelUtils.loadLabelList(this,"labels.txt");
        List<String> danhSachMonAn_Viet = LabelUtils.loadLabelList(this,"labels_viet.txt");
        List<String> listImageUrl = LabelUtils.loadLabelList(this,"image.txt");
        for(int i=0;i<40;i++){
            String foodId = mDatabase.push().getKey();
            Random random = new Random();
            int price = (random.nextInt(6)+5)*1000;
            Food food = new Food(foodId,danhSachMonAn.get(i),danhSachMonAn_Viet.get(i),price,"chua co mo ta",listImageUrl.get(i));
            mDatabase.child(foodId).setValue(food);
        }*/

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
    @Override
    protected void onDestroy() {
        //yolOv11Detector.close();
        super.onDestroy();

    }
}

