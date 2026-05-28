package com.datn.smarttray.manager;

import android.content.Context;

import com.datn.smarttray.detector.EfficientNetClassifier;
import com.datn.smarttray.detector.Food101ModelClassifier;
import com.datn.smarttray.detector.FoodClassifier;
import com.datn.smarttray.detector.YOLOv11Detector;
import com.datn.smarttray.enums.ModelType;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.utils.LabelUtils;

import java.io.IOException;
import java.util.List;

public class ModelManager {

    private static YOLOv11Detector yoloDetector;
    private static FoodClassifier classifier;

    public static void initYolo(Context context) {
        if (yoloDetector == null) {
            try {
                yoloDetector =
                        new YOLOv11Detector(
                                context.getAssets(),
                                "best_float16.tflite"
                        );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void initClassifier(Context context) {
        try {
            if(classifier!=null){
                classifier.close();
                classifier=null;
            }
            if(AppConfigManager.getCurrentModel()
                    == ModelType.FOOD40_MODEL){
                classifier =
                        new EfficientNetClassifier(
                                context,
                                "efficientnet_classifier.tflite",
                                40
                        );
            }else{
                classifier =
                        new Food101ModelClassifier(
                                context,
                                "model_select_ops.tflite",
                                101
                        );
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public interface ReloadCallback {

        void onSuccess();

        void onError(String error);
    }

    public static void reloadAll(
            Context context,
            ReloadCallback callback
    ) {
        FoodManager.refreshFoods(
                new FoodManager.FoodLoadCallback() {

                    @Override
                    public void onLoaded(
                            List<Food> foods
                    ) {
                        initClassifier(context);
                        callback.onSuccess();
                    }
                    @Override
                    public void onError(
                            String error
                    ) {
                        callback.onError(error);
                    }
                }
        );
    }

    public static YOLOv11Detector getYoloDetector() {
        return yoloDetector;
    }

    public static FoodClassifier getClassifier() {
        return classifier;
    }
}
