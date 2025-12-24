package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.dao.AppointmentDao;
import com.example.salonmanagementsystem.dao.impl.AppointmentDaoImpl;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.AppointmentStatus;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {

    private final AppointmentDao dao = new AppointmentDaoImpl();

    public List<Appointment> getAppointments() {
        User u = SessionContext.getCurrentUser();
        if (u.getRole() == Role.ADMIN) {
            return dao.findAll();
        }
        if (u.getEmployeeId() == null) {
            return List.of();
        }
        return dao.findByEmployee(u.getEmployeeId());
    }

    public void create(Appointment a) {
        User u = SessionContext.getCurrentUser();

        if (u.getRole() == Role.MASTER) {
            a.setEmployeeId(u.getEmployeeId());
        }

        // Валидация
        validateAppointment(a);

        // Проверка на прошедшее время
        if (a.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot create appointment in the past");
        }

        // Проверка конфликта времени
        if (dao.hasTimeConflict(
                a.getEmployeeId(),
                a.getStartTime(),
                a.getEndTime())) {

            throw new ValidationException("Time slot is already occupied");
        }

        dao.insert(a);
    }

    public void update(Appointment a) {
        if (a.getId() == null) {
            throw new ValidationException("Appointment ID is required for update");
        }

        // Нельзя редактировать завершённые записи
        if (a.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ValidationException("Cannot edit completed appointments");
        }

        // Проверка конфликта времени (исключая текущую запись)
        if (dao.hasTimeConflict(a.getEmployeeId(), a.getStartTime(), a.getEndTime())) {
            throw new ValidationException("Time slot is already occupied");
        }

        dao.update(a);
    }

    public void cancelAppointment(long appointmentId) {
        dao.updateStatus(appointmentId, AppointmentStatus.CANCELED);
    }

    public void completeAppointment(long appointmentId) {
        dao.updateStatus(appointmentId, AppointmentStatus.COMPLETED);
    }

    private void validateAppointment(Appointment a) {
        if (a.getClientId() == null) {
            throw new ValidationException("Client is required");
        }

        if (a.getEmployeeId() == null) {
            throw new ValidationException("Employee is required");
        }

        if (a.getServiceId() == null) {
            throw new ValidationException("Service is required");
        }

        if (a.getStartTime() == null) {
            throw new ValidationException("Start time is required");
        }

        if (a.getEndTime() == null) {
            throw new ValidationException("End time is required");
        }

        if (a.getEndTime().isBefore(a.getStartTime()) || a.getEndTime().isEqual(a.getStartTime())) {
            throw new ValidationException("End time must be after start time");
        }
    }
}