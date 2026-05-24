package com.datn.smarttray.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

public class ImageStorageUtil {

    public static String saveBitmap(
            Context context,
            Bitmap bitmap
    ) {

        File directory =
                new File(
                        context.getFilesDir(),
                        "history_images"
                );

        if(!directory.exists()){

            directory.mkdirs();

        }

        String fileName =
                "IMG_" +
                        System.currentTimeMillis() +
                        ".jpg";

        File file =
                new File(directory, fileName);

        try {

            FileOutputStream fos =
                    new FileOutputStream(file);

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    70,
                    fos
            );

            fos.flush();

            fos.close();

            return file.getAbsolutePath();

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}
