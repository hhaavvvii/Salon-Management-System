package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.ServiceDao;
import com.example.salonmanagementsystem.dao.impl.ServiceDaoImpl;
import com.example.salonmanagementsystem.model.ServiceItem;

import java.util.List;

public class ServiceService {

    private final ServiceDao dao = new ServiceDaoImpl();

    public List<ServiceItem> getAllServices() {
        return dao.findAll();
    }
}
