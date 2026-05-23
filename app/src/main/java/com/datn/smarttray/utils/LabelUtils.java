package com.datn.smarttray.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LabelUtils {

    public static List<String> loadLabelList(
            Context context,
            String fileName
    ) {

        List<String> labels = new ArrayList<>();

        try {

            // mở file từ assets
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            context.getAssets().open(fileName),
                            "UTF-8"
                    )
            );

            String line;

            while ((line = reader.readLine()) != null) {

                if (!line.trim().isEmpty()) {
                    labels.add(line.trim());
                }
            }

            reader.close();

            Log.d(
                    "LABEL_UTILS",
                    "Đã load thành công "
                            + labels.size()
                            + " labels"
            );

        } catch (IOException e) {

            Log.e(
                    "LABEL_UTILS",
                    "Lỗi đọc labels: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return labels;
    }
}