package com.datn.smarttray;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.datn.smarttray.manager.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 🔑 Kiểm tra xem Token đã tồn tại trong bộ nhớ máy chưa
                String token = SessionManager.getInstance(SplashActivity.this).getToken();

                Intent intent;
                if (token != null && !token.isEmpty()) {
                    // Đã đăng nhập trước đó -> Vào thẳng màn hình chính MainActivity
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Chưa có token -> Bắt buộc sang màn hình LoginActivity
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish(); // Đóng hoàn toàn SplashActivity để không thể nhấn nút Back quay lại đây
            }
        }, 1500);
    }
}