package com.example.salonmanagementsystem.dto;

public class ServiceReportRow {
    private String serviceName;
    private Integer timesBooked;
    private Double totalRevenue;
    private Double averagePrice;

    public ServiceReportRow() {
    }

    public ServiceReportRow(String serviceName, Integer timesBooked,
                            Double totalRevenue, Double averagePrice) {
        this.serviceName = serviceName;
        this.timesBooked = timesBooked;
        this.totalRevenue = totalRevenue;
        this.averagePrice = averagePrice;
    }

    // Getters and Setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getTimesBooked() {
        return timesBooked;
    }

    public void setTimesBooked(Integer timesBooked) {
        this.timesBooked = timesBooked;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    @Override
    public String toString() {
        return "ServiceReportRow{" +
                "serviceName='" + serviceName + '\'' +
                ", timesBooked=" + timesBooked +
                ", totalRevenue=" + totalRevenue +
                ", averagePrice=" + averagePrice +
                '}';
    }
}