package com.example.salonmanagementsystem.dto;

import java.time.LocalDate;


public class RevenueReportRow {
    private LocalDate date;
    private String employeeName;
    private String serviceName;
    private Integer completedCount;
    private Double totalRevenue;

    public RevenueReportRow() {
    }

    public RevenueReportRow(LocalDate date, String employeeName, String serviceName,
                            Integer completedCount, Double totalRevenue) {
        this.date = date;
        this.employeeName = employeeName;
        this.serviceName = serviceName;
        this.completedCount = completedCount;
        this.totalRevenue = totalRevenue;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Override
    public String toString() {
        return "RevenueReportRow{" +
                "date=" + date +
                ", employeeName='" + employeeName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", completedCount=" + completedCount +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}