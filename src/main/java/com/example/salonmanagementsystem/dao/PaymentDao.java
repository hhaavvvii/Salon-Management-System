package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.Payment;
import com.example.salonmanagementsystem.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentDao {
    List<Payment> findAll();
    Optional<Payment> findById(long id);
    Optional<Payment> findByAppointmentId(long appointmentId);
    void insert(Payment payment);
    void updateStatus(long id, PaymentStatus status, String comment);
    boolean existsByAppointmentId(long appointmentId);
}