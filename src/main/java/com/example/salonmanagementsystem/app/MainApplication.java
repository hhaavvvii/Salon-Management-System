package com.example.salonmanagementsystem.app;

import com.example.salonmanagementsystem.controllers.LoginController;
import com.example.salonmanagementsystem.dao.UserDao;
import com.example.salonmanagementsystem.dao.impl.UserDaoImpl;
import com.example.salonmanagementsystem.service.AuthService;
import com.example.salonmanagementsystem.util.PasswordUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // backend
        UserDao userDao = new UserDaoImpl();
        PasswordUtil passwordUtil = new PasswordUtil();
        AuthService authService = new AuthService(userDao, passwordUtil);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setAuthService(authService);

        Scene scene = new Scene(root);

        stage.setTitle("Salon Management System");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(true);
        stage.show();
    }
}
