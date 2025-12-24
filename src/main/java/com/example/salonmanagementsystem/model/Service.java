package com.example.salonmanagementsystem.model;

public class Service {
    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer durationMinutes;
    private String description;
    private boolean active;

    public Service() {
        this.active = true;
    }

    public Service(String name, String category, Double price, Integer durationMinutes) {
        this();
        this.name = name;
        this.category = category;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return active ? "ACTIVE" : "INACTIVE";
    }

    @Override
    public String toString() {
        return name + " - " + price + " ₸ (" + durationMinutes + " min)";
    }
}