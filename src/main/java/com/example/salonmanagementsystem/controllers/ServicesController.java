package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.exceptions.AccessDeniedException;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Service;
import com.example.salonmanagementsystem.service.ServiceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

import java.util.List;

public class ServicesController {

    @FXML private ListView<Service> servicesListView;

    @FXML private TextField searchNameField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> statusFilter;

    @FXML private Label formTitle;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField priceField;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private ComboBox<String> statusBox;
    @FXML private Button saveButton;

    private final ServiceService serviceService = new ServiceService();
    private final ObservableList<Service> serviceData = FXCollections.observableArrayList();

    private Service selectedService = null;
    private boolean isEditMode = false;

    private static final String[] CATEGORIES = {
            "Haircut", "Coloring", "Styling", "Manicure", "Pedicure", "Facial", "Massage", "Makeup"
    };

    @FXML
    public void initialize() {
        setupListView();
        setupFilters();
        setupForm();
        loadData();
    }

    private void setupListView() {
        servicesListView.setItems(serviceData);

        // Modern transparent list styling
        servicesListView.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        servicesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Service service, boolean empty) {
                super.updateItem(service, empty);
                if (empty || service == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Main card container
                    VBox card = new VBox(12);
                    card.setPadding(new Insets(20));
                    card.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-border-color: #E5E7EB;" +
                                    "-fx-border-width: 1;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(139,92,246,0.06), 8, 0, 0, 2);" +
                                    "-fx-cursor: hand;"
                    );

                    // Header with name and status
                    HBox header = new HBox(12);
                    header.setAlignment(Pos.CENTER_LEFT);

                    Label name = new Label(service.getName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1F2937;");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Modern status badge
                    Label status = new Label(service.getStatus());
                    if (service.isActive()) {
                        status.setStyle(
                                "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;" +
                                        "-fx-background-radius: 12; -fx-padding: 4 12;" +
                                        "-fx-font-size: 11px; -fx-font-weight: 600;"
                        );
                    } else {
                        status.setStyle(
                                "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;" +
                                        "-fx-background-radius: 12; -fx-padding: 4 12;" +
                                        "-fx-font-size: 11px; -fx-font-weight: 600;"
                        );
                    }

                    header.getChildren().addAll(name, spacer, status);

                    // Info rows with icons
                    VBox info = new VBox(6);

                    Label category = new Label("📁 " + service.getCategory());
                    category.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

                    Label price = new Label(String.format("💰 %.2f ₸", service.getPrice()));
                    price.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563; -fx-font-weight: 600;");

                    Label duration = new Label("⏱️ " + service.getDurationMinutes() + " minutes");
                    duration.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

                    info.getChildren().addAll(category, price, duration);

                    card.getChildren().addAll(header, info);
                    setGraphic(card);

                    // Cell background styling
                    setStyle("-fx-background-color: transparent; -fx-padding: 6;");
                }
            }
        });

        servicesListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> selectedService = newSel
        );
    }

    private void setupFilters() {
        ObservableList<String> categories = FXCollections.observableArrayList("ALL");
        categories.addAll(CATEGORIES);
        categoryFilter.setItems(categories);
        categoryFilter.setValue("ALL");

        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("ALL");
    }

    private void setupForm() {
        categoryBox.setItems(FXCollections.observableArrayList(CATEGORIES));
        categoryBox.setEditable(true);

        durationSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 480, 60, 15)
        );

        statusBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusBox.setValue("ACTIVE");

        setAddMode();
    }

    private void loadData() {
        try {
            serviceData.setAll(serviceService.getAllServices());
        } catch (Exception e) {
            showError("Failed to load services: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        try {
            String name = searchNameField.getText();
            String category = "ALL".equals(categoryFilter.getValue()) ? null : categoryFilter.getValue();
            Boolean active = null;
            if (!"ALL".equals(statusFilter.getValue())) {
                active = "ACTIVE".equals(statusFilter.getValue());
            }

            List<Service> result = serviceService.searchServices(name, category, active);
            serviceData.setAll(result);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onReset() {
        searchNameField.clear();
        categoryFilter.setValue("ALL");
        statusFilter.setValue("ALL");
        loadData();
    }

    @FXML
    private void onSave() {
        if (SessionContext.isMaster()) {
            showError("Access denied: masters cannot create or edit services");
            return;
        }
        try {
            validateForm();

            Service s = isEditMode ? selectedService : new Service();
            s.setName(nameField.getText().trim());
            s.setCategory(categoryBox.getValue());
            s.setPrice(Double.parseDouble(priceField.getText().trim()));
            s.setDurationMinutes(durationSpinner.getValue());
            s.setActive("ACTIVE".equals(statusBox.getValue()));

            if (isEditMode) serviceService.updateService(s);
            else serviceService.createService(s);

            loadData();
            onClear();
            showSuccess(isEditMode ? "Service updated successfully" : "Service created successfully");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        if (SessionContext.isMaster()) {
            showError("Access denied: masters cannot edit services");
            return;
        }
        if (selectedService == null) {
            showError("Select a service first");
            return;
        }

        isEditMode = true;
        formTitle.setText("Edit Service");
        saveButton.setText("Update Service");
        saveButton.setStyle(
                "-fx-background-color: #F59E0B; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 14 24; " +
                        "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
        );

        nameField.setText(selectedService.getName());
        categoryBox.setValue(selectedService.getCategory());
        priceField.setText(String.format("%.2f", selectedService.getPrice()));
        durationSpinner.getValueFactory().setValue(selectedService.getDurationMinutes());
        statusBox.setValue(selectedService.getStatus());
    }

    @FXML
    private void onClear() {
        nameField.clear();
        categoryBox.setValue(null);
        priceField.clear();
        durationSpinner.getValueFactory().setValue(60);
        statusBox.setValue("ACTIVE");
        selectedService = null;
        setAddMode();
        servicesListView.getSelectionModel().clearSelection();
    }

    private void setAddMode() {
        isEditMode = false;
        formTitle.setText("Add New Service");
        saveButton.setText("Save Service");
        saveButton.setStyle(
                "-fx-background-color: #8B5CF6; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 14 24; " +
                        "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
        );
    }

    private void validateForm() throws ValidationException {
        if (nameField.getText().isEmpty() || categoryBox.getValue() == null || priceField.getText().isEmpty())
            throw new ValidationException("Fill all required fields");
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", "Operation Failed", message);
    }

    private void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Operation Completed", message);
    }

    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Warning", "Attention Required", message);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Modern alert styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white; " +
                        "-fx-font-family: 'Segoe UI', sans-serif;"
        );

        alert.showAndWait();
    }
}