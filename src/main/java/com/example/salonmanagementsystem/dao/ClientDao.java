package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDao {
    List<Client> findAll();
    Optional<Client> findById(long id);
    Optional<Client> findByPhone(String phone);
    List<Client> findByFilters(String name, String phone, Boolean active);
    List<Client> findActiveClients();
    void insert(Client client);
    void update(Client client);
    void setInactive(long id);
    boolean phoneExists(String phone, Long excludeId);
}