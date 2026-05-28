package com.datn.smarttray.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    public static File bitmapToFile(
            Context context,
            Bitmap bitmap
    ) throws IOException {

        File file =
                new File(
                        context.getCacheDir(),
                        "upload.jpg"
                );

        FileOutputStream fos =
                new FileOutputStream(file);

        bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                80,
                fos
        );

        fos.flush();
        fos.close();

        return file;
    }
}

