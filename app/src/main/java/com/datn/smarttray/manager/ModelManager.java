package com.datn.smarttray.manager;

import android.content.Context;

import com.datn.smarttray.detector.EfficientNetClassifier;
import com.datn.smarttray.detector.YOLOv11Detector;
import com.datn.smarttray.utils.LabelUtils;

import java.io.IOException;
import java.util.List;

public class ModelManager {

    private static YOLOv11Detector yoloDetector;
    private static EfficientNetClassifier classifier;

    public static void init(Context context) {

        if (yoloDetector == null) {

            try {

                List<String> labels =
                        LabelUtils.loadLabelList(
                                context,
                                "labels_viet.txt"
                        );

                yoloDetector =
                        new YOLOv11Detector(
                                context.getAssets(),
                                "best_float16.tflite"
                        );

                classifier =
                        new EfficientNetClassifier(
                                context,
                                "efficientnet_classifier.tflite"
                        );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static YOLOv11Detector getYoloDetector() {
        return yoloDetector;
    }

    public static EfficientNetClassifier getClassifier() {
        return classifier;
    }
}
