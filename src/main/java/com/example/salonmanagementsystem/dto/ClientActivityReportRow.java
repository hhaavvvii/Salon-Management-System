package com.example.salonmanagementsystem.dto;

/**
 * DTO для отображения активности клиента
 */
public class ClientActivityReportRow {
    private String clientName;
    private String phone;
    private Integer totalVisits;
    private Double totalSpent;
    private String lastVisitDate;

    public ClientActivityReportRow() {
    }

    public ClientActivityReportRow(String clientName, String phone, Integer totalVisits,
                                   Double totalSpent, String lastVisitDate) {
        this.clientName = clientName;
        this.phone = phone;
        this.totalVisits = totalVisits;
        this.totalSpent = totalSpent;
        this.lastVisitDate = lastVisitDate;
    }

    // Getters and Setters
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(Integer totalVisits) {
        this.totalVisits = totalVisits;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(String lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    @Override
    public String toString() {
        return "ClientActivityReportRow{" +
                "clientName='" + clientName + '\'' +
                ", phone='" + phone + '\'' +
                ", totalVisits=" + totalVisits +
                ", totalSpent=" + totalSpent +
                ", lastVisitDate='" + lastVisitDate + '\'' +
                '}';
    }
}