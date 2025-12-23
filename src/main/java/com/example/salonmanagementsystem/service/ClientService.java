package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.ClientDao;
import com.example.salonmanagementsystem.dao.impl.ClientDaoImpl;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Client;

import java.util.List;

public class ClientService {

    private final ClientDao clientDao = new ClientDaoImpl();

    public List<Client> getAllClients() {
        return clientDao.findAll();
    }

    public List<Client> getActiveClients() {
        return clientDao.findActiveClients();
    }

    public List<Client> searchClients(String name, String phone, Boolean active) {
        return clientDao.findByFilters(name, phone, active);
    }

    public void createClient(Client client) {
        validateClient(client);

        // Проверка уникальности телефона
        if (clientDao.phoneExists(client.getPhone(), null)) {
            throw new ValidationException(
                    "Client with phone " + client.getPhone() + " already exists"
            );
        }

        clientDao.insert(client);
    }

    public void updateClient(Client client) {
        if (client.getId() == null) {
            throw new ValidationException("Client ID is required for update");
        }

        validateClient(client);

        // Проверка уникальности телефона (исключая текущего клиента)
        if (clientDao.phoneExists(client.getPhone(), client.getId())) {
            throw new ValidationException(
                    "Another client with phone " + client.getPhone() + " already exists"
            );
        }

        clientDao.update(client);
    }

    public void deactivateClient(long clientId) {
        clientDao.setInactive(clientId);
    }

    private void validateClient(Client client) {
        StringBuilder errors = new StringBuilder();

        // Проверка обязательных полей
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            errors.append("• First name is required\n");
        }

        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            errors.append("• Last name is required\n");
        }

        if (client.getPhone() == null || client.getPhone().trim().isEmpty()) {
            errors.append("• Phone is required\n");
        }

        // Валидация имени (только буквы и пробелы)
        if (client.getFirstName() != null && !client.getFirstName().matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            errors.append("• First name can only contain letters, spaces and hyphens\n");
        }

        if (client.getLastName() != null && !client.getLastName().matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            errors.append("• Last name can only contain letters, spaces and hyphens\n");
        }

        // Валидация телефона (базовая проверка)
        if (client.getPhone() != null && !client.getPhone().matches("^[+]?[0-9\\s\\-()]{7,20}$")) {
            errors.append("• Phone must be valid (7-20 digits, can include +, -, (), spaces)\n");
        }

        // Валидация email (если указан)
        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            if (!client.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                errors.append("• Email must be valid\n");
            }
        }

        if (errors.length() > 0) {
            throw new ValidationException("Validation failed:\n" + errors.toString());
        }
    }
}