package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.dto.ClientActivityReportRow;
import com.example.salonmanagementsystem.dto.EmployeeLoadReportRow;
import com.example.salonmanagementsystem.dto.RevenueReportRow;
import com.example.salonmanagementsystem.dto.ServiceReportRow;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO для работы с отчетами
 */
public interface ReportDao {

    /**
     * Получить отчет по выручке за период
     * @param from начальная дата
     * @param to конечная дата
     * @return список строк отчета
     */
    List<RevenueReportRow> getRevenueByPeriod(LocalDate from, LocalDate to);

    /**
     * Получить отчет по выручке конкретного сотрудника
     * @param employeeId ID сотрудника
     * @param from начальная дата
     * @param to конечная дата
     * @return список строк отчета
     */
    List<RevenueReportRow> getRevenueByEmployee(Long employeeId, LocalDate from, LocalDate to);

    /**
     * Получить отчет по выручке конкретной услуги
     * @param serviceId ID услуги
     * @param from начальная дата
     * @param to конечная дата
     * @return список строк отчета
     */
    List<RevenueReportRow> getRevenueByService(Long serviceId, LocalDate from, LocalDate to);

    /**
     * Получить отчет по загрузке сотрудников
     * @param from начальная дата
     * @param to конечная дата
     * @return список строк отчета
     */
    List<EmployeeLoadReportRow> getEmployeeLoad(LocalDate from, LocalDate to);

    /**
     * Получить отчет по активности клиентов
     * @param from начальная дата
     * @param to конечная дата
     * @return список строк отчета
     */
    List<ClientActivityReportRow> getClientActivity(LocalDate from, LocalDate to);

    /**
     * Получить отчет по популярности услуг
     * @param from начальная дата
     * @param to конечная дата
     * @return список строк отчета
     */
    List<ServiceReportRow> getServiceStatistics(LocalDate from, LocalDate to);

    /**
     * Получить общую выручку за период
     * @param from начальная дата
     * @param to конечная дата
     * @return общая сумма выручки
     */
    Double getTotalRevenue(LocalDate from, LocalDate to);
}