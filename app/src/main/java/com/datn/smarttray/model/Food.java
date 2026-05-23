package com.datn.smarttray.model;

public class Food {
    public String id;
    public String classname;
    public String vietnamese_name;
    public int price;
    public String description;
    public String image;
    public Food(){}
    public Food(String id, String classname,String vietnamese_name, int price, String description, String image) {
        this.id = id;
        this.classname = classname;
        this.vietnamese_name = vietnamese_name;
        this.price = price;
        this.description = description;
        this.image = image;
    }
    public String getNameViet(){
        return vietnamese_name;
    }
    public int getPrice(){
        return price;
    }
    public String getImageUrl(){
        return image;
    }
}
