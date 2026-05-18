package com.datn.smarttray.detector;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.List;

public class EfficientNetClassifier {
    private int INPUT_SIZE = 224; // EfficientNet-B0 thường dùng 224x224
    private Interpreter tflite;
    private List<String> labelList;

    public EfficientNetClassifier(Context context, String modelPath,List<String> labelList) throws IOException {
        this.tflite = new Interpreter(loadModelFile(context.getAssets(),modelPath));
        this.labelList = labelList;

    }
    private java.nio.MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Hàm phân loại ảnh đã cắt
    public String classifyFood(Bitmap croppedBitmap) {
        // 1. Resize ảnh về 224x224
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, INPUT_SIZE, INPUT_SIZE, false);

        // 2. Nạp dữ liệu ảnh vào ByteBuffer (Chuẩn hóa /255.0f hoặc theo chuẩn EfficientNet)
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
        inputBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        inputBuffer.rewind();
        for (int pixelValue : intValues) {
            // Chuẩn hóa màu về khoảng [0, 1] giống như lúc train bên Python
            inputBuffer.putFloat(((pixelValue >> 16) & 0xFF) );
            inputBuffer.putFloat(((pixelValue >> 8) & 0xFF) );
            inputBuffer.putFloat((pixelValue & 0xFF));
        }

        // 3. Mảng chứa đầu ra: 1 x Số_Lượng_Món_Ăn
        float[][] outputBuffer = new float[1][labelList.size()];

        // 4. Chạy mô hình
        tflite.run(inputBuffer, outputBuffer);

        // 5. Tìm món ăn có xác suất cao nhất (Argmax)
        int maxIndex = 0;
        float maxScore = outputBuffer[0][0];
        for (int i = 1; i < labelList.size(); i++) {
            if (outputBuffer[0][i] > maxScore) {
                maxScore = outputBuffer[0][i];
                maxIndex = i;
            }
        }

        // Trả về tên món ăn nếu độ tự tin tốt, ngược lại trả về không xác định
        //return maxScore > 0.5f ? labelList.get(maxIndex) : "Chưa rõ món";
        return labelList.get(maxIndex) + " " + maxScore;
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}
