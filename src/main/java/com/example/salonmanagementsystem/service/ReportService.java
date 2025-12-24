package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.ReportDao;
import com.example.salonmanagementsystem.dao.impl.ReportDaoImpl;
import com.example.salonmanagementsystem.dto.ClientActivityReportRow;
import com.example.salonmanagementsystem.dto.EmployeeLoadReportRow;
import com.example.salonmanagementsystem.dto.RevenueReportRow;
import com.example.salonmanagementsystem.dto.ServiceReportRow;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.util.CsvExporter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для работы с отчетами
 * Содержит бизнес-логику формирования и экспорта отчетов
 */
public class ReportService {

    private final ReportDao reportDao = new ReportDaoImpl();

    /**
     * Получить отчет по выручке за период
     */
    public List<RevenueReportRow> generateRevenueReport(LocalDate from, LocalDate to,
                                                        Long employeeId, Long serviceId) {
        validateDateRange(from, to);

        if (employeeId != null && serviceId != null) {
            throw new ValidationException("Cannot filter by both employee and service simultaneously");
        }

        if (employeeId != null) {
            return reportDao.getRevenueByEmployee(employeeId, from, to);
        } else if (serviceId != null) {
            return reportDao.getRevenueByService(serviceId, from, to);
        } else {
            return reportDao.getRevenueByPeriod(from, to);
        }
    }

    /**
     * Получить отчет по загрузке сотрудников
     */
    public List<EmployeeLoadReportRow> generateEmployeeLoadReport(LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        return reportDao.getEmployeeLoad(from, to);
    }

    /**
     * Получить отчет по активности клиентов
     */
    public List<ClientActivityReportRow> generateClientActivityReport(LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        return reportDao.getClientActivity(from, to);
    }

    /**
     * Получить статистику по услугам
     */
    public List<ServiceReportRow> generateServiceStatistics(LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        return reportDao.getServiceStatistics(from, to);
    }

    /**
     * Получить общую выручку за период
     */
    public Double getTotalRevenue(LocalDate from, LocalDate to) {
        validateDateRange(from, to);
        return reportDao.getTotalRevenue(from, to);
    }

    /**
     * Экспорт отчета по выручке в CSV
     */
    public File exportRevenueReport(List<RevenueReportRow> data, String filename) throws IOException {
        String[] headers = {"Date", "Employee", "Service", "Completed Count", "Total Revenue (₸)"};

        String[][] rows = data.stream()
                .map(row -> new String[]{
                        row.getDate().toString(),
                        row.getEmployeeName(),
                        row.getServiceName(),
                        String.valueOf(row.getCompletedCount()),
                        String.format("%.2f", row.getTotalRevenue())
                })
                .toArray(String[][]::new);

        return CsvExporter.exportToFile(headers, rows, filename);
    }

    /**
     * Экспорт отчета по загрузке сотрудников в CSV
     */
    public File exportEmployeeLoadReport(List<EmployeeLoadReportRow> data, String filename) throws IOException {
        String[] headers = {"Employee", "Appointments", "Work Time", "Revenue (₸)", "Load %"};

        String[][] rows = data.stream()
                .map(row -> new String[]{
                        row.getEmployeeName(),
                        String.valueOf(row.getTotalAppointments()),
                        row.getFormattedWorkTime(),
                        String.format("%.2f", row.getTotalRevenue()),
                        String.format("%.1f%%", row.getLoadPercentage())
                })
                .toArray(String[][]::new);

        return CsvExporter.exportToFile(headers, rows, filename);
    }

    /**
     * Экспорт отчета по активности клиентов в CSV
     */
    public File exportClientActivityReport(List<ClientActivityReportRow> data, String filename) throws IOException {
        String[] headers = {"Client Name", "Phone", "Total Visits", "Total Spent (₸)", "Last Visit"};

        String[][] rows = data.stream()
                .map(row -> new String[]{
                        row.getClientName(),
                        row.getPhone(),
                        String.valueOf(row.getTotalVisits()),
                        String.format("%.2f", row.getTotalSpent()),
                        row.getLastVisitDate()
                })
                .toArray(String[][]::new);

        return CsvExporter.exportToFile(headers, rows, filename);
    }

    /**
     * Экспорт статистики по услугам в CSV
     */
    public File exportServiceStatistics(List<ServiceReportRow> data, String filename) throws IOException {
        String[] headers = {"Service", "Times Booked", "Total Revenue (₸)", "Average Price (₸)"};

        String[][] rows = data.stream()
                .map(row -> new String[]{
                        row.getServiceName(),
                        String.valueOf(row.getTimesBooked()),
                        String.format("%.2f", row.getTotalRevenue()),
                        String.format("%.2f", row.getAveragePrice())
                })
                .toArray(String[][]::new);

        return CsvExporter.exportToFile(headers, rows, filename);
    }

    /**
     * Валидация диапазона дат
     */
    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new ValidationException("Date range is required");
        }

        if (from.isAfter(to)) {
            throw new ValidationException("Start date must be before or equal to end date");
        }

        // Ограничение: максимум 1 год
        if (from.plusYears(1).isBefore(to)) {
            throw new ValidationException("Date range cannot exceed 1 year");
        }
    }
}