package com.example.salonmanagementsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Long appointmentId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private PaymentMethod method;

    public Payment(){}

    public Payment(Long appointmentId, BigDecimal amount, LocalDateTime createdAt, PaymentMethod method) {
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.method = method;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getAppointmentId() {return appointmentId;}
    public void setAppointmentId(Long appointmentId) {this.appointmentId = appointmentId;}

    public BigDecimal getAmount() {return amount;}
    public void setAmount(BigDecimal amount) {this.amount = amount;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public PaymentMethod getMethod() {return method;}
    public void setMethod(PaymentMethod method) {this.method = method;}

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", appointmentId=" + appointmentId +
                ", amount=" + amount +
                ", createdAt=" + createdAt + '\'' +
                ", method=" + method +
                '}';
    }
}
