package com.datn.smarttray.fragment;

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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.smarttray.InvoiceFragment;
import com.datn.smarttray.R;
import com.datn.smarttray.data.Recognition;
import com.datn.smarttray.detector.EfficientNetClassifier;
import com.datn.smarttray.detector.YOLOv11Detector;
import com.datn.smarttray.manager.ModelManager;
import com.datn.smarttray.utils.ImageUtils;
import com.datn.smarttray.utils.InvoiceItem;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;


public class ScanFragment extends Fragment {


    ImageView imageView;
    Button galleryBtn, cameraBtn, analystBtn;
    Uri image_uri;
    YOLOv11Detector yolOv11Detector;
    EfficientNetClassifier efficientNetClassifier;
    Bitmap image_predict;
    TextView txtLog;

    InvoiceFragment invoiceFragment;

    FrameLayout invoiceContainer;




    public ScanFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_scan,
                container,
                false
        );

        initViews(view);

        initModels();

        initInvoiceFragment();

        setupListeners();

        return view;
    }
    private void initViews(View view) {

        imageView = view.findViewById(R.id.imageView);

        galleryBtn = view.findViewById(R.id.button);

        cameraBtn = view.findViewById(R.id.button2);

        analystBtn = view.findViewById(R.id.button3);

        txtLog = view.findViewById(R.id.txtView);

        invoiceContainer =
                view.findViewById(R.id.invoiceContainer);
    }

    private void initModels() {

        yolOv11Detector = ModelManager.getYoloDetector();

        efficientNetClassifier = ModelManager.getClassifier();
    }

    private void initInvoiceFragment() {

        invoiceFragment = new InvoiceFragment();

        getChildFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.invoiceContainer,
                        invoiceFragment
                )
                .commit();
    }

    private void setupListeners() {

        galleryBtn.setOnClickListener(v -> {

            openGallery();

        });

        cameraBtn.setOnClickListener(v -> {

            checkCameraPermission();

        });

        analystBtn.setOnClickListener(v -> {

            analyzeImage();

        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );

        galleryLauncher.launch(galleryIntent);

    }
    private void checkCameraPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        112
                );

            } else {

                openCamera();
            }

        } else {

            openCamera();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == 112) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                Toast.makeText(
                        requireContext(),
                        "Bạn cần cấp quyền Camera",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraLauncher.launch(cameraIntent);
    }


    private Bitmap getBitmapImage(){
        if (imageView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            return bitmap;
        }
        return null;
    }

    private void analyzeImage() {
        Bitmap imagePredict = getBitmapImage();
        if (imagePredict == null) {

            Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn ảnh trước",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }
        if(yolOv11Detector == null || efficientNetClassifier == null){
            Toast.makeText(
                    requireContext(),
                    "Model chưa được load",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        invoiceContainer.setVisibility(View.GONE);
        txtLog.setVisibility(View.VISIBLE);
        txtLog.setText("Đang phân tích...");

        List<Recognition> results= yolOv11Detector.detectObjects(imagePredict);

        if (results == null || results.isEmpty()) {

            txtLog.setText(
                    "Không phát hiện món ăn nào"
            );

            return;
        }
        classifyFood(results,imagePredict);
    }
    private String ClassifierBox(Bitmap image_predict,RectF loc, int xx){
        Bitmap croppedBox = ImageUtils.cropBitmapWithRect(image_predict,loc,10);
        String resultClassier = efficientNetClassifier.classifyFood(croppedBox);
        croppedBox.recycle();
        return resultClassier;
    }
    private void drawBox(String nameFood,RectF loc,Canvas canvas){


        Paint paintBox = new Paint();
        paintBox.setColor(Color.RED);
        paintBox.setStyle(Paint.Style.STROKE);


        Paint paintText = new Paint();
        paintText.setColor(Color.GREEN);

        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(loc.width()/8f);
        paintBox.setStrokeWidth(loc.width()/50f);
        canvas.drawRect(loc, paintBox);

        canvas.drawText(nameFood, loc.left, loc.top + 15, paintText);

    }

    private void updateLayoutInvoice(Map<String, InvoiceItem> mapBill){
        List<InvoiceItem> danhSachInvoice = new ArrayList<>(mapBill.values());

        txtLog.setVisibility(View.GONE);
        invoiceContainer.setVisibility(View.VISIBLE);
        invoiceFragment.updateInvoice(danhSachInvoice);

    }

    private Map<String, InvoiceItem> addInvoiceItem(Map<String, InvoiceItem> mapBill, String FoodRatio){
        int lastSpaceIndex = FoodRatio.lastIndexOf(" ");

        String tenMonAnGoc = FoodRatio.substring(0,lastSpaceIndex);

        if (mapBill.containsKey(tenMonAnGoc)) {
            InvoiceItem itemCu = mapBill.get(tenMonAnGoc);
            mapBill.put(tenMonAnGoc, new InvoiceItem(tenMonAnGoc, itemCu.getQuantity() + 1, 5000));
        } else {
            // Nếu là món mới xuất hiện thì đặt số lượng là 1
            mapBill.put(tenMonAnGoc, new InvoiceItem(tenMonAnGoc, 1, 5000));
        }


       return mapBill;
    }

    private void classifyFood(List<Recognition> results, Bitmap imagePredict){
        Map<String, InvoiceItem> mapBill = new HashMap<>();
        Bitmap mutableBitmap = imagePredict.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        for (Recognition box : results){
            RectF loc = box.getLocation();

            // Lấy tọa độ nguyên (int) để cắt Bitmap
            int left = Math.max(0, (int) loc.left);
            int top = Math.max(0, (int) loc.top);
            int width = Math.min(imagePredict.getWidth() - left, (int) loc.width());
            int height = Math.min(imagePredict.getHeight() - top, (int) loc.height());



            if(width>0 && height>0){
                String result_classifier = ClassifierBox(imagePredict,loc,10);
                drawBox(result_classifier,loc,canvas);
                //String[] mangTach = result_classifier.split(" ");
                addInvoiceItem(mapBill,result_classifier);
            }

        }
        updateLayoutInvoice(mapBill);
        imageView.setImageBitmap(mutableBitmap);
    }
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
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
    private final ActivityResultLauncher<Intent> cameraLauncher  = registerForActivityResult(
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
    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input){
        if (input == null) {
            return null;
        }
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = requireActivity().getContentResolver().query(image_uri, orientationColumn, null, null, null);
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

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    requireActivity().getContentResolver().openFileDescriptor(selectedFileUri, "r");
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
}