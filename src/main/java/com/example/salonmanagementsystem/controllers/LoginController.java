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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private RadioButton adminRadio;

    @FXML private RadioButton masterRadio;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;


    @FXML
    public void initialize() {
        this.authService = new AuthService(
                new UserDaoImpl(),
                new PasswordUtil()
        );

        SessionContext.clearSession();
    }



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

        // базовая UI-проверка
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            showError("Please enter username and password");
            return;
        }

        // роль выбирается для валидации, но не для установки
        Role selectedRole;
        if (adminRadio.isSelected()) {
            selectedRole = Role.ADMIN;
        } else if (masterRadio.isSelected()) {
            selectedRole = Role.MASTER;
        } else {
            showError("Please select role");
            return;
        }

        try {
            // 🔹 AuthService возвращает пользователя
            User user = authService.login(username, password, selectedRole);

            // 🔹 сохраняем пользователя в session
            SessionContext.setCurrentUser(user);

            errorLabel.setVisible(false);

            // 🔹 открываем dashboard по реальной роли пользователя
            openDashboard(user.getRole());

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
            stage.setResizable(true);
            stage.setMaximized(false);


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
