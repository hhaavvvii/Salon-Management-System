package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;
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

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showError("Please enter username and password");
            return;
        }

        try {
            User user = authService.login(username, password);

            if (user == null) {
                showError("Invalid username or password");
                return;
            }

            openDashboard(user.getRole());

        } catch (Exception e) {
            showError("Login error. Please try again later");
            e.printStackTrace();
        }
    }

    private void openDashboard(Role role) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/dashboard.fxml")
        );

        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
