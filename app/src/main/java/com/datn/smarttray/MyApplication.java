package com.datn.smarttray;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        // Hệ thống Android vừa kích hoạt App là dòng này chạy ngay lập tức
        MyApplication.context = getApplicationContext();
    }

    // Bất kỳ class nào trong App cũng có thể gọi hàm này để lấy Context
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
