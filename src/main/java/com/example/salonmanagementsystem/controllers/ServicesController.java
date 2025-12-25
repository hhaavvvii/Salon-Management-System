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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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

        servicesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Service service, boolean empty) {
                super.updateItem(service, empty);
                if (empty || service == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(4);
                    card.setPadding(new Insets(10));
                    card.setStyle(
                            "-fx-background-color: #f3e8ff;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-border-color: #d9cfff;" +
                                    "-fx-border-width: 1;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4,0,0,1);"
                    );

                    Label name = new Label(service.getName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label category = new Label("Category: " + service.getCategory());
                    Label price = new Label(String.format("Price: %.2f ₸", service.getPrice()));
                    Label duration = new Label("Duration: " + service.getDurationMinutes() + " min");
                    Label status = new Label("Status: " + service.getStatus());
                    status.setStyle(service.isActive()
                            ? "-fx-text-fill: green; -fx-font-weight: bold;"
                            : "-fx-text-fill: red; -fx-font-weight: bold;");

                    card.getChildren().addAll(name, category, price, duration, status);
                    setGraphic(card);
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
            showError("Access denied");
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
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        if (SessionContext.isMaster()) { showError("Access denied"); return; }
        if (selectedService == null) { showError("Select a service first"); return; }

        isEditMode = true;
        formTitle.setText("Edit Service");
        saveButton.setText("Update Service");

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
    }

    private void validateForm() throws ValidationException {
        if (nameField.getText().isEmpty() || categoryBox.getValue() == null || priceField.getText().isEmpty())
            throw new ValidationException("Fill all required fields");
    }

    private void showError(String m) { new Alert(Alert.AlertType.ERROR, m).showAndWait(); }
}
