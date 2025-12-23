package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.ServiceItem;
import java.util.List;

public interface ServiceDao {
    List<ServiceItem> findAll();
}
