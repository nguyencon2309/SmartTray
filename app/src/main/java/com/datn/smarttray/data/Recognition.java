package com.datn.smarttray.data;

import android.graphics.RectF;

public class Recognition {

    private String id;          // ID của đối tượng (0, 1, 2...)
    private String title;       // Tên nhãn (ví dụ: "apple", "tray")
    private Float confidence;   // Độ tự tin (0.0 -> 1.0)
    private RectF location;     // Tọa độ Bounding Box (Trái, Trên, Phải, Dưới)

    public Recognition(String id, String title, Float confidence, RectF location) {
        this.id = id;
        this.title = title;
        this.confidence = confidence;
        this.location = location;
    }

    // Các hàm Getter và Setter để lấy và cập nhật dữ liệu
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Float getConfidence() { return confidence; }

    public RectF getLocation() { return location; }
    public void setLocation(RectF location) { this.location = location; }

}
