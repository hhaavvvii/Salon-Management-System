package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.ReportDao;
import com.example.salonmanagementsystem.dto.ClientActivityReportRow;
import com.example.salonmanagementsystem.dto.EmployeeLoadReportRow;
import com.example.salonmanagementsystem.dto.RevenueReportRow;
import com.example.salonmanagementsystem.dto.ServiceReportRow;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация DAO для отчетов
 * Все расчеты основаны на PAID платежах и COMPLETED записях
 */
public class ReportDaoImpl implements ReportDao {


    @Override
    public List<RevenueReportRow> getRevenueByPeriod(LocalDate from, LocalDate to) {
        String sql = """
        SELECT 
            DATE(p.created_at) as payment_date,
            e.first_name || ' ' || e.last_name as employee_name,
            s.name as service_name,
            COUNT(p.id) as completed_count,
            SUM(p.amount) as total_revenue
        FROM payments p
        JOIN appointments a ON p.appointment_id = a.id
        JOIN employees e ON a.employee_id = e.id
        JOIN services s ON a.service_id = s.id
        WHERE p.status = 'PAID'
          AND p.created_at BETWEEN ? AND ?
        GROUP BY DATE(p.created_at), e.id, s.id
        ORDER BY payment_date DESC, total_revenue DESC
    """;

        List<RevenueReportRow> rows = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("=== SQL DEBUG ===");
            System.out.println("From: " + from.toString());
            System.out.println("To: " + to.toString());

            ps.setString(1, from.toString());
            ps.setString(2, to.toString());

            System.out.println("Executing query...");
            ResultSet rs = ps.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                RevenueReportRow row = new RevenueReportRow();
                row.setDate(LocalDate.parse(rs.getString("payment_date")));
                row.setEmployeeName(rs.getString("employee_name"));
                row.setServiceName(rs.getString("service_name"));
                row.setCompletedCount(rs.getInt("completed_count"));
                row.setTotalRevenue(rs.getDouble("total_revenue"));
                rows.add(row);

                if (count == 1) {
                    System.out.println("First row: " + row);
                }
            }

            System.out.println("Total rows found: " + count);

        } catch (SQLException e) {
            System.out.println("SQL ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting revenue by period", e);
        }

        return rows;
    }

    @Override
    public List<RevenueReportRow> getRevenueByEmployee(Long employeeId, LocalDate from, LocalDate to) {
        String sql = """
        SELECT 
            DATE(p.created_at) as payment_date,
            e.first_name || ' ' || e.last_name as employee_name,
            s.name as service_name,
            COUNT(p.id) as completed_count,
            SUM(p.amount) as total_revenue
        FROM payments p
        JOIN appointments a ON p.appointment_id = a.id
        JOIN employees e ON a.employee_id = e.id
        JOIN services s ON a.service_id = s.id
        WHERE p.status = 'PAID'
          AND a.employee_id = ?
          AND p.created_at BETWEEN ? AND ?
        GROUP BY DATE(p.created_at), e.id, s.id
        ORDER BY payment_date DESC
    """;

        List<RevenueReportRow> rows = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, employeeId);
            ps.setString(2, from.toString());
            ps.setString(3, to.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RevenueReportRow row = new RevenueReportRow();
                row.setDate(LocalDate.parse(rs.getString("payment_date")));
                row.setEmployeeName(rs.getString("employee_name"));
                row.setServiceName(rs.getString("service_name"));
                row.setCompletedCount(rs.getInt("completed_count"));
                row.setTotalRevenue(rs.getDouble("total_revenue"));
                rows.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting revenue by employee", e);
        }

        return rows;
    }

    @Override
    public List<RevenueReportRow> getRevenueByService(Long serviceId, LocalDate from, LocalDate to) {
        String sql = """
        SELECT 
            DATE(p.created_at) as payment_date,
            e.first_name || ' ' || e.last_name as employee_name,
            s.name as service_name,
            COUNT(p.id) as completed_count,
            SUM(p.amount) as total_revenue
        FROM payments p
        JOIN appointments a ON p.appointment_id = a.id
        JOIN employees e ON a.employee_id = e.id
        JOIN services s ON a.service_id = s.id
        WHERE p.status = 'PAID'
          AND a.service_id = ?
          AND p.created_at BETWEEN ? AND ?
        GROUP BY DATE(p.created_at), e.id, s.id
        ORDER BY payment_date DESC
    """;

        List<RevenueReportRow> rows = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, serviceId);
            ps.setString(2, from.toString());
            ps.setString(3, to.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RevenueReportRow row = new RevenueReportRow();
                row.setDate(LocalDate.parse(rs.getString("payment_date")));
                row.setEmployeeName(rs.getString("employee_name"));
                row.setServiceName(rs.getString("service_name"));
                row.setCompletedCount(rs.getInt("completed_count"));
                row.setTotalRevenue(rs.getDouble("total_revenue"));
                rows.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting revenue by service", e);
        }

        return rows;
    }

    @Override
    public List<EmployeeLoadReportRow> getEmployeeLoad(LocalDate from, LocalDate to) {
        String sql = """
        SELECT 
            e.first_name || ' ' || e.last_name as employee_name,
            COUNT(a.id) as total_appointments,
            SUM(s.duration_minutes) as total_minutes,
            COALESCE(SUM(p.amount), 0) as total_revenue
        FROM employees e
        LEFT JOIN appointments a ON e.id = a.employee_id 
            AND a.status = 'COMPLETED'
            AND DATE(a.start_time) BETWEEN ? AND ?
        LEFT JOIN services s ON a.service_id = s.id
        LEFT JOIN payments p ON a.id = p.appointment_id AND p.status = 'PAID'
        GROUP BY e.id
        ORDER BY total_revenue DESC
    """;

        List<EmployeeLoadReportRow> rows = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, from.toString());
            ps.setString(2, to.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeLoadReportRow row = new EmployeeLoadReportRow();
                row.setEmployeeName(rs.getString("employee_name"));
                row.setTotalAppointments(rs.getInt("total_appointments"));

                Integer totalMinutes = rs.getObject("total_minutes") != null
                        ? rs.getInt("total_minutes") : 0;
                row.setTotalMinutes(totalMinutes);

                row.setTotalRevenue(rs.getDouble("total_revenue"));

                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
                int availableMinutes = (int) (daysBetween * 8 * 60);
                double loadPercentage = availableMinutes > 0
                        ? (totalMinutes * 100.0 / availableMinutes) : 0;
                row.setLoadPercentage(Math.min(loadPercentage, 100.0));

                rows.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting employee load", e);
        }

        return rows;
    }



    @Override
    public List<ClientActivityReportRow> getClientActivity(LocalDate from, LocalDate to) {
        String sql = """
        SELECT 
            c.first_name || ' ' || c.last_name as client_name,
            c.phone,
            COUNT(a.id) as total_visits,
            COALESCE(SUM(p.amount), 0) as total_spent,
            MAX(DATE(a.start_time)) as last_visit_date
        FROM clients c
        LEFT JOIN appointments a ON c.id = a.client_id 
            AND a.status = 'COMPLETED'
            AND DATE(a.start_time) BETWEEN ? AND ?
        LEFT JOIN payments p ON a.id = p.appointment_id AND p.status = 'PAID'
        GROUP BY c.id
        HAVING COUNT(a.id) > 0
        ORDER BY total_spent DESC
    """;

        List<ClientActivityReportRow> rows = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, from.toString());
            ps.setString(2, to.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ClientActivityReportRow row = new ClientActivityReportRow();
                row.setClientName(rs.getString("client_name"));
                row.setPhone(rs.getString("phone"));
                row.setTotalVisits(rs.getInt("total_visits"));
                row.setTotalSpent(rs.getDouble("total_spent"));
                row.setLastVisitDate(rs.getString("last_visit_date"));
                rows.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting client activity", e);
        }

        return rows;
    }

    @Override
    public List<ServiceReportRow> getServiceStatistics(LocalDate from, LocalDate to) {
        String sql = """
        SELECT 
            s.name as service_name,
            COUNT(a.id) as times_booked,
            COALESCE(SUM(p.amount), 0) as total_revenue,
            COALESCE(AVG(p.amount), 0) as average_price
        FROM services s
        LEFT JOIN appointments a ON s.id = a.service_id 
            AND a.status = 'COMPLETED'
            AND DATE(a.start_time) BETWEEN ? AND ?
        LEFT JOIN payments p ON a.id = p.appointment_id AND p.status = 'PAID'
        GROUP BY s.id
        HAVING COUNT(a.id) > 0
        ORDER BY total_revenue DESC
    """;

        List<ServiceReportRow> rows = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, from.toString());
            ps.setString(2, to.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceReportRow row = new ServiceReportRow();
                row.setServiceName(rs.getString("service_name"));
                row.setTimesBooked(rs.getInt("times_booked"));
                row.setTotalRevenue(rs.getDouble("total_revenue"));
                row.setAveragePrice(rs.getDouble("average_price"));
                rows.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting service statistics", e);
        }

        return rows;
    }

    @Override
    public Double getTotalRevenue(LocalDate from, LocalDate to) {
        String sql = """
        SELECT COALESCE(SUM(amount), 0) as total
        FROM payments
        WHERE status = 'PAID'
          AND created_at BETWEEN ? AND ?
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, from.toString());
            ps.setString(2, to.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0.0;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting total revenue", e);
        }
    }
}