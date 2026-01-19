package com.example.new_foodfleet;

public class MenueItemModel {

    public String itemName;
    public String price;   // price is String in Firebase
    public int qty;

    public MenueItemModel() { }

    public MenueItemModel(String itemName, String price, int qty) {
        this.itemName = itemName;
        this.price = price;
        this.qty = qty;
    }
}
