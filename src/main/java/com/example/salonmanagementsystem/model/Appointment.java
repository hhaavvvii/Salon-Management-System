package com.example.salonmanagementsystem.model;

import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Long clientId;
    private Long employeeId;
    private Long serviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;

    public Appointment() {}

    public Appointment(Long clientId, Long employeeId, Long serviceId, LocalDateTime startTime, LocalDateTime endTime, AppointmentStatus status) {
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getClientId() {return clientId;}
    public void setClientId(Long clientId) {this.clientId = clientId;}

    public Long getEmployeeId() {return employeeId;}
    public void setEmployeeId(Long employeeId) {this.employeeId = employeeId;}

    public Long getServiceId() {return serviceId;}
    public void setServiceId(Long serviceId) {this.serviceId = serviceId;}

    public LocalDateTime getStartTime() {return startTime;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public LocalDateTime getEndTime() {return endTime;}
    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

    public AppointmentStatus getStatus() {return status;}
    public void setStatus(AppointmentStatus status) {this.status = status;}

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", employeeId=" + employeeId +
                ", serviceId=" + serviceId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
}
