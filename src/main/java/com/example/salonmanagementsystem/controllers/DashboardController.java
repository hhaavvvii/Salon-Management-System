package com.example.salonmanagementsystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane mainContainer;

    @FXML
    private void openClients() throws IOException {
        loadView("/fxml/clients.fxml");
    }

    @FXML
    private void openEmployees() throws IOException {
        loadView("/fxml/employees.fxml");
    }

    @FXML
    private void openServices() throws IOException {
        loadView("/fxml/services.fxml");
    }

    @FXML
    private void openAppointments() throws IOException {
        loadView("/fxml/appointments.fxml");
    }

    @FXML
    private void openPayments() throws IOException {
        loadView("/fxml/payments.fxml");
    }

    @FXML
    private void openReports() throws IOException {
        loadView("/fxml/reports.fxml");
    }

    @FXML
    private void openSettings() throws IOException {
        loadView("/fxml/settings.fxml");
    }

    // 🔹 единый метод загрузки
    private void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Node view = loader.load();

        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(view);
    }
}
