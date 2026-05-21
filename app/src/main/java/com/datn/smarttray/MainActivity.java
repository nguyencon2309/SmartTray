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
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.smarttray.data.Recognition;
import com.datn.smarttray.detector.EfficientNetClassifier;
import com.datn.smarttray.detector.YOLOv11Detector;
import com.datn.smarttray.utils.InvoiceItem;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button galleryBtn, cameraBtn, analystBtn;
    Uri image_uri;
    YOLOv11Detector yolOv11Detector;
    EfficientNetClassifier efficientNetClassifier;
    Bitmap image_predict;
    TextView txtLog;

    InvoiceFragment invoiceFragment;

    FrameLayout invoiceContainer;



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
        txtLog = findViewById(R.id.txtView);
        invoiceContainer = findViewById(R.id.invoiceContainer);
        invoiceFragment = new InvoiceFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.invoiceContainer, invoiceFragment)
                .commit();

        //load model yolo
        try {
            List<String> danhSachMonAn = loadLabelList("labels.txt");
            yolOv11Detector = new YOLOv11Detector(getAssets(), "best_float16.tflite");
            efficientNetClassifier = new EfficientNetClassifier(this,"efficientnet_classifier.tflite",danhSachMonAn);

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
                        invoiceContainer.setVisibility(View.GONE);
                        txtLog.setVisibility(View.VISIBLE);
                        txtLog.setText("Đang phân tích...");

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
        final Map<String, InvoiceItem> mapHoaDon = new HashMap<>();

        // Tạo bản copy của ảnh để vẽ khung
        Bitmap mutableBitmap = bitmapGoc.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paintBox = new Paint();
        paintBox.setColor(Color.RED);
        paintBox.setStyle(Paint.Style.STROKE);
        paintBox.setStrokeWidth(3.0f);

        Paint paintText = new Paint();
        paintText.setColor(Color.GREEN);
        paintText.setTextSize(20.0f);
        paintText.setStyle(Paint.Style.FILL);

        StringBuilder logText = new StringBuilder("Kết quả phân tích:\n");

        // Duyệt qua danh sách kết quả sạch nhận từ class YOLOv11Detector
        for (Recognition box : results) {

            RectF loc = box.getLocation();

            // Lấy tọa độ nguyên (int) để cắt Bitmap
            int left = Math.max(0, (int) loc.left);
            int top = Math.max(0, (int) loc.top);
            int width = Math.min(bitmapGoc.getWidth() - left, (int) loc.width());
            int height = Math.min(bitmapGoc.getHeight() - top, (int) loc.height());

            if (width > 0 && height > 0) {
                // TIẾN HÀNH CẮT (CROP) ẢNH ĐĨA ĐỒ ĂN
                Bitmap croppedFood = Bitmap.createBitmap(bitmapGoc, Math.max(left-10,0), Math.max(top-10,0), width+20, height+20);

                // BƯỚC C: Thả ảnh vừa cắt vào EfficientNet để nhận diện món cụ thể
                String tenMonAn = efficientNetClassifier.classifyFood(croppedFood);

                paintText.setTextSize(width/8f);
                paintBox.setStrokeWidth(width/50f);
                canvas.drawRect(loc, paintBox);
                canvas.drawText(tenMonAn, loc.left, loc.top + 15, paintText);
                //tính tiền
                String[] mangTach = tenMonAn.split(" ");
                String tenMonAnGoc = mangTach[0];

                if (mapHoaDon.containsKey(tenMonAnGoc)) {
                    InvoiceItem itemCu = mapHoaDon.get(tenMonAnGoc);
                    mapHoaDon.put(tenMonAnGoc, new InvoiceItem(tenMonAnGoc, itemCu.getQuantity() + 1, 5000));
                } else {
                    // Nếu là món mới xuất hiện thì đặt số lượng là 1
                    mapHoaDon.put(tenMonAnGoc, new InvoiceItem(tenMonAnGoc, 1, 5000));
                }
                final List<InvoiceItem> danhSachInvoice = new ArrayList<>(mapHoaDon.values());

                if (!danhSachInvoice.isEmpty()) {
                    // NẾU CÓ MÓN ĂN: Ẩn chữ thông báo, hiện khung hóa đơn và nạp bảng dữ liệu vào
                    txtLog.setVisibility(View.GONE);
                    invoiceContainer.setVisibility(View.VISIBLE);

                    invoiceFragment.updateInvoice(danhSachInvoice);
                } else {
                    // NẾU KHÔNG CÓ MÓN: Hiện TextView thông báo lỗi, ẩn bảng hóa đơn đi
                    txtLog.setVisibility(View.VISIBLE);
                    txtLog.setText("Không phát hiện đĩa món ăn nào trên khay!");
                    invoiceContainer.setVisibility(View.GONE);
                }

            }
        }
        android.util.Log.d("SMART_TRAY_AI", logText.toString());
        // Cập nhật lên màn hình
        imageView.setImageBitmap(mutableBitmap);

    }

    private List<String> loadLabelList(String fileName) {
        List<String> labels = new ArrayList<>();
        try {
            // Mở file từ thư mục assets
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(fileName), "UTF-8")
            );
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Bỏ qua các dòng trống nếu có
                    labels.add(line.trim());
                }
            }
            reader.close();
            android.util.Log.d("EFFICIENTNET_LABEL", "Đã load thành công " + labels.size() + " món ăn.");
        } catch (IOException e) {
            android.util.Log.e("EFFICIENTNET_LABEL", "Lỗi không đọc được file label: " + e.getMessage());
            e.printStackTrace();
        }
        return labels;
    }

    @Override
    protected void onDestroy() {
        yolOv11Detector.close();
        super.onDestroy();

    }





}

