package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.service.ClientService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ClientsController {

    // Таблица
    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, String> idCol;
    @FXML private TableColumn<Client, String> firstNameCol;
    @FXML private TableColumn<Client, String> lastNameCol;
    @FXML private TableColumn<Client, String> phoneCol;
    @FXML private TableColumn<Client, String> emailCol;
    @FXML private TableColumn<Client, String> statusCol;

    // Фильтры
    @FXML private TextField searchNameField;
    @FXML private TextField searchPhoneField;
    @FXML private ComboBox<String> statusFilter;

    // Форма
    @FXML private Label formTitle;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea notesField;
    @FXML private Button saveButton;

    private final ClientService clientService = new ClientService();
    private final ObservableList<Client> clientData = FXCollections.observableArrayList();

    private Client selectedClient = null;
    private boolean isEditMode = false;

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

        phoneCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhone())
        );

        emailCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail() != null ? data.getValue().getEmail() : "")
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

        clientsTable.setItems(clientData);

        // Обработка выбора строки
        clientsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedClient = newSelection;
                }
        );
    }

    private void setupFilters() {
        // Заполнение фильтра статусов
        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("ALL");
    }

    private void setupForm() {
        // Начальное состояние - режим добавления
        setAddMode();
    }

    private void loadData() {
        try {
            clientData.setAll(clientService.getAllClients());
        } catch (Exception e) {
            showError("Failed to load clients: " + e.getMessage());
        }
    }

    /* ==========================================================
       SEARCH AND FILTER
       ========================================================== */

    @FXML
    private void onSearch() {
        try {
            String name = searchNameField.getText();
            String phone = searchPhoneField.getText();
            String status = statusFilter.getValue();

            Boolean activeFilter = null;
            if (status != null && !status.equals("ALL")) {
                activeFilter = status.equals("ACTIVE");
            }

            List<Client> results = clientService.searchClients(name, phone, activeFilter);
            clientData.setAll(results);

        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void onReset() {
        searchNameField.clear();
        searchPhoneField.clear();
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

            Client client = isEditMode ? selectedClient : new Client();

            client.setFirstName(firstNameField.getText().trim());
            client.setLastName(lastNameField.getText().trim());
            client.setPhone(phoneField.getText().trim());
            client.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
            client.setNotes(notesField.getText().trim().isEmpty() ? null : notesField.getText().trim());

            if (isEditMode) {
                clientService.updateClient(client);
                showSuccess("Client updated successfully!");
            } else {
                clientService.createClient(client);
                showSuccess("Client created successfully!");
            }

            loadData();
            onClear();

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error saving client: " + e.getMessage());
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

        if (phoneField.getText() == null || phoneField.getText().trim().isEmpty()) {
            errors.append("• Phone is required\n");
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
        if (selectedClient == null) {
            showWarning("Please select a client to edit");
            return;
        }

        setEditMode();
        fillFormWithClient(selectedClient);
    }

    private void fillFormWithClient(Client client) {
        firstNameField.setText(client.getFirstName());
        lastNameField.setText(client.getLastName());
        phoneField.setText(client.getPhone());
        emailField.setText(client.getEmail() != null ? client.getEmail() : "");
        notesField.setText(client.getNotes() != null ? client.getNotes() : "");
    }

    /* ==========================================================
       DEACTIVATE
       ========================================================== */

    @FXML
    private void onDeactivate() {
        if (selectedClient == null) {
            showWarning("Please select a client to deactivate");
            return;
        }

        if (!selectedClient.isActive()) {
            showWarning("Client is already inactive");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deactivation");
        confirmation.setHeaderText("Deactivate Client");
        confirmation.setContentText(
                "Are you sure you want to deactivate " + selectedClient.getFullName() + "?\n" +
                        "This client will not be available for new appointments."
        );

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clientService.deactivateClient(selectedClient.getId());
                    showSuccess("Client deactivated successfully!");
                    loadData();
                    onClear();
                } catch (Exception e) {
                    showError("Error deactivating client: " + e.getMessage());
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
        phoneField.clear();
        emailField.clear();
        notesField.clear();

        selectedClient = null;
        setAddMode();
        clientsTable.getSelectionModel().clearSelection();
    }

    /* ==========================================================
       UI STATE MANAGEMENT
       ========================================================== */

    private void setAddMode() {
        isEditMode = false;
        formTitle.setText("Add New Client");
        saveButton.setText("Save Client");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
    }

    private void setEditMode() {
        isEditMode = true;
        formTitle.setText("Edit Client");
        saveButton.setText("Update Client");
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