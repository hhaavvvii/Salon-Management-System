package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class ClientsController {

    /* ===================== UI ===================== */

    @FXML private TableView<Client> clientsTable;
    @FXML private TextField searchField;

    @FXML private TableColumn<Client, Long> idColumn;
    @FXML private TableColumn<Client, String> firstNameColumn;
    @FXML private TableColumn<Client, String> lastNameColumn;
    @FXML private TableColumn<Client, String> phoneColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> notesColumn;

    /* ===================== DATA ===================== */

    private final ClientService clientService = new ClientService();
    private final ObservableList<Client> clients =
            FXCollections.observableArrayList();

    /* ===================== INIT ===================== */

    @FXML
    public void initialize() {
        configureColumns();
        configureTable();
        reloadClients();
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }

    private void configureTable() {
        clientsTable.setItems(clients);
        clientsTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
    }

    /* ===================== DATA LOAD ===================== */

    private void reloadClients() {
        clients.setAll(clientService.getAllClients());
    }

    /* ===================== ACTIONS ===================== */

    @FXML
    private void onAdd() {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Add client");

        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField phone = new TextField();
        TextField email = new TextField();
        TextArea notes = new TextArea();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("First name*:"), 0, 0);
        grid.add(firstName, 1, 0);

        grid.add(new Label("Last name:"), 0, 1);
        grid.add(lastName, 1, 1);

        grid.add(new Label("Phone*:"), 0, 2);
        grid.add(phone, 1, 2);

        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);

        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notes, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == save) {
                Client c = new Client();
                c.setFirstName(firstName.getText());
                c.setLastName(lastName.getText());
                c.setPhone(phone.getText());
                c.setEmail(email.getText());
                c.setNotes(notes.getText());
                return c;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(client -> {
            try {
                clientService.createClient(client);
                reloadClients();
            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        });
    }

    @FXML
    private void onDelete() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            clientService.deleteClient(selected.getId());
            reloadClients();
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText();

        if (query == null || query.isBlank()) {
            reloadClients();
        } else {
            clients.setAll(clientService.searchClients(query));
        }
    }

    @FXML
    private void onEdit() {
        // ШАГ 3 — реализуем дальше
    }

    /* ===================== UTIL ===================== */

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
