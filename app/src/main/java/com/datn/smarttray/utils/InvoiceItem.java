package com.datn.smarttray.utils;

import java.io.Serializable;

public class InvoiceItem implements Serializable {
    private String name;     // Tên món ăn
    private int quantity;    // Số lượng
    private int price;       // Đơn giá

    public InvoiceItem(String name, int quantity, int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Các hàm Getter/Setter
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
    public int getTotalPrice() { return quantity * price; }
}
