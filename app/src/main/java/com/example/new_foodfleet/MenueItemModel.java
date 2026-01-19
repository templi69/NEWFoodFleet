package com.example.new_foodfleet;

public class MenueItemModel {

    public String itemName;
    public long price;  // <-- must be long
    public int qty;

    public MenueItemModel() { }

    public MenueItemModel(String itemName, long price, int qty) {
        this.itemName = itemName;
        this.price = price;
        this.qty = qty;
    }
}





