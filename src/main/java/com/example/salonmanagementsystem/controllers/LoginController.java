package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.dao.impl.UserDaoImpl;
import com.example.salonmanagementsystem.exceptions.AuthException;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;
import com.example.salonmanagementsystem.service.AuthService;
import com.example.salonmanagementsystem.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML private VBox adminRoleBox;
    @FXML private VBox masterRoleBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService;
    private Role selectedRole = null;

    // Стили для выбранной и невыбранной роли
    private static final String ROLE_SELECTED_STYLE =
            "-fx-background-color: linear-gradient(to bottom right, #7c3aed, #a855f7); " +
                    "-fx-background-radius: 10; " +
                    "-fx-padding: 20 30; " +
                    "-fx-cursor: hand; " +
                    "-fx-border-color: #7c3aed; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 2;";

    private static final String ROLE_UNSELECTED_STYLE =
            "-fx-background-color: #f8f9fa; " +
                    "-fx-background-radius: 10; " +
                    "-fx-padding: 20 30; " +
                    "-fx-cursor: hand; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 2;";

    private static final String ROLE_HOVER_STYLE =
            "-fx-background-color: #e8e9f3; " +
                    "-fx-background-radius: 10; " +
                    "-fx-padding: 20 30; " +
                    "-fx-cursor: hand; " +
                    "-fx-border-color: #7c3aed; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 2;";

    @FXML
    public void initialize() {
        // Инициализация AuthService
        this.authService = new AuthService(
                new UserDaoImpl(),
                new PasswordUtil()
        );

        // Очистка сессии
        SessionContext.clearSession();

        // Настройка hover эффектов для выбора роли
        setupRoleHoverEffects();
    }

    /**
     * Настройка hover эффектов для кнопок выбора роли
     */
    private void setupRoleHoverEffects() {
        // Admin box hover
        adminRoleBox.setOnMouseEntered(e -> {
            if (selectedRole != Role.ADMIN) {
                adminRoleBox.setStyle(ROLE_HOVER_STYLE);
            }
        });

        adminRoleBox.setOnMouseExited(e -> {
            if (selectedRole != Role.ADMIN) {
                adminRoleBox.setStyle(ROLE_UNSELECTED_STYLE);
            }
        });

        // Master box hover
        masterRoleBox.setOnMouseEntered(e -> {
            if (selectedRole != Role.MASTER) {
                masterRoleBox.setStyle(ROLE_HOVER_STYLE);
            }
        });

        masterRoleBox.setOnMouseExited(e -> {
            if (selectedRole != Role.MASTER) {
                masterRoleBox.setStyle(ROLE_UNSELECTED_STYLE);
            }
        });
    }

    /**
     * Выбрать роль ADMIN
     */
    @FXML
    private void selectAdminRole() {
        selectedRole = Role.ADMIN;
        adminRoleBox.setStyle(ROLE_SELECTED_STYLE);
        masterRoleBox.setStyle(ROLE_UNSELECTED_STYLE);
        errorLabel.setVisible(false);
    }

    /**
     * Выбрать роль MASTER
     */
    @FXML
    private void selectMasterRole() {
        selectedRole = Role.MASTER;
        masterRoleBox.setStyle(ROLE_SELECTED_STYLE);
        adminRoleBox.setStyle(ROLE_UNSELECTED_STYLE);
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Проверка роли
        if (selectedRole == null) {
            showError("❌ Please select your role (Administrator or Master)");
            return;
        }

        // Проверка полей
        if (username == null || username.isBlank()) {
            showError("❌ Please enter your username");
            return;
        }

        if (password == null || password.isBlank()) {
            showError("❌ Please enter your password");
            return;
        }

        try {
            // Аутентификация через сервис
            User user = authService.login(username, password, selectedRole);

            // Сохранение пользователя в сессию
            SessionContext.setCurrentUser(user);

            errorLabel.setVisible(false);

            // Открытие Dashboard
            openDashboard();

        } catch (AuthException e) {
            showError("❌ " + e.getMessage());
        } catch (Exception e) {
            showError("❌ An error occurred during login. Please try again.");
            e.printStackTrace();
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Salon Management System - Dashboard");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);


        } catch (Exception e) {
            showError("❌ Failed to open dashboard");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}