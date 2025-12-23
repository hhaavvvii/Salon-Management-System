package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.Appointment;

import java.util.List;

public interface AppointmentDao {
    List<Appointment> findAll();
    List<Appointment> findByEmployee(long employeeId);
    void insert(Appointment a);
    boolean hasFutureAppointments(long clientId);
}
