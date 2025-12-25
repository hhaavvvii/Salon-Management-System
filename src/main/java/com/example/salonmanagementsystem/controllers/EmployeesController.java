package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Employee;
import com.example.salonmanagementsystem.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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

        employeesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null) {
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

                    Label name = new Label(employee.getFirstName() + " " + employee.getLastName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label position = new Label("Position: " + employee.getPosition());
                    Label status = new Label("Status: " + employee.getStatus());
                    status.setStyle(employee.isActive()
                            ? "-fx-text-fill: green; -fx-font-weight: bold;"
                            : "-fx-text-fill: red; -fx-font-weight: bold;");

                    card.getChildren().addAll(name, position, status);
                    setGraphic(card);
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
        try {
            employeeService.deactivateEmployee(selectedEmployee.getId());
            loadData();
            onClear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
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
    }

    private void validateForm() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || positionBox.getValue() == null)
            throw new ValidationException("Fill all required fields");
    }

    private void showError(String m) { new Alert(Alert.AlertType.ERROR, m).showAndWait(); }
    private void showWarning(String m) { new Alert(Alert.AlertType.WARNING, m).showAndWait(); }
}
