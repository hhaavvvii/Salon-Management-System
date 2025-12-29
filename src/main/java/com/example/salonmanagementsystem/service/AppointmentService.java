package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.dao.AppointmentDao;
import com.example.salonmanagementsystem.dao.impl.AppointmentDaoImpl;
import com.example.salonmanagementsystem.exceptions.AccessDeniedException;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentService {

    private final AppointmentDao appointmentDao = new AppointmentDaoImpl();


    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = appointmentDao.findAll();

        // Фильтрация для MASTER
        if (SessionContext.isMaster()) {
            Long currentEmployeeId = SessionContext.getCurrentEmployeeId();
            return appointments.stream()
                    .filter(a -> a.getEmployeeId().equals(currentEmployeeId))
                    .collect(Collectors.toList());
        }

        return appointments;
    }


    public List<Appointment> getAppointments() {
        return getAllAppointments();
    }


    public List<Appointment> getAppointmentsByEmployee(long employeeId) {
        // Проверка прав доступа для MASTER
        if (SessionContext.isMaster()) {
            Long currentEmployeeId = SessionContext.getCurrentEmployeeId();
            if (!currentEmployeeId.equals(employeeId)) {
                throw new AccessDeniedException("Access denied: cannot view other employee's appointments");
            }
        }

        return appointmentDao.findByEmployee(employeeId);
    }


    public void createAppointment(Appointment appointment) {
        // Для мастеров - проверяем что они создают запись только для себя
        if (SessionContext.isMaster()) {
            Long currentEmployeeId = SessionContext.getCurrentEmployeeId();
            if (!appointment.getEmployeeId().equals(currentEmployeeId)) {
                throw new AccessDeniedException("Access denied: you can only create appointments for yourself");
            }
        }

        validateAppointment(appointment);
        checkTimeConflict(appointment);
        appointmentDao.insert(appointment);
    }


    public void create(Appointment appointment) {
        createAppointment(appointment);
    }


    public void updateAppointment(Appointment appointment) {
        // Проверка прав доступа для MASTER
        if (SessionContext.isMaster()) {
            Long currentEmployeeId = SessionContext.getCurrentEmployeeId();
            if (!appointment.getEmployeeId().equals(currentEmployeeId)) {
                throw new AccessDeniedException("Access denied: cannot modify other employee's appointments");
            }
        }

        validateAppointment(appointment);
        appointmentDao.update(appointment);
    }


    public void updateStatus(long appointmentId, AppointmentStatus newStatus) {
        // Для MASTER проверяем, что это его запись
        if (SessionContext.isMaster()) {
            List<Appointment> allAppointments = appointmentDao.findAll();
            Appointment appointment = allAppointments.stream()
                    .filter(a -> a.getId().equals(appointmentId))
                    .findFirst()
                    .orElseThrow(() -> new ValidationException("Appointment not found"));

            Long currentEmployeeId = SessionContext.getCurrentEmployeeId();
            if (!appointment.getEmployeeId().equals(currentEmployeeId)) {
                throw new AccessDeniedException("Access denied: cannot change status of other employee's appointments");
            }

            // MASTER может менять только: PLANNED → COMPLETED или PLANNED → CANCELLED
            if (appointment.getStatus() != AppointmentStatus.PLANNED) {
                throw new ValidationException("Can only change status of PLANNED appointments");
            }

            if (newStatus != AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.CANCELLED) {
                throw new ValidationException("Can only change status to COMPLETED or CANCELLED");
            }
        }

        appointmentDao.updateStatus(appointmentId, newStatus);
    }


    public void completeAppointment(long appointmentId) {
        updateStatus(appointmentId, AppointmentStatus.COMPLETED);
    }


    public void cancelAppointment(long appointmentId) {
        updateStatus(appointmentId, AppointmentStatus.CANCELLED);
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment.getClientId() == null) {
            throw new ValidationException("Client is required");
        }
        if (appointment.getEmployeeId() == null) {
            throw new ValidationException("Employee is required");
        }
        if (appointment.getServiceId() == null) {
            throw new ValidationException("Service is required");
        }
        if (appointment.getStartTime() == null) {
            throw new ValidationException("Start time is required");
        }
        if (appointment.getEndTime() == null) {
            throw new ValidationException("End time is required");
        }
        if (appointment.getStartTime().isAfter(appointment.getEndTime())) {
            throw new ValidationException("Start time must be before end time");
        }
        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot create appointment in the past");
        }
    }

    private void checkTimeConflict(Appointment appointment) {
        boolean hasConflict = appointmentDao.hasTimeConflict(
                appointment.getEmployeeId(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        if (hasConflict) {
            throw new ValidationException(
                    "Time conflict: employee already has an appointment at this time"
            );
        }
    }
}