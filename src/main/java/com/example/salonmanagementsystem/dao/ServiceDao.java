package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceDao {
    List<Service> findAll();
    Optional<Service> findById(long id);
    List<Service> findByFilters(String name, String category, Boolean active);
    List<Service> findActiveServices();
    void insert(Service service);
    void update(Service service);
    void setInactive(long id);
    boolean serviceExists(String name, String category, Long excludeId);
}