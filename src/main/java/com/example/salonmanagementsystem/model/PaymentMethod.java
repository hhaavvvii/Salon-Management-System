package com.example.salonmanagementsystem.model;

public enum PaymentMethod {
    CASH("Cash"),
    CARD("Card"),
    TRANSFER("Transfer");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}