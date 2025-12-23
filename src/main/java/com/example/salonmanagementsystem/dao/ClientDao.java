package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.Client;
import java.util.List;

public interface ClientDao {
    List<Client> findAll();
    void insert(Client client);
    void delete(long id);
}
