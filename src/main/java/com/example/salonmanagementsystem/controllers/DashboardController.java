package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane mainContainer;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    // Кнопки навигации для подсветки активной
    @FXML private Button clientsButton;
    @FXML private Button employeesButton;
    @FXML private Button servicesButton;
    @FXML private Button appointmentsButton;
    @FXML private Button paymentsButton;
    @FXML private Button reportsButton;

    private Button activeButton = null;

    @FXML
    public void initialize() {
        loadUserInfo();
        configureAccessByRole();
        showWelcomeScreen();
    }

    private void configureAccessByRole() {
        if (SessionContext.isMaster()) {
            // Скрыть недоступные для MASTER окна
            paymentsButton.setVisible(false);
            paymentsButton.setManaged(false);

            reportsButton.setVisible(false);
            reportsButton.setManaged(false);

            employeesButton.setVisible(false);
            employeesButton.setManaged(false);

        }
    }

    //USER INFO
    private void loadUserInfo() {
        User currentUser = SessionContext.getCurrentUser();

        if (currentUser != null) {
            userNameLabel.setText(currentUser.getUsername());
            userRoleLabel.setText("Role: " + currentUser.getRole().name());
        } else {
            userNameLabel.setText("Guest");
            userRoleLabel.setText("Role: Unknown");
        }
    }

    //NAVIGATION METHODS

    @FXML
    private void openClients() {
        loadContent("/fxml/clients.fxml");
        setActiveButton(clientsButton);
    }

    @FXML
    private void openEmployees() {
        if (SessionContext.isMaster()) {
            showError("Access denied: insufficient permissions");
            return;
        }
        loadContent("/fxml/employees.fxml");
        setActiveButton(employeesButton);
    }

    @FXML
    private void openServices() {
        loadContent("/fxml/services.fxml");
        setActiveButton(servicesButton);
    }

    @FXML
    private void openAppointments() {
        loadContent("/fxml/appointments.fxml");
        setActiveButton(appointmentsButton);
    }

    @FXML
    private void openPayments() {
        if (SessionContext.isMaster()) {
            showError("Access denied: insufficient permissions");
            return;
        }
        loadContent("/fxml/payments.fxml");
        setActiveButton(paymentsButton);
    }

    @FXML
    private void openReports() {
        if (SessionContext.isMaster()) {
            showError("Access denied: insufficient permissions");
            return;
        }
        loadContent("/fxml/reports.fxml");
        setActiveButton(reportsButton);
    }

    @FXML
    private void handleLogout() {
        try {
            // Очищаем сессию
            SessionContext.clearSession();

            // Закрываем текущее окно
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.close();

            // Открываем окно логина
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login - Salon Management System");
            loginStage.setScene(new Scene(root));
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to logout: " + e.getMessage());
        }
    }

    //UTILITY METHODS

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();

            // Очищаем и добавляем новый контент
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(content);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load content: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showError("FXML file not found: " + fxmlPath);
        }
    }


    private void showWelcomeScreen() {
        javafx.scene.layout.HBox mainBox = new javafx.scene.layout.HBox(40);
        mainBox.setAlignment(javafx.geometry.Pos.CENTER);
        mainBox.setStyle("-fx-padding: 30 40;");

        // ЛЕВАЯ ЧАСТЬ:
        javafx.scene.layout.VBox leftBox = new javafx.scene.layout.VBox(15);
        leftBox.setAlignment(javafx.geometry.Pos.CENTER);
        leftBox.setMinWidth(240);
        leftBox.setMaxWidth(240);

        // Логотип - изображение
        javafx.scene.image.ImageView logoImage = new javafx.scene.image.ImageView();
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/icons/logo.png")
            );
            logoImage.setImage(img);
            logoImage.setFitWidth(240);
            logoImage.setFitHeight(240);
            logoImage.setPreserveRatio(true);
            leftBox.getChildren().add(logoImage);
        } catch (Exception e) {

            javafx.scene.shape.Circle logoCircle = new javafx.scene.shape.Circle(80);
            logoCircle.setStyle("-fx-fill: linear-gradient(to bottom right, #7c3aed, #a855f7);");

            javafx.scene.control.Label logoIcon = new javafx.scene.control.Label("✂️");
            logoIcon.setStyle("-fx-font-size: 60px;");

            leftBox.getChildren().addAll(logoCircle, logoIcon);
        }

        // ПРАВАЯ ЧАСТЬ
        javafx.scene.layout.VBox rightBox = new javafx.scene.layout.VBox(20);
        rightBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        rightBox.setMaxWidth(500);
        rightBox.setStyle("-fx-padding: 20;");

        // Заголовок
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Welcome to Salon Management System");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #6b46c1; -fx-wrap-text: true;");
        titleLabel.setWrapText(true);

        // Описание
        javafx.scene.control.Label descLabel = new javafx.scene.control.Label(
                "A comprehensive solution for managing your salon business efficiently. " +
                        "Streamline appointments, track clients, manage employees, and boost your productivity."
        );
        descLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #666666; -fx-wrap-text: true; -fx-line-spacing: 5px;");
        descLabel.setWrapText(true);

        // Разделитель
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setMaxWidth(300);

        // Информация о пользователе
        User currentUser = SessionContext.getCurrentUser();
        javafx.scene.control.Label userLabel = new javafx.scene.control.Label(
                "Logged in as: " + (currentUser != null ? currentUser.getUsername() : "Guest")
        );
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999; -fx-font-style: italic;");

        // Список функций
        javafx.scene.layout.VBox featuresBox = new javafx.scene.layout.VBox(10);
        featuresBox.setStyle("-fx-padding: 10 0;");

        javafx.scene.control.Label featuresTitle = new javafx.scene.control.Label("Available Features:");
        featuresTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        String[] features = {
                "✓ Client Management - Track and manage your client database",
                "✓ Employee Management - Organize your team effectively",
                "✓ Service Catalog - Manage services and pricing",
                "✓ Appointment Scheduling - Book and manage appointments",
                "✓ Payment Tracking - Monitor financial transactions",
                "✓ Reports & Analytics - Gain insights into your business"
        };

        for (String feature : features) {
            javafx.scene.control.Label featureLabel = new javafx.scene.control.Label(feature);
            featureLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");
            featuresBox.getChildren().add(featureLabel);
        }

        // Инструкция
        javafx.scene.control.Label instructionLabel = new javafx.scene.control.Label(
                "👈 Select a menu item from the left to get started"
        );
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7c3aed; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        rightBox.getChildren().addAll(
                titleLabel,
                descLabel,
                separator,
                userLabel,
                featuresBox,
                instructionLabel
        );

        mainBox.getChildren().addAll(leftBox, rightBox);

        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(mainBox);

        // Сбросить активную кнопку
        clearActiveButton();
    }


    private void setActiveButton(Button button) {
        // Сбросить стиль предыдущей активной кнопки
        clearActiveButton();

        // Установить новую активную кнопку
        activeButton = button;
        activeButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2); " +
                        "-fx-text-fill: white; " +
                        "-fx-alignment: CENTER_LEFT; " +
                        "-fx-padding: 10 15; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5; " +
                        "-fx-font-weight: bold;"
        );
    }


    private void clearActiveButton() {
        if (activeButton != null) {
            activeButton.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-text-fill: white; " +
                            "-fx-alignment: CENTER_LEFT; " +
                            "-fx-padding: 10 15; " +
                            "-fx-font-size: 14px; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5;"
            );
            activeButton = null;
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}