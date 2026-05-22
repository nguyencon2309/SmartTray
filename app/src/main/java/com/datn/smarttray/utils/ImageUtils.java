package com.datn.smarttray.utils;

import android.graphics.Bitmap;

public class ImageUtils {
    public static Bitmap cropBitmapWithRect(Bitmap original, android.graphics.RectF rect, int xx) {
        // Đảm bảo tọa độ nằm trong phạm vi ảnh, tránh crash app
        int x = Math.max(0, (int) rect.left-xx);
        int y = Math.max(0, (int) rect.top-xx);
        int width = Math.min(original.getWidth() - x+xx, (int) rect.width());
        int height = Math.min(original.getHeight() - y+xx, (int) rect.height());

        if (width <= 0 || height <= 0) return null;

        // Tiến hành cắt ảnh
        return Bitmap.createBitmap(original, x, y, width, height);
    }
}
