package com.example.salonmanagementsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ServicesController {

    @FXML
    private TableView<?> servicesTable;

    @FXML
    public void initialize() {
        System.out.println("ServicesController initialized");
    }

    @FXML
    private void onAdd() {
        System.out.println("Add service (not implemented)");
    }

    @FXML
    private void onEdit() {
        System.out.println("Edit service (not implemented)");
    }

    @FXML
    private void onDelete() {
        System.out.println("Delete service (not implemented)");
    }

    @FXML
    private void onRefresh() {
        System.out.println("Refresh services (not implemented)");
    }
}
