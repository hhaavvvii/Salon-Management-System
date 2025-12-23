package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.dao.ClientDao;
import com.example.salonmanagementsystem.dao.impl.ClientDaoImpl;
import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.model.Role;

import java.util.List;

public class ClientService {

    private final ClientDao clientDao = new ClientDaoImpl();

    public List<Client> getClients() {
        return clientDao.findAll();
    }

    public void addClient(Client client) {
        if (SessionContext.getCurrentUser().getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }
        clientDao.insert(client);
    }

    public void deleteClient(long id) {
        if (SessionContext.getCurrentUser().getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }
        clientDao.delete(id);
    }
}
