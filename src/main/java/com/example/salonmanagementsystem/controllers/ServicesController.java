package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Service;
import com.example.salonmanagementsystem.service.ServiceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ServicesController {

    // Таблица
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, String> idCol;
    @FXML private TableColumn<Service, String> nameCol;
    @FXML private TableColumn<Service, String> categoryCol;
    @FXML private TableColumn<Service, String> priceCol;
    @FXML private TableColumn<Service, String> durationCol;
    @FXML private TableColumn<Service, String> statusCol;

    // Фильтры
    @FXML private TextField searchNameField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> statusFilter;

    // Форма
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

    // Категории услуг
    private static final String[] CATEGORIES = {
            "Haircut", "Coloring", "Styling", "Manicure", "Pedicure", "Facial", "Massage", "Makeup"
    };

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        setupForm();
        loadData();
    }

    private void setupTable() {
        // Настройка колонок
        idCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId()))
        );

        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName())
        );

        categoryCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory())
        );

        priceCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().getPrice()))
        );

        durationCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getDurationMinutes()))
        );

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus())
        );

        // Цветовое выделение статуса
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("ACTIVE")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        servicesTable.setItems(serviceData);

        // Обработка выбора строки
        servicesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedService = newSelection;
                }
        );
    }

    private void setupFilters() {
        // Заполнение фильтра категорий
        ObservableList<String> categories = FXCollections.observableArrayList("ALL");
        categories.addAll(CATEGORIES);
        categoryFilter.setItems(categories);
        categoryFilter.setValue("ALL");

        // Заполнение фильтра статусов
        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("ALL");
    }

    private void setupForm() {
        // Заполнение ComboBox категорий (с возможностью ввода своей)
        categoryBox.setItems(FXCollections.observableArrayList(CATEGORIES));

        // Spinner для длительности (15-480 минут, шаг 15)
        durationSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 480, 60, 15)
        );

        // Заполнение ComboBox статусов
        statusBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusBox.setValue("ACTIVE");

        // Начальное состояние - режим добавления
        setAddMode();
    }

    private void loadData() {
        try {
            serviceData.setAll(serviceService.getAllServices());
        } catch (Exception e) {
            showError("Failed to load services: " + e.getMessage());
        }
    }

    /* ==========================================================
       SEARCH AND FILTER
       ========================================================== */

    @FXML
    private void onSearch() {
        try {
            String name = searchNameField.getText();
            String category = categoryFilter.getValue();
            String status = statusFilter.getValue();

            Boolean activeFilter = null;
            if (status != null && !status.equals("ALL")) {
                activeFilter = status.equals("ACTIVE");
            }

            String categoryValue = (category != null && !category.equals("ALL")) ? category : null;

            List<Service> results = serviceService.searchServices(name, categoryValue, activeFilter);
            serviceData.setAll(results);

        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void onReset() {
        searchNameField.clear();
        categoryFilter.setValue("ALL");
        statusFilter.setValue("ALL");
        loadData();
    }

    /* ==========================================================
       SAVE (CREATE OR UPDATE)
       ========================================================== */

    @FXML
    private void onSave() {
        try {
            validateForm();

            Service service = isEditMode ? selectedService : new Service();

            service.setName(nameField.getText().trim());
            service.setCategory(categoryBox.getValue().trim());

            // Парсинг цены
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                service.setPrice(price);
            } catch (NumberFormatException e) {
                throw new ValidationException("Price must be a valid number");
            }

            service.setDurationMinutes(durationSpinner.getValue());
            service.setActive(statusBox.getValue().equals("ACTIVE"));

            if (isEditMode) {
                serviceService.updateService(service);
                showSuccess("Service updated successfully!");
            } else {
                serviceService.createService(service);
                showSuccess("Service created successfully!");
            }

            loadData();
            onClear();

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error saving service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("• Service name is required\n");
        }

        if (categoryBox.getValue() == null || categoryBox.getValue().trim().isEmpty()) {
            errors.append("• Category is required\n");
        }

        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
            errors.append("• Price is required\n");
        }

        if (statusBox.getValue() == null) {
            errors.append("• Status is required\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException("Please fill all required fields:\n" + errors.toString());
        }
    }

    /* ==========================================================
       EDIT
       ========================================================== */

    @FXML
    private void onEdit() {
        if (selectedService == null) {
            showWarning("Please select a service to edit");
            return;
        }

        setEditMode();
        fillFormWithService(selectedService);
    }

    private void fillFormWithService(Service service) {
        nameField.setText(service.getName());
        categoryBox.setValue(service.getCategory());
        priceField.setText(String.format("%.2f", service.getPrice()));
        durationSpinner.getValueFactory().setValue(service.getDurationMinutes());
        statusBox.setValue(service.getStatus());
    }

    /* ==========================================================
       DEACTIVATE
       ========================================================== */

    @FXML
    private void onDeactivate() {
        if (selectedService == null) {
            showWarning("Please select a service to deactivate");
            return;
        }

        if (!selectedService.isActive()) {
            showWarning("Service is already inactive");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deactivation");
        confirmation.setHeaderText("Deactivate Service");
        confirmation.setContentText(
                "Are you sure you want to deactivate '" + selectedService.getName() + "'?\n" +
                        "This service will not be available for new appointments."
        );

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceService.deactivateService(selectedService.getId());
                    showSuccess("Service deactivated successfully!");
                    loadData();
                    onClear();
                } catch (Exception e) {
                    showError("Error deactivating service: " + e.getMessage());
                }
            }
        });
    }

    /* ==========================================================
       CLEAR FORM
       ========================================================== */

    @FXML
    private void onClear() {
        nameField.clear();
        categoryBox.setValue(null);
        priceField.clear();
        durationSpinner.getValueFactory().setValue(60);
        statusBox.setValue("ACTIVE");

        selectedService = null;
        setAddMode();
        servicesTable.getSelectionModel().clearSelection();
    }

    /* ==========================================================
       UI STATE MANAGEMENT
       ========================================================== */

    private void setAddMode() {
        isEditMode = false;
        formTitle.setText("Add New Service");
        saveButton.setText("Save Service");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
    }

    private void setEditMode() {
        isEditMode = true;
        formTitle.setText("Edit Service");
        saveButton.setText("Update Service");
        saveButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10;");
    }

    /* ==========================================================
       ALERT DIALOGS
       ========================================================== */

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Operation Completed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Attention Required");
        alert.setContentText(message);
        alert.showAndWait();
    }
}