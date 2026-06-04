package com.datn.smarttray.model;

import com.google.gson.annotations.SerializedName;

public class FoodShortReponse {
    @SerializedName("id")
    private String id;
    @SerializedName("nameViet")
    private String nameViet;
    @SerializedName("price")
    private int price;
    @SerializedName("image")
    private String image;
    public FoodShortReponse(String id, String nameViet,int price,String image){
        this.image = image;
        this.id = id;
        this.nameViet = nameViet;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getNameViet() {
        return nameViet;
    }
    public void setPrice(int price){
        this.price = price;
    }
}
