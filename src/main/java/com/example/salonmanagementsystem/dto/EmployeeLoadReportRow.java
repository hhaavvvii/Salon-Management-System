package com.example.salonmanagementsystem.dto;

/**
 * DTO для отображения загрузки сотрудника
 */
public class EmployeeLoadReportRow {
    private String employeeName;
    private Integer totalAppointments;
    private Integer totalMinutes;
    private Double totalRevenue;
    private Double loadPercentage;

    public EmployeeLoadReportRow() {
    }

    public EmployeeLoadReportRow(String employeeName, Integer totalAppointments,
                                 Integer totalMinutes, Double totalRevenue, Double loadPercentage) {
        this.employeeName = employeeName;
        this.totalAppointments = totalAppointments;
        this.totalMinutes = totalMinutes;
        this.totalRevenue = totalRevenue;
        this.loadPercentage = loadPercentage;
    }

    // Getters and Setters
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getLoadPercentage() {
        return loadPercentage;
    }

    public void setLoadPercentage(Double loadPercentage) {
        this.loadPercentage = loadPercentage;
    }

    /**
     * Возвращает общее рабочее время в формате "X hours Y min"
     */
    public String getFormattedWorkTime() {
        if (totalMinutes == null || totalMinutes == 0) {
            return "0 min";
        }
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (hours == 0) {
            return minutes + " min";
        }
        return hours + " hours " + (minutes > 0 ? minutes + " min" : "");
    }

    @Override
    public String toString() {
        return "EmployeeLoadReportRow{" +
                "employeeName='" + employeeName + '\'' +
                ", totalAppointments=" + totalAppointments +
                ", totalMinutes=" + totalMinutes +
                ", totalRevenue=" + totalRevenue +
                ", loadPercentage=" + loadPercentage +
                '}';
    }
}