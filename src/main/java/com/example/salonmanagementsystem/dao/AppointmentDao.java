package com.example.salonmanagementsystem.dao;

public interface AppointmentDao {

    boolean hasFutureAppointments(long clientId);
}
