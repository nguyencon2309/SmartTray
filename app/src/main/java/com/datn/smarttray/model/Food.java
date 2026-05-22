package com.datn.smarttray.model;

public class Food {
    public String id;
    public String name;
    public int price;
    public String description;
    public String image;
    public Food(){}
    public Food(String id, String name, int price, String description, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
    }
}
