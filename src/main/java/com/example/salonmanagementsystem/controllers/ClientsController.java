package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.exceptions.AccessDeniedException;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class ClientsController {

    @FXML private ListView<Client> clientsListView;

    @FXML private TextField searchNameField;
    @FXML private TextField searchPhoneField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private Label formTitle;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea notesField;
    @FXML private Button saveButton;
    @FXML private VBox formContainer;

    private final ClientService clientService = new ClientService();
    private final ObservableList<Client> clientData = FXCollections.observableArrayList();

    private Client selectedClient = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupListView();
        setupFilters();
        setupForm();
        configureMasterMode();
        loadData();
    }

    private void configureMasterMode() {
        if (SessionContext.isMaster()) {
            if (saveButton != null) { saveButton.setVisible(false); saveButton.setManaged(false); }
            if (firstNameField != null) firstNameField.setEditable(false);
            if (lastNameField != null) lastNameField.setEditable(false);
            if (phoneField != null) phoneField.setEditable(false);
            if (emailField != null) emailField.setEditable(false);
            if (notesField != null) notesField.setEditable(false);
            if (formTitle != null) {
                formTitle.setText("Client Details (Read-Only)");
                formTitle.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            }
            if (formContainer != null) {
                Label infoLabel = new Label("Read-only mode: you can only view clients from your appointments");
                infoLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold; -fx-padding: 10; -fx-background-color: #f8d7da; -fx-background-radius: 5;");
                infoLabel.setWrapText(true);
                infoLabel.setMaxWidth(Double.MAX_VALUE);
                formContainer.getChildren().add(0, infoLabel);
            }
        }
    }

    private void setupListView() {
        clientsListView.setItems(clientData);

        clientsListView.setCellFactory(lv -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty || client == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(4);
                    card.setPadding(new Insets(10));
                    card.setStyle(
                            "-fx-background-color: linear-gradient(to right, #f3e8ff, #e0d4f8);" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-border-color: #d9cfff;" +
                                    "-fx-border-width: 1;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4,0,0,1);"
                    );

                    Label name = new Label(client.getFirstName() + " " + client.getLastName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label phone = new Label("Phone: " + client.getPhone());
                    Label email = new Label("Email: " + (client.getEmail() != null ? client.getEmail() : ""));
                    Label status = new Label("Status: " + client.getStatus());
                    if ("ACTIVE".equals(client.getStatus())) status.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    else status.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

                    card.getChildren().addAll(name, phone, email, status);
                    setGraphic(card);
                }
            }
        });

        clientsListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedClient = newSelection;
                    if (newSelection != null) fillFormWithClient(newSelection);
                }
        );
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("ALL");
    }

    private void setupForm() {
        setAddMode();
    }

    private void loadData() {
        try {
            clientData.setAll(clientService.getAllClients());
        } catch (Exception e) {
            showError("Failed to load clients: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        try {
            String name = searchNameField.getText();
            String phone = searchPhoneField.getText();
            String status = statusFilter.getValue();
            Boolean activeFilter = null;
            if (status != null && !"ALL".equals(status)) activeFilter = "ACTIVE".equals(status);
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

    @FXML
    private void onSave() {
        if (SessionContext.isMaster()) { showError("Access denied: masters cannot create or edit clients"); return; }
        try {
            validateForm();
            Client client = isEditMode ? selectedClient : new Client();
            client.setFirstName(firstNameField.getText().trim());
            client.setLastName(lastNameField.getText().trim());
            client.setPhone(phoneField.getText().trim());
            client.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
            client.setNotes(notesField.getText().trim().isEmpty() ? null : notesField.getText().trim());

            if (isEditMode) clientService.updateClient(client);
            else clientService.createClient(client);

            loadData();
            onClear();

        } catch (ValidationException | AccessDeniedException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error saving client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateForm() throws ValidationException {
        StringBuilder errors = new StringBuilder();
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) errors.append("• First name is required\n");
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) errors.append("• Last name is required\n");
        if (phoneField.getText() == null || phoneField.getText().trim().isEmpty()) errors.append("• Phone is required\n");
        if (errors.length() > 0) throw new ValidationException("Please fill all required fields:\n" + errors);
    }

    @FXML
    private void onEdit() {
        if (SessionContext.isMaster()) { showError("Access denied: masters cannot edit clients"); return; }
        if (selectedClient == null) { showError("Please select a client to edit"); return; }
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

    @FXML
    private void onDeactivate() {
        if (SessionContext.isMaster()) { showError("Access denied: masters cannot deactivate clients"); return; }
        if (selectedClient == null) { showWarning("Please select a client to deactivate"); return; }
        if (!selectedClient.isActive()) { showWarning("Client is already inactive"); return; }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deactivation");
        confirmation.setHeaderText("Deactivate Client");
        confirmation.setContentText("Are you sure you want to deactivate " + selectedClient.getFirstName() + " " + selectedClient.getLastName() + "?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try { clientService.deactivateClient(selectedClient.getId()); loadData(); onClear(); }
                catch (Exception e) { showError("Error deactivating client: " + e.getMessage()); }
            }
        });
    }

    @FXML
    private void onClear() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
        notesField.clear();
        selectedClient = null;
        setAddMode();
        clientsListView.getSelectionModel().clearSelection();
    }

    private void setAddMode() {
        isEditMode = false;
        if (!SessionContext.isMaster()) {
            formTitle.setText("Add New Client");
            saveButton.setText("Save Client");
            saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        }
    }

    private void setEditMode() {
        isEditMode = true;
        if (!SessionContext.isMaster()) {
            formTitle.setText("Edit Client");
            saveButton.setText("Update Client");
            saveButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        }
    }

    private void showError(String message) { showAlert(Alert.AlertType.ERROR, "Error", "Operation Failed", message); }
    private void showSuccess(String message) { showAlert(Alert.AlertType.INFORMATION, "Success", "Operation Completed", message); }
    private void showWarning(String message) { showAlert(Alert.AlertType.WARNING, "Warning", "Attention Required", message); }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
