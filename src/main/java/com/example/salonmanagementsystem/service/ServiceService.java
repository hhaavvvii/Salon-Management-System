package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.ServiceDao;
import com.example.salonmanagementsystem.dao.impl.ServiceDaoImpl;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Service;

import java.util.List;

public class ServiceService {

    private final ServiceDao serviceDao = new ServiceDaoImpl();

    public List<Service> getAllServices() {
        return serviceDao.findAll();
    }

    public List<Service> getActiveServices() {
        return serviceDao.findActiveServices();
    }

    public List<Service> searchServices(String name, String category, Boolean active) {
        return serviceDao.findByFilters(name, category, active);
    }

    public void createService(Service service) {
        validateService(service);

        // Проверка уникальности (название + категория)
        if (serviceDao.serviceExists(service.getName(), service.getCategory(), null)) {
            throw new ValidationException(
                    "Service '" + service.getName() + "' in category '" + service.getCategory() + "' already exists"
            );
        }

        serviceDao.insert(service);
    }

    public void updateService(Service service) {
        if (service.getId() == null) {
            throw new ValidationException("Service ID is required for update");
        }

        validateService(service);

        // Проверка уникальности (исключая текущий сервис)
        if (serviceDao.serviceExists(service.getName(), service.getCategory(), service.getId())) {
            throw new ValidationException(
                    "Another service with name '" + service.getName() + "' in category '" + service.getCategory() + "' already exists"
            );
        }

        serviceDao.update(service);
    }

    public void deactivateService(long serviceId) {
        serviceDao.setInactive(serviceId);
    }

    private void validateService(Service service) {
        StringBuilder errors = new StringBuilder();

        // Проверка обязательных полей
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            errors.append("• Service name is required\n");
        }

        if (service.getCategory() == null || service.getCategory().trim().isEmpty()) {
            errors.append("• Category is required\n");
        }

        if (service.getPrice() == null || service.getPrice() <= 0) {
            errors.append("• Price must be greater than 0\n");
        }

        if (service.getDurationMinutes() == null || service.getDurationMinutes() <= 0) {
            errors.append("• Duration must be greater than 0 minutes\n");
        }

        // Валидация названия (буквы, цифры, пробелы, дефисы)
        if (service.getName() != null && !service.getName().matches("^[a-zA-Zа-яА-ЯёЁ0-9\\s-]+$")) {
            errors.append("• Service name can only contain letters, numbers, spaces and hyphens\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException("Validation failed:\n" + errors.toString());
        }
    }
}