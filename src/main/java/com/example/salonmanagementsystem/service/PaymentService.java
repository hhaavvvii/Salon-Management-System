package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.PaymentDao;
import com.example.salonmanagementsystem.dao.impl.PaymentDaoImpl;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Payment;
import com.example.salonmanagementsystem.model.PaymentStatus;

import java.util.List;

public class PaymentService {

    private final PaymentDao paymentDao = new PaymentDaoImpl();

    public List<Payment> getAllPayments() {
        return paymentDao.findAll();
    }

    public void createPayment(Payment payment) {
        validatePayment(payment);

        // Проверка: оплата уже существует для этой записи
        if (paymentDao.existsByAppointmentId(payment.getAppointmentId())) {
            throw new ValidationException(
                    "Payment for this appointment already exists"
            );
        }

        paymentDao.insert(payment);
    }

    public void refundPayment(long paymentId, String comment) {
        paymentDao.updateStatus(paymentId, PaymentStatus.REFUNDED, comment);
    }

    public boolean isAppointmentPaid(long appointmentId) {
        return paymentDao.existsByAppointmentId(appointmentId);
    }

    private void validatePayment(Payment payment) {
        if (payment.getAppointmentId() == null) {
            throw new ValidationException("Appointment is required");
        }

        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new ValidationException("Amount must be greater than 0");
        }

        if (payment.getPaymentMethod() == null) {
            throw new ValidationException("Payment method is required");
        }

        if (payment.getPaymentDate() == null) {
            throw new ValidationException("Payment date is required");
        }
    }
}