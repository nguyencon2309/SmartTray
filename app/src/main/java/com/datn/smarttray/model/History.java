package com.datn.smarttray.model;



import com.datn.smarttray.utils.InvoiceItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class History implements Serializable {

    @SerializedName("imagePredict")
    public String imagePredict;
    @SerializedName("timestamp")
    public long timestamp;
    @SerializedName("id")
    public String id;
    @SerializedName("listInvoice")
    public List<InvoiceItem> listInvoice;
    public History(String imagePredict,long timestamp, List<InvoiceItem> listInvoice ){
        this.id=String.valueOf(timestamp);
        this.timestamp=timestamp;
        this.imagePredict=imagePredict;
        this.listInvoice = listInvoice;
    }
    public int getSumPrice(){
        int s = 0;
        for(InvoiceItem invoiceItem:listInvoice){
            s+=invoiceItem.getTotalPrice();
        }
        return s;
    }
    public List<InvoiceItem> getListInvoice(){
        return listInvoice;
    }
    public long getTimestamp(){
        return timestamp;
    }
    public String getId(){
        return id;
    }
    public String getImagePredict(){
        return imagePredict;
    }

}

