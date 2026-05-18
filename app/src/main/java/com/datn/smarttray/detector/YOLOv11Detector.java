package com.datn.smarttray.detector;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;

import com.datn.smarttray.data.Recognition;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class YOLOv11Detector {
    private Interpreter tflite;
    private final int INPUT_SIZE = 640;
    private final float CONFIDENCE_THRESHOLD = 0.4f;

    public YOLOv11Detector(AssetManager assetManager, String modelPath) throws IOException{
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);
        tflite = new Interpreter(loadModelFile(assetManager,modelPath), options);
    }
    private java.nio.MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
/*
    public List<Recognition> detectObjects(Bitmap bitmapGoc) {

        List<Recognition> danhSachKetQua = new ArrayList<>();

        // 1. Chuẩn hóa kích thước ảnh đầu vào (640x640)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapGoc, INPUT_SIZE, INPUT_SIZE, false);

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
        inputBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        inputBuffer.rewind();
        for (int pixelValue : intValues) {
            inputBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
            inputBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
            inputBuffer.putFloat((pixelValue & 0xFF) / 255.0f);
        }

        // 2. Mảng chứa output theo đúng cấu trúc Netron của bạn: 1 x 5 x 8400
        float[][][] outputBuffer = new float[1][5][8400];

        // 3. Chạy mô hình
        tflite.run(inputBuffer, outputBuffer);

        // 4. Giải mã dữ liệu đầu ra và map tọa độ về kích thước ảnh gốc
        float scaleX = (float) bitmapGoc.getWidth() ;
        float scaleY = (float) bitmapGoc.getHeight() ;
        int count = 0;

        for (int i = 0; i < 8400; i++) {
            float score = outputBuffer[0][4][i]; // Điểm tự tin của class "food"

            if (score > CONFIDENCE_THRESHOLD) {
                float cx = outputBuffer[0][0][i];
                float cy = outputBuffer[0][1][i];
                float w  = outputBuffer[0][2][i];
                float h  = outputBuffer[0][3][i];

                // Chuyển đổi sang tọa độ pixel góc
                float left   = (cx - w / 2) * scaleX;
                float top    = (cy - h / 2) * scaleY;
                float right  = (cx + w / 2) * scaleX;
                float bottom = (cy + h / 2) * scaleY;

                // Giới hạn tọa độ không vượt quá biên của ảnh gốc
                left = Math.max(0, left);
                top = Math.max(0, top);
                right = Math.min(bitmapGoc.getWidth(), right);
                bottom = Math.min(bitmapGoc.getHeight(), bottom);

                RectF location = new RectF(left, top, right, bottom);

                // Thêm vào danh sách kết quả trả về
                danhSachKetQua.add(new Recognition(String.valueOf(count), "food", score, location));
                count++;
            }
        }

        return danhSachKetQua;
    }*/
public List<Recognition> detectObjects(Bitmap bitmapGoc) {

    List<Recognition> danhSachKetQua = new ArrayList<>();

    // 1. Chuẩn hóa kích thước ảnh đầu vào (640x640) - GIỮ NGUYÊN
    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapGoc, INPUT_SIZE, INPUT_SIZE, false);

    ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
    inputBuffer.order(ByteOrder.nativeOrder());
    int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
    resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

    inputBuffer.rewind();
    for (int pixelValue : intValues) {
        inputBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
        inputBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
        inputBuffer.putFloat((pixelValue & 0xFF) / 255.0f);
    }

    // 2. Mảng chứa output: 1 x 5 x 8400 - GIỮ NGUYÊN
    float[][][] outputBuffer = new float[1][5][8400];

    // 3. Chạy mô hình - GIỮ NGUYÊN
    tflite.run(inputBuffer, outputBuffer);

    // 4. SỬA LẠI: Tính toán tỉ lệ scale dựa trên KÍCH THƯỚC THỰC TẾ của ảnh gốc
    // Vì tọa độ thô thu được đang ở dạng tỷ lệ phần trăm (Normalized)
    float scaleX = (float) bitmapGoc.getWidth();
    float scaleY = (float) bitmapGoc.getHeight();
    int count = 0;

    for (int i = 0; i < 8400; i++) {
        float score = outputBuffer[0][4][i]; // Điểm tự tin của class "food"

        if (score > CONFIDENCE_THRESHOLD) {
            // Lấy tọa độ thô siêu nhỏ ra (ví dụ: 0.33, 0.81...)
            float cx = outputBuffer[0][0][i];
            float cy = outputBuffer[0][1][i];
            float w  = outputBuffer[0][2][i];
            float h  = outputBuffer[0][3][i];

            // SỬA LẠI CÔNG THỨC TOÁN HỌC:
            // Tính toán tọa độ góc dạng % trước, rồi mới phóng đại bằng cách nhân với kích thước ảnh gốc
            float left   = (cx - w / 2f) * scaleX;
            float top    = (cy - h / 2f) * scaleY;
            float right  = (cx + w / 2f) * scaleX;
            float bottom = (cy + h / 2f) * scaleY;

            // Giới hạn tọa độ không vượt quá biên của ảnh gốc - GIỮ NGUYÊN
            left = Math.max(0, left);
            top = Math.max(0, top);
            right = Math.min(bitmapGoc.getWidth(), right);
            bottom = Math.min(bitmapGoc.getHeight(), bottom);

            RectF location = new RectF(left, top, right, bottom);

            // Thêm vào danh sách kết quả tạm thời
            danhSachKetQua.add(new Recognition(String.valueOf(count), "food", score, location));
            count++;
        }
    }

    // SỬA LẠI: Không return trực tiếp nữa, mà phải chạy qua bộ lọc trùng NMS
    return applyNMS(danhSachKetQua);
}

    // === THÊM HÀM LỌC TRÙNG NMS NÀY VÀO TRONG CLASS YOLOV11DETECTOR ===
    private List<Recognition> applyNMS(List<Recognition> boxes) {
        List<Recognition> ketQuaNMS = new ArrayList<>();
        while (!boxes.isEmpty()) {
            // 1. Tìm box có điểm tự tin (score) cao nhất trong danh sách
            Recognition maxBox = boxes.get(0);
            for (Recognition b : boxes) {
                if (b.getConfidence() > maxBox.getConfidence()) {
                    maxBox = b;
                }
            }
            ketQuaNMS.add(maxBox);
            boxes.remove(maxBox);

            // 2. Loại bỏ tất cả các box khác đè lên maxBox quá nhiều (trùng lặp)
            List<Recognition> trungLap = new ArrayList<>();
            for (Recognition b : boxes) {
                if (calculateIoU(maxBox.getLocation(), b.getLocation()) > 0.45f) { // Ngưỡng đè nhau 45%
                    trungLap.add(b);
                }
            }
            boxes.removeAll(trungLap);
        }
        return ketQuaNMS;
    }

    // === THÊM HÀM TÍNH TỶ LỆ ĐÈ NHAU (IoU) VÀO TRONG CLASS YOLOV11DETECTOR ===
    private float calculateIoU(RectF box1, RectF box2) {
        float x1 = Math.max(box1.left, box2.left);
        float y1 = Math.max(box1.top, box2.top);
        float x2 = Math.min(box1.right, box2.right);
        float y2 = Math.min(box1.bottom, box2.bottom);

        float intersection = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);
        float area1 = (box1.right - box1.left) * (box1.bottom - box1.top);
        float area2 = (box2.right - box2.left) * (box2.bottom - box2.top);

        return intersection / (area1 + area2 - intersection);
    }

    /*

    * */


}
