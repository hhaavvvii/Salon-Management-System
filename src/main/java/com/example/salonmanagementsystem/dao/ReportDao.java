package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.dto.ClientActivityReportRow;
import com.example.salonmanagementsystem.dto.EmployeeLoadReportRow;
import com.example.salonmanagementsystem.dto.RevenueReportRow;
import com.example.salonmanagementsystem.dto.ServiceReportRow;

import java.time.LocalDate;
import java.util.List;

public interface ReportDao {

    List<RevenueReportRow> getRevenueByPeriod(LocalDate from, LocalDate to);

    List<RevenueReportRow> getRevenueByEmployee(Long employeeId, LocalDate from, LocalDate to);

    List<RevenueReportRow> getRevenueByService(Long serviceId, LocalDate from, LocalDate to);

    List<EmployeeLoadReportRow> getEmployeeLoad(LocalDate from, LocalDate to);

    List<ClientActivityReportRow> getClientActivity(LocalDate from, LocalDate to);

    List<ServiceReportRow> getServiceStatistics(LocalDate from, LocalDate to);

    Double getTotalRevenue(LocalDate from, LocalDate to);
}