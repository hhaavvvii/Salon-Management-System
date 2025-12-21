package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.exceptions.AuthException;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // AuthService ПЕРЕДАЁТСЯ извне (например, из Application)
    private AuthService authService;

    // setter-инъекция (подходит для JavaFX)
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // базовая UI-проверка (дублирование допустимо)
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            showError("Please enter username and password");
            return;
        }

        // TODO: получить роль из UI (RadioButton / ChoiceBox)
        Role selectedRole = Role.ADMIN; // временно, заменить на реальный выбор

        try {
            authService.login(username, password, selectedRole);

            errorLabel.setVisible(false);
            openDashboard(selectedRole);

        } catch (AuthException e) {
            showError(e.getMessage());
        }
    }

    private void openDashboard(Role role) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

        } catch (Exception e) {
            showError("Failed to open dashboard");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
