package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Employee;
import com.example.salonmanagementsystem.service.EmployeeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class EmployeesController {

    // Таблица
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, String> idCol;
    @FXML private TableColumn<Employee, String> firstNameCol;
    @FXML private TableColumn<Employee, String> lastNameCol;
    @FXML private TableColumn<Employee, String> positionCol;
    @FXML private TableColumn<Employee, String> statusCol;

    // Фильтры
    @FXML private TextField searchField;
    @FXML private ComboBox<String> positionFilter;
    @FXML private ComboBox<String> statusFilter;

    // Форма
    @FXML private Label formTitle;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> positionBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private Button saveButton;

    private final EmployeeService employeeService = new EmployeeService();
    private final ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    private Employee selectedEmployee = null;
    private boolean isEditMode = false;

    // Позиции сотрудников
    private static final String[] POSITIONS = {
            "Master", "Administrator", "Manager", "Receptionist", "Stylist", "Colorist"
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

        firstNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFirstName())
        );

        lastNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLastName())
        );

        positionCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPosition())
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

        employeesTable.setItems(employeeData);

        // Обработка выбора строки
        employeesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedEmployee = newSelection;
                }
        );
    }

    private void setupFilters() {
        // Заполнение фильтра позиций
        positionFilter.setItems(FXCollections.observableArrayList("ALL", "Master", "Administrator", "Manager", "Receptionist", "Stylist", "Colorist"));
        positionFilter.setValue("ALL");

        // Заполнение фильтра статусов
        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("ALL");
    }

    private void setupForm() {
        // Заполнение ComboBox позиций
        positionBox.setItems(FXCollections.observableArrayList(POSITIONS));

        // Заполнение ComboBox статусов
        statusBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusBox.setValue("ACTIVE");

        // Начальное состояние - режим добавления
        setAddMode();
    }

    private void loadData() {
        try {
            employeeData.setAll(employeeService.getAllEmployees());
        } catch (Exception e) {
            showError("Failed to load employees: " + e.getMessage());
        }
    }

    /* ==========================================================
       SEARCH AND FILTER
       ========================================================== */

    @FXML
    private void onSearch() {
        try {
            String name = searchField.getText();
            String position = positionFilter.getValue();
            String status = statusFilter.getValue();

            Boolean activeFilter = null;
            if (status != null && !status.equals("ALL")) {
                activeFilter = status.equals("ACTIVE");
            }

            String positionValue = (position != null && !position.equals("ALL")) ? position : null;

            List<Employee> results = employeeService.searchEmployees(name, positionValue, activeFilter);
            employeeData.setAll(results);

        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void onReset() {
        searchField.clear();
        positionFilter.setValue("ALL");
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

            Employee employee = isEditMode ? selectedEmployee : new Employee();

            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setPosition(positionBox.getValue());
            employee.setActive(statusBox.getValue().equals("ACTIVE"));

            if (isEditMode) {
                employeeService.updateEmployee(employee);
                showSuccess("Employee updated successfully!");
            } else {
                employeeService.createEmployee(employee);
                showSuccess("Employee created successfully!");
            }

            loadData();
            onClear();

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error saving employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateForm() {
        StringBuilder errors = new StringBuilder();

        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            errors.append("• First name is required\n");
        }

        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            errors.append("• Last name is required\n");
        }

        if (positionBox.getValue() == null) {
            errors.append("• Position is required\n");
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
        if (selectedEmployee == null) {
            showWarning("Please select an employee to edit");
            return;
        }

        setEditMode();
        fillFormWithEmployee(selectedEmployee);
    }

    private void fillFormWithEmployee(Employee employee) {
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        positionBox.setValue(employee.getPosition());
        statusBox.setValue(employee.getStatus());
    }

    /* ==========================================================
       DEACTIVATE
       ========================================================== */

    @FXML
    private void onDeactivate() {
        if (selectedEmployee == null) {
            showWarning("Please select an employee to deactivate");
            return;
        }

        if (!selectedEmployee.isActive()) {
            showWarning("Employee is already inactive");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deactivation");
        confirmation.setHeaderText("Deactivate Employee");
        confirmation.setContentText(
                "Are you sure you want to deactivate " + selectedEmployee.getFullName() + "?\n" +
                        "This employee will not be available for new appointments."
        );

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    employeeService.deactivateEmployee(selectedEmployee.getId());
                    showSuccess("Employee deactivated successfully!");
                    loadData();
                    onClear();
                } catch (ValidationException e) {
                    showError(e.getMessage());
                } catch (Exception e) {
                    showError("Error deactivating employee: " + e.getMessage());
                }
            }
        });
    }

    /* ==========================================================
       CLEAR FORM
       ========================================================== */

    @FXML
    private void onClear() {
        firstNameField.clear();
        lastNameField.clear();
        positionBox.setValue(null);
        statusBox.setValue("ACTIVE");

        selectedEmployee = null;
        setAddMode();
        employeesTable.getSelectionModel().clearSelection();
    }

    /* ==========================================================
       UI STATE MANAGEMENT
       ========================================================== */

    private void setAddMode() {
        isEditMode = false;
        formTitle.setText("Add New Employee");
        saveButton.setText("Save Employee");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
    }

    private void setEditMode() {
        isEditMode = true;
        formTitle.setText("Edit Employee");
        saveButton.setText("Update Employee");
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