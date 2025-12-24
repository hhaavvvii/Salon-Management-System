package com.example.salonmanagementsystem.model;

import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Long clientId;
    private Long employeeId;
    private Long serviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Double price;
    private AppointmentStatus status;
    private String comment;

    // Для отображения в таблице
    private String clientName;
    private String employeeName;
    private String serviceName;

    public Appointment() {
        this.status = AppointmentStatus.PLANNED;
    }

    public Appointment(Long clientId, Long employeeId, Long serviceId, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Display names
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", employeeId=" + employeeId +
                ", serviceId=" + serviceId +
                ", startTime=" + startTime +
                ", status=" + status +
                '}';
    }
}