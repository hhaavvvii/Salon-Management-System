package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.dao.AppointmentDao;
import com.example.salonmanagementsystem.dao.ClientDao;
import com.example.salonmanagementsystem.dao.impl.AppointmentDaoImpl;
import com.example.salonmanagementsystem.dao.impl.ClientDaoImpl;
import com.example.salonmanagementsystem.exceptions.AccessDeniedException;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.Client;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientService {

    private final ClientDao clientDao = new ClientDaoImpl();
    private final AppointmentDao appointmentDao = new AppointmentDaoImpl();

    /**
     * Получить всех клиентов с учётом роли
     * ADMIN видит всех клиентов
     * MASTER видит только клиентов из своих записей
     */
    public List<Client> getAllClients() {
        List<Client> clients = clientDao.findAll();

        // Фильтрация для MASTER
        if (SessionContext.isMaster()) {
            return filterClientsForMaster(clients);
        }

        return clients;
    }

    /**
     * Получить активных клиентов с учётом роли
     */
    public List<Client> getActiveClients() {
        List<Client> clients = clientDao.findActiveClients();

        // Фильтрация для MASTER
        if (SessionContext.isMaster()) {
            return filterClientsForMaster(clients);
        }

        return clients;
    }

    /**
     * Поиск клиентов с учётом роли
     */
    public List<Client> searchClients(String name, String phone, Boolean active) {
        List<Client> clients = clientDao.findByFilters(name, phone, active);

        // Фильтрация для MASTER
        if (SessionContext.isMaster()) {
            return filterClientsForMaster(clients);
        }

        return clients;
    }

    /**
     * Создать клиента
     * MASTER не может создавать клиентов
     */
    public void createClient(Client client) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot create clients");
        }

        validateClient(client);

        // Проверка уникальности телефона
        if (clientDao.phoneExists(client.getPhone(), null)) {
            throw new ValidationException(
                    "Client with phone " + client.getPhone() + " already exists"
            );
        }

        clientDao.insert(client);
    }

    /**
     * Обновить клиента
     * MASTER не может редактировать клиентов
     */
    public void updateClient(Client client) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot edit clients");
        }

        if (client.getId() == null) {
            throw new ValidationException("Client ID is required for update");
        }

        validateClient(client);

        // Проверка уникальности телефона (исключая текущего клиента)
        if (clientDao.phoneExists(client.getPhone(), client.getId())) {
            throw new ValidationException(
                    "Another client with phone " + client.getPhone() + " already exists"
            );
        }

        clientDao.update(client);
    }

    /**
     * Деактивировать клиента
     * MASTER не может деактивировать клиентов
     */
    public void deactivateClient(long clientId) {
        // Проверка прав доступа
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot deactivate clients");
        }

        clientDao.setInactive(clientId);
    }

    /**
     * Фильтрация клиентов для MASTER
     * Оставляет только тех клиентов, у которых есть записи к данному мастеру
     */
    private List<Client> filterClientsForMaster(List<Client> clients) {
        Long currentEmployeeId = SessionContext.getCurrentEmployeeId();

        if (currentEmployeeId == null) {
            return List.of(); // Если нет employee_id, нет доступа
        }

        // Получить все записи мастера
        List<Appointment> masterAppointments = appointmentDao.findByEmployee(currentEmployeeId);

        // Собрать ID клиентов из записей
        Set<Long> clientIds = masterAppointments.stream()
                .map(Appointment::getClientId)
                .collect(Collectors.toSet());

        // Оставить только клиентов из записей мастера
        return clients.stream()
                .filter(client -> clientIds.contains(client.getId()))
                .collect(Collectors.toList());
    }

    private void validateClient(Client client) {
        StringBuilder errors = new StringBuilder();

        // Проверка обязательных полей
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            errors.append("• First name is required\n");
        }

        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            errors.append("• Last name is required\n");
        }

        if (client.getPhone() == null || client.getPhone().trim().isEmpty()) {
            errors.append("• Phone is required\n");
        }

        // Валидация имени (только буквы и пробелы)
        if (client.getFirstName() != null && !client.getFirstName().matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            errors.append("• First name can only contain letters, spaces and hyphens\n");
        }

        if (client.getLastName() != null && !client.getLastName().matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            errors.append("• Last name can only contain letters, spaces and hyphens\n");
        }

        // Валидация телефона (базовая проверка)
        if (client.getPhone() != null && !client.getPhone().matches("^[+]?[0-9\\s\\-()]{7,20}$")) {
            errors.append("• Phone must be valid (7-20 digits, can include +, -, (), spaces)\n");
        }

        // Валидация email (если указан)
        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            if (!client.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                errors.append("• Email must be valid\n");
            }
        }

        if (errors.length() > 0) {
            throw new ValidationException("Validation failed:\n" + errors.toString());
        }
    }
}