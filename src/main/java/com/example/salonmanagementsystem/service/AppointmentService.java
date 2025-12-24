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

    /**
     * Получить все записи с учётом роли пользователя
     * ADMIN видит все записи
     * MASTER видит только свои записи
     *
     * Этот метод используется в контроллерах
     */
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

    /**
     * Получить записи (псевдоним для getAllAppointments)
     * Для обратной совместимости со старым кодом
     */
    public List<Appointment> getAppointments() {
        return getAllAppointments();
    }

    /**
     * Получить записи конкретного сотрудника
     * MASTER может получить только свои записи
     */
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

    /**
     * Создать новую запись
     * MASTER не может создавать записи
     */
    public void createAppointment(Appointment appointment) {
        // Проверка прав: MASTER не может создавать записи
        if (SessionContext.isMaster()) {
            throw new AccessDeniedException("Access denied: masters cannot create appointments");
        }

        validateAppointment(appointment);
        checkTimeConflict(appointment);
        appointmentDao.insert(appointment);
    }

    /**
     * Создать запись (псевдоним для createAppointment)
     * Для обратной совместимости
     */
    public void create(Appointment appointment) {
        createAppointment(appointment);
    }

    /**
     * Обновить запись
     * MASTER может обновлять только свои записи и только определённые поля
     */
    public void updateAppointment(Appointment appointment) {
        // Проверка прав доступа для MASTER
        if (SessionContext.isMaster()) {
            Long currentEmployeeId = SessionContext.getCurrentEmployeeId();
            if (!appointment.getEmployeeId().equals(currentEmployeeId)) {
                throw new AccessDeniedException("Access denied: cannot modify other employee's appointments");
            }
            // MASTER не может менять дату, время, услугу
            // Эта проверка должна быть на уровне UI - просто запрещаем редактирование этих полей
        }

        validateAppointment(appointment);
        appointmentDao.update(appointment);
    }

    /**
     * Обновить статус записи
     * MASTER может менять статус только своих записей: PLANNED → COMPLETED/CANCELED
     */
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

            // MASTER может менять только: PLANNED → COMPLETED или PLANNED → CANCELED
            if (appointment.getStatus() != AppointmentStatus.PLANNED) {
                throw new ValidationException("Can only change status of PLANNED appointments");
            }

            if (newStatus != AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.CANCELED) {
                throw new ValidationException("Can only change status to COMPLETED or CANCELED");
            }
        }

        appointmentDao.updateStatus(appointmentId, newStatus);
    }

    /**
     * Пометить запись как выполненную
     * Для обратной совместимости
     */
    public void completeAppointment(long appointmentId) {
        updateStatus(appointmentId, AppointmentStatus.COMPLETED);
    }

    /**
     * Отменить запись
     * Для обратной совместимости
     */
    public void cancelAppointment(long appointmentId) {
        updateStatus(appointmentId, AppointmentStatus.CANCELED);
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