package com.datn.smarttray.model;

import com.google.gson.annotations.SerializedName;

public class HistoryShortReponse {
    @SerializedName("id")
    private String _id;
    @SerializedName("imagePredict")
    private String image;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("sumPrice")
    private int sumPrice;
    @SerializedName("quanlityFood")
    private int quanlityFood;
    public HistoryShortReponse(String id, String image, long timestamp,int sumPrice,int quanlityFood){
        this._id = id;
        this.image = image;
        this.timestamp = timestamp;
        this.sumPrice = sumPrice;
        this.quanlityFood = quanlityFood;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return _id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getQuanlityFood() {
        return quanlityFood;
    }

    public int getSumPrice() {
        return sumPrice;
    }
}
