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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

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
                formTitle.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 20px;");
            }
            if (formContainer != null) {
                Label infoLabel = new Label("Read-only mode: you can only view clients from your appointments");
                infoLabel.setStyle(
                        "-fx-text-fill: #991B1B; -fx-font-weight: 600; -fx-padding: 12; " +
                                "-fx-background-color: #FEE2E2; -fx-background-radius: 8; -fx-font-size: 12px;"
                );
                infoLabel.setWrapText(true);
                infoLabel.setMaxWidth(Double.MAX_VALUE);
                formContainer.getChildren().add(0, infoLabel);
            }
        }
    }

    private void setupListView() {
        clientsListView.setItems(clientData);

        // Modern transparent list styling
        clientsListView.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        clientsListView.setCellFactory(lv -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty || client == null) {
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

                    Label name = new Label(client.getFirstName() + " " + client.getLastName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1F2937;");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Modern status badge
                    Label status = new Label(client.getStatus());
                    if ("ACTIVE".equals(client.getStatus())) {
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

                    Label phone = new Label("📞 " + client.getPhone());
                    phone.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

                    String emailText = (client.getEmail() != null && !client.getEmail().isEmpty())
                            ? client.getEmail()
                            : "No email provided";
                    Label email = new Label("✉️ " + emailText);
                    email.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

                    info.getChildren().addAll(phone, email);

                    card.getChildren().addAll(header, info);
                    setGraphic(card);

                    // Cell background styling
                    setStyle("-fx-background-color: transparent; -fx-padding: 6;");
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
        if (SessionContext.isMaster()) {
            showError("Access denied: masters cannot create or edit clients");
            return;
        }
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
            showSuccess(isEditMode ? "Client updated successfully" : "Client created successfully");

        } catch (ValidationException | AccessDeniedException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error saving client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateForm() throws ValidationException {
        StringBuilder errors = new StringBuilder();
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty())
            errors.append("• First name is required\n");
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty())
            errors.append("• Last name is required\n");
        if (phoneField.getText() == null || phoneField.getText().trim().isEmpty())
            errors.append("• Phone is required\n");
        if (errors.length() > 0)
            throw new ValidationException("Please fill all required fields:\n" + errors);
    }

    @FXML
    private void onEdit() {
        if (SessionContext.isMaster()) {
            showError("Access denied: masters cannot edit clients");
            return;
        }
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

    @FXML
    private void onDeactivate() {
        if (SessionContext.isMaster()) {
            showError("Access denied: masters cannot deactivate clients");
            return;
        }
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
                "Are you sure you want to deactivate " +
                        selectedClient.getFirstName() + " " + selectedClient.getLastName() + "?"
        );

        // Style the dialog
        DialogPane dialogPane = confirmation.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clientService.deactivateClient(selectedClient.getId());
                    loadData();
                    onClear();
                    showSuccess("Client deactivated successfully");
                }
                catch (Exception e) {
                    showError("Error deactivating client: " + e.getMessage());
                }
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
            saveButton.setStyle(
                    "-fx-background-color: #8B5CF6; -fx-text-fill: white; " +
                            "-fx-background-radius: 8; -fx-padding: 14 24; " +
                            "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
            );
        }
    }

    private void setEditMode() {
        isEditMode = true;
        if (!SessionContext.isMaster()) {
            formTitle.setText("Edit Client");
            saveButton.setText("Update Client");
            saveButton.setStyle(
                    "-fx-background-color: #F59E0B; -fx-text-fill: white; " +
                            "-fx-background-radius: 8; -fx-padding: 14 24; " +
                            "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
            );
        }
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