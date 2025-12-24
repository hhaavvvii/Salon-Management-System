package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentDao {
    List<Appointment> findAll();
    List<Appointment> findByEmployee(long employeeId);
    void insert(Appointment a);
    void update(Appointment a);
    void updateStatus(long id, AppointmentStatus status);
    boolean hasFutureAppointments(long clientId);
    boolean hasTimeConflict(long employeeId, LocalDateTime start, LocalDateTime end);
}