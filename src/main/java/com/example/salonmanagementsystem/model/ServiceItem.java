package com.example.salonmanagementsystem.model;

import java.math.BigDecimal;

public class ServiceItem {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private int durationMinutes;
    private boolean active;

    public ServiceItem() {}

    public ServiceItem(String name, String category, BigDecimal price, int durationMinutes, boolean active) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.active = active;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public BigDecimal getPrice() {return price;}
    public void setPrice(BigDecimal price) {this.price = price;}

    public int getDurationMinutes() {return durationMinutes;}
    public void setDurationMinutes(int durationMinutes) {this.durationMinutes = durationMinutes;}

    public boolean isActive() {return active;}
    public void setActive(boolean active) {this.active = active;}

    @Override
    public String toString() {
        return "ServiceItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", durationMinutes=" + durationMinutes +
                ", active=" + active +
                '}';
    }
}
