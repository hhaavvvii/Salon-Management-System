package com.example.salonmanagementsystem.model;

public class InventoryItem {
    private Long id;
    private String name;
    private int quantity;
    private int threshold;

    public InventoryItem(){}

    public InventoryItem(String name, int quantity, int threshold){
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

    public int getThreshold() {return threshold;}
    public void setThreshold(int threshold) {this.threshold = threshold;}

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", threshold=" + threshold +
                '}';
    }
}

