package com.datn.smarttray.detector;

import android.graphics.Bitmap;

public interface FoodClassifier {

    String classifyFood(
            Bitmap bitmap
    );

    void setThreshold(float threshold);

    float getThreshold();

    void close();
}
