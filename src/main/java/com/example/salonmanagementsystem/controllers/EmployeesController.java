package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Employee;
import com.example.salonmanagementsystem.service.EmployeeService;
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

public class EmployeesController {

    @FXML private ListView<Employee> employeesListView;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> positionFilter;
    @FXML private ComboBox<String> statusFilter;

    @FXML private Label formTitle;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> positionBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private Button saveButton;

    private final EmployeeService employeeService = new EmployeeService();
    private final ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    private Employee selectedEmployee;
    private boolean isEditMode = false;

    private static final String[] POSITIONS = {
            "Master", "Administrator", "Manager", "Receptionist", "Stylist", "Colorist"
    };

    @FXML
    public void initialize() {
        setupListView();
        setupFilters();
        setupForm();
        loadData();
    }

    private void setupListView() {
        employeesListView.setItems(employeeData);

        // Modern transparent list styling
        employeesListView.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        employeesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null) {
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

                    Label name = new Label(employee.getFirstName() + " " + employee.getLastName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1F2937;");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Modern status badge
                    Label status = new Label(employee.getStatus());
                    if (employee.isActive()) {
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

                    // Info row with position icon
                    VBox info = new VBox(6);

                    Label position = new Label("💼 " + employee.getPosition());
                    position.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

                    info.getChildren().add(position);

                    card.getChildren().addAll(header, info);
                    setGraphic(card);

                    // Cell background styling
                    setStyle("-fx-background-color: transparent; -fx-padding: 6;");
                }
            }
        });

        employeesListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> selectedEmployee = newSel
        );
    }

    private void setupFilters() {
        positionFilter.setItems(FXCollections.observableArrayList(
                "ALL", "Master", "Administrator", "Manager", "Receptionist", "Stylist", "Colorist"
        ));
        positionFilter.setValue("ALL");

        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("ALL");
    }

    private void setupForm() {
        positionBox.setItems(FXCollections.observableArrayList(POSITIONS));
        statusBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusBox.setValue("ACTIVE");
        setAddMode();
    }

    private void loadData() {
        try {
            employeeData.setAll(employeeService.getAllEmployees());
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        try {
            String name = searchField.getText();
            String position = "ALL".equals(positionFilter.getValue()) ? null : positionFilter.getValue();
            Boolean active = "ALL".equals(statusFilter.getValue()) ? null : "ACTIVE".equals(statusFilter.getValue());

            List<Employee> result = employeeService.searchEmployees(name, position, active);
            employeeData.setAll(result);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onReset() {
        searchField.clear();
        positionFilter.setValue("ALL");
        statusFilter.setValue("ALL");
        loadData();
    }

    @FXML
    private void onSave() {
        try {
            validateForm();

            Employee e = isEditMode ? selectedEmployee : new Employee();
            e.setFirstName(firstNameField.getText().trim());
            e.setLastName(lastNameField.getText().trim());
            e.setPosition(positionBox.getValue());
            e.setActive("ACTIVE".equals(statusBox.getValue()));

            if (isEditMode) employeeService.updateEmployee(e);
            else employeeService.createEmployee(e);

            loadData();
            onClear();
            showSuccess(isEditMode ? "Employee updated successfully" : "Employee created successfully");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        if (selectedEmployee == null) {
            showWarning("Select an employee first");
            return;
        }
        isEditMode = true;
        formTitle.setText("Edit Employee");
        saveButton.setText("Update Employee");
        saveButton.setStyle(
                "-fx-background-color: #F59E0B; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 14 24; " +
                        "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
        );

        firstNameField.setText(selectedEmployee.getFirstName());
        lastNameField.setText(selectedEmployee.getLastName());
        positionBox.setValue(selectedEmployee.getPosition());
        statusBox.setValue(selectedEmployee.getStatus());
    }

    @FXML
    private void onDeactivate() {
        if (selectedEmployee == null) {
            showWarning("Select an employee first");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deactivation");
        confirmation.setHeaderText("Deactivate Employee");
        confirmation.setContentText(
                "Are you sure you want to deactivate " +
                        selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + "?"
        );

        // Style the dialog
        DialogPane dialogPane = confirmation.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    employeeService.deactivateEmployee(selectedEmployee.getId());
                    loadData();
                    onClear();
                    showSuccess("Employee deactivated successfully");
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onClear() {
        firstNameField.clear();
        lastNameField.clear();
        positionBox.setValue(null);
        statusBox.setValue("ACTIVE");
        selectedEmployee = null;
        setAddMode();
        employeesListView.getSelectionModel().clearSelection();
    }

    private void setAddMode() {
        isEditMode = false;
        formTitle.setText("Add New Employee");
        saveButton.setText("Save Employee");
        saveButton.setStyle(
                "-fx-background-color: #8B5CF6; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 14 24; " +
                        "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
        );
    }

    private void validateForm() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || positionBox.getValue() == null)
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