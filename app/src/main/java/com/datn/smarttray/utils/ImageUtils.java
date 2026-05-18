package com.datn.smarttray.utils;

import android.graphics.Bitmap;

public class ImageUtils {
    public static Bitmap cropBitmapWithRect(Bitmap original, android.graphics.RectF rect) {
        // Đảm bảo tọa độ nằm trong phạm vi ảnh, tránh crash app
        int x = Math.max(0, (int) rect.left);
        int y = Math.max(0, (int) rect.top);
        int width = Math.min(original.getWidth() - x, (int) rect.width());
        int height = Math.min(original.getHeight() - y, (int) rect.height());

        if (width <= 0 || height <= 0) return null;

        // Tiến hành cắt ảnh
        return Bitmap.createBitmap(original, x, y, width, height);
    }
}
