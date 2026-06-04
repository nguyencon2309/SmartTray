package com.datn.smarttray.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetLocalTime {
    public static String getLocalTime(long timestamp){
        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                );

        String time =
                sdf.format(
                        new Date(timestamp)
                );
        return time;
    }
}
