package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeDao {
    List<Employee> findAll();
    Optional<Employee> findById(long id);
    List<Employee> findByFilters(String name, String position, Boolean active);
    List<Employee> findActiveEmployees();
    void insert(Employee employee);
    void update(Employee employee);
    void setInactive(long id);
    boolean hasFutureAppointments(long employeeId);
}