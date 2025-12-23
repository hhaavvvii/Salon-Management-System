package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane mainContainer;

    @FXML private Button clientsButton;
    @FXML private Button employeesButton;
    @FXML private Button servicesButton;
    @FXML private Button appointmentsButton;
    @FXML private Button paymentsButton;
    @FXML private Button reportsButton;
    @FXML private Button settingsButton;

    @FXML
    public void initialize() {
        User currentUser = SessionContext.getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user in session");
        }

        if (currentUser.getRole() == Role.MASTER) {
            employeesButton.setVisible(false);
            employeesButton.setManaged(false);

            reportsButton.setVisible(false);
            reportsButton.setManaged(false);

            settingsButton.setVisible(false);
            settingsButton.setManaged(false);

            paymentsButton.setVisible(false);
            paymentsButton.setManaged(false);
        }
    }

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

    private void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Node view = loader.load();

        mainContainer.getChildren().setAll(view);
    }


    @FXML
    private void handleLogout() {
        try {
            SessionContext.clear();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(scene);

            // 🔑 КЛЮЧЕВОЕ
            stage.setResizable(true);
            stage.setMaximized(false);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
