package com.datn.smarttray;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.datn.smarttray.manager.SessionManager;
import com.datn.smarttray.model.LoginRequest;
import com.datn.smarttray.model.LoginResponse;
import com.datn.smarttray.repository.AuthRepository;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> handleLogin());
    }
    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo model chứa dữ liệu login đóng gói
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Khóa UI tạm thời để tránh bấm lặp lại
        btnLogin.setEnabled(false);
        btnLogin.setText("ĐANG XỬ LÝ...");

        // 🌟 Gọi qua tầng Repository tương tự như cách tương tác với Food
        AuthRepository.login(loginRequest, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                // Mở khóa nút bấm
                btnLogin.setEnabled(true);
                btnLogin.setText("ĐANG NHẬP");

                // Lưu access_token nhận diện từ FastAPI vào bộ nhớ máy qua SharedPrefManager
                String token = loginResponse.getAccessToken();
                SessionManager.getInstance(LoginActivity.this).saveToken(token);

                String role = loginResponse.getRole(); // Ví dụ: "admin"
                SessionManager.getInstance(LoginActivity.this).saveUserRole(role);

                String username = loginResponse.getUserName(); // Ví dụ: "admin"
                SessionManager.getInstance(LoginActivity.this).saveUserName(username);

                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // Chuyển màn hình vào MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Đóng LoginActivity để nút Back không quay lại được đây
            }

            @Override
            public void onError(String error) {
                // Mở khóa nút bấm khi thất bại để người dùng sửa thông tin nhập lại
                btnLogin.setEnabled(true);
                btnLogin.setText("ĐĂNG NHẬP");

                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}