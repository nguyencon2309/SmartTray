package com.datn.smarttray;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.smarttray.data.Recognition;
import com.datn.smarttray.detector.YOLOv11Detector;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button galleryBtn, cameraBtn, analystBtn;
    Uri image_uri;
    YOLOv11Detector yolOv11Detector;
    Bitmap image_predict;
    TextView txtLog;



    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                        Uri selectedImageUri = result.getData().getData();
                        if(selectedImageUri != null){
                            image_uri = result.getData().getData();
                            Bitmap inputImage = uriToBitmap(image_uri);
                            Bitmap rotated = rotateBitmap(inputImage);
                            imageView.setImageBitmap(rotated);
                            image_predict = rotated;
                        }

                    }
                }

            }
    );

    //TODO capture the image using camera and display it
    ActivityResultLauncher<Intent> cameraActivityResultLauncher  = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK

                    ){
                        Bitmap inputImage = uriToBitmap(image_uri);
                        Bitmap rotated = rotateBitmap(inputImage);
                        imageView.setImageBitmap(rotated);
                        image_predict = rotated;
                    }
                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        galleryBtn = findViewById(R.id.button);
        cameraBtn = findViewById(R.id.button2);
        analystBtn = findViewById(R.id.button3);
        txtLog = findViewById(R.id.textView);

        //load model yolo
        try {
            yolOv11Detector = new YOLOv11Detector(getAssets(), "best_float16.tflite");
            txtLog.setText("load model yolo thành công");
        } catch (IOException e) {
            txtLog.setText("load model yolo thất bại" + e.getMessage());
        }

        //TODO ask for permission of camera upon first launch of application



        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryActivityResultLauncher.launch(galleryIntent);

            }
        });


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED
                    ) {
                        String[] permission = {
                                Manifest.permission.CAMERA
                        };
                        requestPermissions(permission, 112);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }

            }
        });

        analystBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(yolOv11Detector!=null){
                    if (yolOv11Detector != null) {
                        // 1. Gọi class YOLO xử lý và trả về list kết quả sạch
                        List<Recognition> results = yolOv11Detector.detectObjects(image_predict);

                        // 2. Hiển thị kết quả lên UI
                        hienThiKetQuaLenUI(image_predict, results);
                    }
                }
            }
        });
    }

    //TODO opens camera so that user can capture image

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 112) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openCamera();
            } else {

                Toast.makeText(this, "Bạn cần cấp quyền Camera ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //TODO takes URI of the image and returns bitmap
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            if (parcelFileDescriptor == null) {
                return null;
            }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    //TODO rotate image if image captured on samsung devices
    //TODO Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input){
        if (input == null) {
            return null;
        }
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = getContentResolver().query(image_uri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            cur.close();
        }
        Log.d("tryOrientation",orientation+"");
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        Bitmap cropped = Bitmap.createBitmap(input,0,0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        return cropped;
    }

    public void hienThiKetQuaLenUI(Bitmap bitmapGoc,List<Recognition> results){
        if (results.isEmpty()) {
            txtLog.setText("Không phát hiện thấy món ăn nào!");
            return;
        }

        // Tạo bản copy của ảnh để vẽ khung
        Bitmap mutableBitmap = bitmapGoc.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paintBox = new Paint();
        paintBox.setColor(Color.RED);
        paintBox.setStyle(Paint.Style.STROKE);
        paintBox.setStrokeWidth(8.0f);

        Paint paintText = new Paint();
        paintText.setColor(Color.GREEN);
        paintText.setTextSize(40.0f);
        paintText.setStyle(Paint.Style.FILL);

        StringBuilder logText = new StringBuilder("Kết quả phân tích:\n");

        // Duyệt qua danh sách kết quả sạch nhận từ class YOLOv11Detector
        for (Recognition res : results) {
            // Vẽ khung lên hình ảnh
            canvas.drawRect(res.getLocation(), paintBox);
            canvas.drawText(res.getTitle() + ": " + String.format("%.2f", res.getConfidence()),
                    res.getLocation().left, res.getLocation().top - 10, paintText);

            // Nối chuỗi để in ra TextView log
            logText.append(String.format("- Tìm thấy %s (Độ chính xác: %.2f) tại Vị trí: %s\n", res.getTitle(), res.getConfidence(), res.getLocation().toString()));
        }
        android.util.Log.d("SMART_TRAY_AI", logText.toString());
        // Cập nhật lên màn hình
        imageView.setImageBitmap(mutableBitmap);
        txtLog.setText(logText.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }





}

