package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.AppointmentDao;
import com.example.salonmanagementsystem.dao.ClientDao;
import com.example.salonmanagementsystem.dao.impl.AppointmentDaoImpl;
import com.example.salonmanagementsystem.dao.impl.ClientDaoImpl;
import com.example.salonmanagementsystem.exceptions.ServiceException;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Client;

import java.util.List;
import java.util.regex.Pattern;

public class ClientService {

    private final ClientDao clientDao = new ClientDaoImpl();

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{9,15}$");

    /* 1. Получение списка */
    public List<Client> getAllClients() {
        return clientDao.findAll();
    }

    /* 2. Создание клиента */
    public Client createClient(Client client) {
        if (client.getFirstName() == null || client.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (client.getPhone() == null || client.getPhone().isBlank()) {
            throw new ValidationException("Phone is required");
        }

        if (!client.getPhone().matches("^\\+?[0-9]{9,15}$")) {
            throw new ValidationException("Invalid phone format");
        }

        clientDao.findByPhone(client.getPhone()).ifPresent(c -> {
            throw new ValidationException("Client with this phone already exists");
        });

        clientDao.insert(client);
        return client;
    }



    /* 3. Обновление */
    public void updateClient(Client client) {
        if (client.getId() == null) {
            throw new ValidationException("Client id is required");
        }

        if (client.getFirstName() == null || client.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }

        if (client.getPhone() == null || client.getPhone().isBlank()) {
            throw new ValidationException("Phone is required");
        }

        if (!client.getPhone().matches("^\\+?[0-9]{9,15}$")) {
            throw new ValidationException("Invalid phone format");
        }

        clientDao.findById(client.getId())
                .orElseThrow(() -> new ValidationException("Client not found"));

        clientDao.findByPhone(client.getPhone())
                .filter(c -> !c.getId().equals(client.getId()))
                .ifPresent(c -> {
                    throw new ValidationException("Phone already used by another client");
                });

        clientDao.update(client);
    }


    /* 4. Удаление */
    private final AppointmentDao appointmentDao =
            new AppointmentDaoImpl();

    public void deleteClient(long clientId) {

        clientDao.findById(clientId)
                .orElseThrow(() -> new ValidationException("Client not found"));

        if (appointmentDao.hasFutureAppointments(clientId)) {
            throw new ValidationException(
                    "Cannot delete client with active or future appointments"
            );
        }

        clientDao.delete(clientId);
    }


    /* 5. Поиск */
    public List<Client> searchClients(String query) {
        String q = query.toLowerCase();

        return clientDao.findAll().stream()
                .filter(c ->
                        c.getFirstName().toLowerCase().contains(q) ||
                                c.getPhone().contains(q)
                )
                .toList();
    }

    /* ===== helpers ===== */

    private void validate(Client c) {
        if (c.getFirstName() == null || c.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (c.getPhone() == null || c.getPhone().isBlank()) {
            throw new ValidationException("Phone is required");
        }
        if (!PHONE_PATTERN.matcher(c.getPhone()).matches()) {
            throw new ValidationException("Invalid phone format");
        }
    }
}
