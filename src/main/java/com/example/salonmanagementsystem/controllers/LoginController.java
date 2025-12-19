package com.example.salonmanagementsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginController {

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLogin() {
        statusLabel.setText("Login clicked");
    }
}
