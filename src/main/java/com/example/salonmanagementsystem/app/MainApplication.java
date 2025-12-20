package com.example.salonmanagementsystem.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Salon Management System");
        stage.setScene(scene);

        stage.setMaximized(true);
        stage.show();
    }
}
