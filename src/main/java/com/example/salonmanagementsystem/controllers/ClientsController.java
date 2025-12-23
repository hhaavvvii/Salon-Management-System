package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.service.ClientService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientsController {

    @FXML
    private TableView<Client> clientsTable;

    @FXML private TableColumn<Client, Long> idColumn;
    @FXML private TableColumn<Client, String> nameColumn;
    @FXML private TableColumn<Client, String> phoneColumn;
    @FXML private TableColumn<Client, String> emailColumn;

    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        System.out.println("ClientsController initialized");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        refresh();
    }

    private void refresh() {
        clientsTable.setItems(
                FXCollections.observableArrayList(
                        clientService.getClients()
                )
        );
    }

    @FXML
    private void onDelete() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            clientService.deleteClient(selected.getId());
            refresh();
        }
    }

    @FXML
    private void onAdd() {
        System.out.println("Add clicked (not implemented)");
    }

    @FXML
    private void onEdit() {
        System.out.println("Edit clicked (not implemented)");
    }
}
