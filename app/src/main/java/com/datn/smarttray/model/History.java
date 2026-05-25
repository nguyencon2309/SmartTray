package com.datn.smarttray.model;

import android.graphics.Bitmap;

import com.datn.smarttray.utils.InvoiceItem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class History implements Serializable {

    public String imagePredict;
    public long timestamp;
    public String id;
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

    public int getQuanlityFood() {
        return listInvoice.size();
    }


    public String getLocalTime(){
        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                );

        String time =
                sdf.format(
                        new Date(timestamp)
                );
        return time;
    }
}

