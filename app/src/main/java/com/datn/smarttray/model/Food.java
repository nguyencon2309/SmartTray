package com.datn.smarttray.model;

public class Food {
    public String id;
    public String className;
    public String nameViet;
    public int price;
    public String description;
    public String image;

    // BẮT BUỘC CHO FIREBASE
    public Food() {
    }
    public Food(String id, String className,String nameViet, int price, String description, String image) {
        this.id = id;
        this.className = className;
        this.nameViet = nameViet;
        this.price = price;
        this.description = description;
        this.image = image;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public void setDescription(String desc){
        this.description = desc;
    }
    public String getNameViet(){
        return nameViet;
    }
    public String getClassName(){return className;}
    public int getPrice(){
        return price;
    }
    public String getImageUrl(){
        return image;
    }
    public String getDescription(){ return description;}
    public String getId(){ return id;}

}
