package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.EmployeeDao;
import com.example.salonmanagementsystem.dao.impl.EmployeeDaoImpl;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Employee;

import java.util.List;

public class EmployeeService {

    private final EmployeeDao employeeDao = new EmployeeDaoImpl();

    public List<Employee> getAllEmployees() {
        return employeeDao.findAll();
    }

    public List<Employee> getActiveEmployees() {
        return employeeDao.findActiveEmployees();
    }

    public List<Employee> searchEmployees(String name, String position, Boolean active) {
        return employeeDao.findByFilters(name, position, active);
    }

    public void createEmployee(Employee employee) {
        validateEmployee(employee);
        employeeDao.insert(employee);
    }

    public void updateEmployee(Employee employee) {
        if (employee.getId() == null) {
            throw new ValidationException("Employee ID is required for update");
        }

        validateEmployee(employee);

        // Проверка: если деактивируем, проверяем наличие будущих записей
        if (!employee.isActive()) {
            if (employeeDao.hasFutureAppointments(employee.getId())) {
                throw new ValidationException(
                        "Cannot deactivate employee with future appointments.\n" +
                                "Please cancel or reassign appointments first."
                );
            }
        }

        employeeDao.update(employee);
    }

    public void deactivateEmployee(long employeeId) {
        if (employeeDao.hasFutureAppointments(employeeId)) {
            throw new ValidationException(
                    "Cannot deactivate employee with future appointments.\n" +
                            "Please cancel or reassign appointments first."
            );
        }

        employeeDao.setInactive(employeeId);
    }

    private void validateEmployee(Employee employee) {
        StringBuilder errors = new StringBuilder();

        // Проверка обязательных полей
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            errors.append("• First name is required\n");
        }

        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            errors.append("• Last name is required\n");
        }

        if (employee.getPosition() == null || employee.getPosition().trim().isEmpty()) {
            errors.append("• Position is required\n");
        }

        // Валидация имени (только буквы и пробелы)
        if (employee.getFirstName() != null && !employee.getFirstName().matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            errors.append("• First name can only contain letters, spaces and hyphens\n");
        }

        if (employee.getLastName() != null && !employee.getLastName().matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            errors.append("• Last name can only contain letters, spaces and hyphens\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException("Validation failed:\n" + errors.toString());
        }
    }
}