package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.PaymentDao;
import com.example.salonmanagementsystem.model.Payment;
import com.example.salonmanagementsystem.model.PaymentMethod;
import com.example.salonmanagementsystem.model.PaymentStatus;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDaoImpl implements PaymentDao {

    @Override
    public List<Payment> findAll() {
        String sql = """
            SELECT p.*,
                   c.first_name || ' ' || c.last_name as client_name,
                   e.first_name || ' ' || e.last_name as employee_name,
                   s.name as service_name
            FROM payments p
            JOIN appointments a ON p.appointment_id = a.id
            JOIN clients c ON a.client_id = c.id
            JOIN employees e ON a.employee_id = e.id
            JOIN services s ON a.service_id = s.id
            ORDER BY p.created_at DESC
        """;

        return executeQuery(sql);
    }

    @Override
    public Optional<Payment> findById(long id) {
        String sql = """
            SELECT p.*,
                   c.first_name || ' ' || c.last_name as client_name,
                   e.first_name || ' ' || e.last_name as employee_name,
                   s.name as service_name
            FROM payments p
            JOIN appointments a ON p.appointment_id = a.id
            JOIN clients c ON a.client_id = c.id
            JOIN employees e ON a.employee_id = e.id
            JOIN services s ON a.service_id = s.id
            WHERE p.id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPayment(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment by id", e);
        }
    }

    @Override
    public Optional<Payment> findByAppointmentId(long appointmentId) {
        String sql = """
            SELECT p.*,
                   c.first_name || ' ' || c.last_name as client_name,
                   e.first_name || ' ' || e.last_name as employee_name,
                   s.name as service_name
            FROM payments p
            JOIN appointments a ON p.appointment_id = a.id
            JOIN clients c ON a.client_id = c.id
            JOIN employees e ON a.employee_id = e.id
            JOIN services s ON a.service_id = s.id
            WHERE p.appointment_id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPayment(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment by appointment", e);
        }
    }

    @Override
    public void insert(Payment payment) {
        String sql = """
            INSERT INTO payments (appointment_id, amount, created_at, method, status, comment)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, payment.getAppointmentId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentDate().toString());
            ps.setString(4, payment.getPaymentMethod().name());
            ps.setString(5, payment.getStatus().name());
            ps.setString(6, payment.getComment());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                payment.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting payment", e);
        }
    }

    @Override
    public void updateStatus(long id, PaymentStatus status, String comment) {
        String sql = "UPDATE payments SET status = ?, comment = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, comment);
            ps.setLong(3, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment status", e);
        }
    }

    @Override
    public boolean existsByAppointmentId(long appointmentId) {
        String sql = "SELECT 1 FROM payments WHERE appointment_id = ? LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error checking payment existence", e);
        }
    }

    private List<Payment> executeQuery(String sql) {
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }

            return payments;

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setAppointmentId(rs.getLong("appointment_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentDate(LocalDate.parse(rs.getString("created_at")));
        payment.setPaymentMethod(PaymentMethod.valueOf(rs.getString("method")));
        payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        payment.setComment(rs.getString("comment"));

        // Display names
        payment.setClientName(rs.getString("client_name"));
        payment.setEmployeeName(rs.getString("employee_name"));
        payment.setServiceName(rs.getString("service_name"));

        return payment;
    }
}