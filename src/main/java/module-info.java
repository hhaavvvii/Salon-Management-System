module com.example.salonmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    exports com.example.salonmanagementsystem.app;

    opens com.example.salonmanagementsystem.app to javafx.fxml;
    opens com.example.salonmanagementsystem.controllers to javafx.fxml;
}
