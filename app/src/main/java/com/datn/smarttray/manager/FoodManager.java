package com.datn.smarttray.manager;

public class FoodManager {
//    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("food");
//    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("food_images");
//
//    // 1. Lấy danh sách (Get List)
//    public void getAllFoods(ValueEventListener listener) {
//        dbRef.addValueEventListener(listener);
//    }
//
//    // 2. Upload ảnh và lưu thông tin Food
//    public void addFood(Food food, Uri imageUri) {
//        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
//        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
//            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                food.image = uri.toString(); // Gán URL ảnh vào model
//                dbRef.child(food.id).setValue(food);
//            });
//        });
//    }


}
