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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.smarttray.InvoiceFragment;
import com.datn.smarttray.R;
import com.datn.smarttray.data.Recognition;
import com.datn.smarttray.detector.EfficientNetClassifier;
import com.datn.smarttray.detector.FoodClassifier;
import com.datn.smarttray.detector.YOLOv11Detector;
import com.datn.smarttray.manager.FoodManager;
import com.datn.smarttray.manager.HistoryManager;
import com.datn.smarttray.manager.ModelManager;
import com.datn.smarttray.model.Food;
import com.datn.smarttray.model.History;
import com.datn.smarttray.repository.HistoryRepository;
import com.datn.smarttray.utils.FileUtil;
import com.datn.smarttray.utils.ImageStorageUtil;
import com.datn.smarttray.utils.ImageUtils;
import com.datn.smarttray.utils.InvoiceItem;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ScanFragment extends Fragment {


    ImageView imageView;
    Button analystBtn;
    Uri image_uri;
    YOLOv11Detector yolOv11Detector;
    FoodClassifier efficientNetClassifier;
    Bitmap image_predict,copy_image_bitmap;
    TextView txtLog;

    InvoiceFragment invoiceFragment;

    FrameLayout invoiceContainer;
    List<Food> listFood;
    List<InvoiceItem> danhSachInvoice;

    ImageButton btnSetting,galleryBtn, cameraBtn;

    CardView layoutSetting;

    SeekBar seekYolo, seekClassifier;

    TextView txtYoloValue, txtClassifierValue;

    long startTime;
    private boolean isPredictMode = true;




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

        galleryBtn = view.findViewById(R.id.imageBtnGallery);

        cameraBtn = view.findViewById(R.id.imageBtnCamera);

        analystBtn = view.findViewById(R.id.button3);

        txtLog = view.findViewById(R.id.txtView);

        invoiceContainer =
                view.findViewById(R.id.invoiceContainer);

        btnSetting = view.findViewById(R.id.btnSetting);

        layoutSetting = view.findViewById(R.id.layoutSetting);

        seekYolo = view.findViewById(R.id.seekYolo);

        seekClassifier = view.findViewById(R.id.seekClassifier);

        txtYoloValue = view.findViewById(R.id.txtYoloValue);

        txtClassifierValue = view.findViewById(R.id.txtClassifierValue);
    }

    private void initModels() {

        yolOv11Detector = ModelManager.getYoloDetector();

        efficientNetClassifier = ModelManager.getClassifier();
        listFood = FoodManager.getFoodList();
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

    private void setButtonPredict(){
        danhSachInvoice=null;
        isPredictMode = true;
        analystBtn.setText("PREDICT");

    }
    private void setButtonSave(){
        isPredictMode = false;
        analystBtn.setText("SAVE");
    }
    private void setupListeners() {

        galleryBtn.setOnClickListener(v -> {

            openGallery();

        });

        cameraBtn.setOnClickListener(v -> {

            checkCameraPermission();

        });

        analystBtn.setOnClickListener(v -> {
            if(isPredictMode){
                analyzeImage();
            }
            else{
                saveHistory();
            }
        });
        btnSetting.setOnClickListener(v->{
            showHidenLayoutSetting();
        });
        setupThresholdSeekBar();

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

    /*
    private Bitmap getBitmapImage(){
        if (imageView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            return bitmap;
        }
        return null;
    }*/

    private void analyzeImage() {
        startTime = System.currentTimeMillis();
        Bitmap imagePredict = image_predict;
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

        setThresholdModel();

        List<Recognition> results= yolOv11Detector.detectObjects(imagePredict);

        if (results == null || results.isEmpty()) {

            txtLog.setText(
                    "Không phát hiện món ăn nào"
            );
            image_predict = null;

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
        String[] splitArray = nameFood.split(" ");
        int index = Integer.parseInt(splitArray[0]);
        String tenMonAnGoc;
        if(index==-1){
            tenMonAnGoc = "Unknow";
        }
        else{
            tenMonAnGoc = listFood.get(index).getNameViet();
        }
        Paint paintBox = new Paint();
        paintBox.setColor(Color.RED);
        paintBox.setStyle(Paint.Style.STROKE);


        Paint paintText = new Paint();
        paintText.setColor(Color.GREEN);

        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(loc.width()/8f);
        paintBox.setStrokeWidth(loc.width()/50f);
        canvas.drawRect(loc, paintBox);

        canvas.drawText(tenMonAnGoc+" "+splitArray[1], loc.left, loc.top + 15, paintText);

    }

    private void updateLayoutInvoice(Map<String, InvoiceItem> mapBill){
        danhSachInvoice = null;
        danhSachInvoice = new ArrayList<>(mapBill.values());

        txtLog.setVisibility(View.GONE);
        invoiceContainer.setVisibility(View.VISIBLE);
        invoiceFragment.updateInvoice(danhSachInvoice);

    }

    private Map<String, InvoiceItem> addInvoiceItem(Map<String, InvoiceItem> mapBill, String FoodRatio){
        String[] splitArray = FoodRatio.split(" ");
        int index = Integer.parseInt(splitArray[0]);
        String tenMonAnGoc;
        int price;
        if(index==-1){
            return mapBill;
        }
        else{
            tenMonAnGoc = listFood.get(index).getNameViet();
            price = listFood.get(Integer.parseInt(splitArray[0])).getPrice();
        }
        String idFood = listFood.get(index).getId();
        if (mapBill.containsKey(tenMonAnGoc)) {
            InvoiceItem itemCu = mapBill.get(tenMonAnGoc);
            mapBill.put(tenMonAnGoc, new InvoiceItem(idFood,tenMonAnGoc, itemCu.getQuantity() + 1, price));
        } else {
            // Nếu là món mới xuất hiện thì đặt số lượng là 1
            mapBill.put(tenMonAnGoc, new InvoiceItem(idFood,tenMonAnGoc, 1, price));
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
        long duration = System.currentTimeMillis()-startTime;
        updateLayoutInvoice(mapBill);
        imageView.setImageBitmap(mutableBitmap);

        copy_image_bitmap = mutableBitmap;
        txtLog.setVisibility(View.VISIBLE);
        txtLog.setText("Time predict "+duration+" ms");


        setButtonSave();
    }
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                        Uri selectedImageUri = result.getData().getData();
                        if(selectedImageUri != null){
                            setButtonPredict();

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
                        setButtonPredict();
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
    private void saveHistory(){

        History history = new History(saveImage(copy_image_bitmap),System.currentTimeMillis(),danhSachInvoice);
        HistoryManager.addHistory(requireContext(),history);
        copy_image_bitmap.recycle();
        copy_image_bitmap = null;
        setButtonPredict();

    }
    private void saveHistory(Bitmap bitmap) throws IOException {
        Bitmap resized =
                Bitmap.createScaledBitmap(
                        bitmap,
                        600,
                        600,
                        true
                );
        File file = FileUtil.bitmapToFile(requireContext(),resized);
        History history = new History("",System.currentTimeMillis(),danhSachInvoice);
        String json =
                new Gson().toJson(history);
        RequestBody dataBody =
                RequestBody.create(
                        MediaType.parse("application/json"),
                        json
        );
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData(
                        "file",
                        file.getName(),
                        requestFile
                );


        HistoryRepository.addHistory(filePart, dataBody, new HistoryRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(
                        requireContext(),
                        "Upload thành công",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(
                        requireContext(),
                        error,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    private String saveImage(Bitmap bitmap){
        Bitmap resized =
                Bitmap.createScaledBitmap(
                        bitmap,
                        600,
                        600,
                        true
                );
        String path =
                ImageStorageUtil.saveBitmap(
                        requireContext(),
                        resized
                );
        resized = null;
        return path;
    }
    private void showHidenLayoutSetting(){
        if(layoutSetting.getVisibility() == View.GONE){
            layoutSetting.setVisibility(View.VISIBLE);
        }else
        {
            layoutSetting.setVisibility(View.GONE);
        }
    }
    private void setupThresholdSeekBar(){
        seekYolo.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        float threshold = progress / 100f;
                        txtYoloValue.setText(
                                String.format("%.2f", threshold)
                        );
                    }
                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar
                    ) {
                    }
                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar
                    ) {
                    }
                }
        );
        seekClassifier.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        float threshold = progress / 100f;
                        txtClassifierValue.setText(
                                String.format("%.2f", threshold)
                        );

                    }
                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar
                    ) {
                    }
                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar
                    ) {
                    }
                }
        );
    }
    private float getFloatFromTextView(TextView textView){
        try {
            String textValue = textView.getText().toString();
            textValue = textValue.replace(',', '.');
            float threshold = Float.parseFloat(textValue);
            return threshold;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0f;
    }
    private void setThresholdModel(){
        yolOv11Detector.setTheshold(getFloatFromTextView(txtYoloValue));
        efficientNetClassifier.setThreshold(getFloatFromTextView(txtClassifierValue));
    }

}