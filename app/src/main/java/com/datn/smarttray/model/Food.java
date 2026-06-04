package com.datn.smarttray.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Food {
    @SerializedName("id")
    private String id;
    @SerializedName("nameViet")
    private String nameViet;
    @SerializedName("price")
    private int price;
    @SerializedName("image")
    private String image;
    @SerializedName("description")
    private String description;
    @SerializedName("className")
    private String className;
    @SerializedName("calories")
    private int calories;
    @SerializedName("category")
    private String category;
    @SerializedName("recipe")
    private List<String> recipe;
    @SerializedName("tips")
    private List<String> tips;
    @SerializedName("ingredients")
    private List<String> ingredients;
    // BẮT BUỘC CHO FIREBASE
    public Food() {
    }
    public Food(String id, String className,String nameViet, int price, String description, String image,String category,int calories, List<String> recipe, List<String> ingredients, List<String> tips) {
        this.id = id;
        this.className = className;
        this.nameViet = nameViet;
        this.price = price;
        this.description = description;
        this.image = image;
        this.category = category;
        this.calories = calories;
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.tips = tips;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public void setDescription(String desc){
        this.description = desc;
    }
    public void setCalories(int calories){this.calories = calories;}
    public void setCategory(String category){this.category = category;}
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
    public String getCategory(){return category;}
    public int getCalories(){
        return calories;
    }
    public List<String> getRecipe(){
        return recipe;
    }
    public List<String> getIngredients(){
        return ingredients;
    }
    public List<String> getTips(){
        return tips;
    }

}
