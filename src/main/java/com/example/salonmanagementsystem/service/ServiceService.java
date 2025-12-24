package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.dao.ServiceDao;
import com.example.salonmanagementsystem.dao.impl.ServiceDaoImpl;
import com.example.salonmanagementsystem.exceptions.AccessDeniedException;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Service;

import java.util.List;
import java.util.Optional;

public class ServiceService {

    private final ServiceDao serviceDao = new ServiceDaoImpl();

    /**
     * Получить все услуги
     * MASTER и ADMIN видят все услуги (фильтрация не требуется)
     */
    public List<Service> getAllServices() {
        return serviceDao.findAll();
    }

    /**
     * Получить активные услуги
     */
    public List<Service> getActiveServices() {
        return serviceDao.findActiveServices();
    }

    /**
     * Поиск услуг
     */
    public List<Service> searchServices(String name, String category, Boolean active) {
        return serviceDao.findByFilters(name, category, active);
    }

    /**
     * Создать услугу
     * MASTER не может создавать услуги
     */
    public void createService(Service service) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot create services");
        }

        validateService(service);

        // Проверка уникальности (название + категория)
        if (serviceDao.serviceExists(service.getName(), service.getCategory(), null)) {
            throw new ValidationException(
                    "Service '" + service.getName() + "' in category '" + service.getCategory() + "' already exists"
            );
        }

        serviceDao.insert(service);
    }

    /**
     * Обновить услугу
     * MASTER не может обновлять услуги
     */
    public void updateService(Service service) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot edit services");
        }

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

    /**
     * Удалить услугу
     * MASTER не может удалять услуги
     */
    public void deleteService(long serviceId) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot delete services");
        }

        // Проверка существования услуги
        Optional<Service> service = serviceDao.findById(serviceId);
        if (service.isEmpty()) {
            throw new ValidationException("Service not found with ID: " + serviceId);
        }

        // Удаление услуги
        serviceDao.delete(serviceId);
    }

    /**
     * Деактивировать услугу
     * MASTER не может деактивировать услуги
     */
    public void deactivateService(long serviceId) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot deactivate services");
        }

        serviceDao.setInactive(serviceId);
    }

    /**
     * Активировать услугу
     * MASTER не может активировать услуги
     */
    public void activateService(long serviceId) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot activate services");
        }

        serviceDao.setActive(serviceId);
    }

    /**
     * Получить услугу по ID
     */
    public Optional<Service> getServiceById(long serviceId) {
        return serviceDao.findById(serviceId);
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