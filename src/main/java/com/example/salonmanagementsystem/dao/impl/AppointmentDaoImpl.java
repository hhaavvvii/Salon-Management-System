package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.AppointmentDao;
import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.AppointmentStatus;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDaoImpl implements AppointmentDao {

    private static final DateTimeFormatter DB_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public boolean hasFutureAppointments(long clientId) {
        String sql = """
            SELECT 1
            FROM appointments
            WHERE client_id = ?
              AND start_time >= ?
            LIMIT 1
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, clientId);
            ps.setString(2, LocalDateTime.now().toString());

            return ps.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        String sql = """
            SELECT a.*, 
                   c.first_name || ' ' || c.last_name as client_name,
                   e.first_name || ' ' || e.last_name as employee_name,
                   s.name as service_name,
                   s.price as service_price,
                   s.duration_minutes as service_duration
            FROM appointments a
            JOIN clients c ON a.client_id = c.id
            JOIN employees e ON a.employee_id = e.id
            JOIN services s ON a.service_id = s.id
            ORDER BY a.start_time DESC
        """;
        return query(sql, ps -> {});
    }

    @Override
    public List<Appointment> findByEmployee(long employeeId) {
        String sql = """
            SELECT a.*, 
                   c.first_name || ' ' || c.last_name as client_name,
                   e.first_name || ' ' || e.last_name as employee_name,
                   s.name as service_name,
                   s.price as service_price,
                   s.duration_minutes as service_duration
            FROM appointments a
            JOIN clients c ON a.client_id = c.id
            JOIN employees e ON a.employee_id = e.id
            JOIN services s ON a.service_id = s.id
            WHERE a.employee_id = ?
            ORDER BY a.start_time DESC
        """;
        return query(sql, ps -> ps.setLong(1, employeeId));
    }

    @Override
    public void insert(Appointment a) {
        String sql = """
            INSERT INTO appointments
            (client_id, employee_id, service_id, start_time, end_time, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, a.getClientId());
            ps.setLong(2, a.getEmployeeId());
            ps.setLong(3, a.getServiceId());
            ps.setString(4, a.getStartTime().toString());
            ps.setString(5, a.getEndTime().toString());
            ps.setString(6, a.getStatus().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Appointment a) {
        String sql = """
            UPDATE appointments
            SET employee_id = ?,
                start_time = ?,
                end_time = ?,
                status = ?
            WHERE id = ?
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, a.getEmployeeId());
            ps.setString(2, a.getStartTime().toString());
            ps.setString(3, a.getEndTime().toString());
            ps.setString(4, a.getStatus().name());
            ps.setLong(5, a.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(long id, AppointmentStatus status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setLong(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Appointment> query(String sql, SqlConsumer<PreparedStatement> binder) {
        List<Appointment> list = new ArrayList<>();

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            binder.accept(ps);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getLong("id"));
                a.setClientId(rs.getLong("client_id"));
                a.setEmployeeId(rs.getLong("employee_id"));
                a.setServiceId(rs.getLong("service_id"));

                a.setStartTime(
                        LocalDateTime.parse(
                                rs.getString("start_time"),
                                DB_DATE_TIME_FORMATTER
                        )
                );

                a.setEndTime(
                        LocalDateTime.parse(
                                rs.getString("end_time"),
                                DB_DATE_TIME_FORMATTER
                        )
                );

                a.setStatus(AppointmentStatus.valueOf(rs.getString("status")));

                a.setClientName(rs.getString("client_name"));
                a.setEmployeeName(rs.getString("employee_name"));
                a.setServiceName(rs.getString("service_name"));

                a.setPrice(rs.getDouble("service_price"));
                a.setDurationMinutes(rs.getInt("service_duration"));

                list.add(a);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public boolean hasTimeConflict(long employeeId,
                                   LocalDateTime start,
                                   LocalDateTime end) {

        String sql = """
        SELECT 1
        FROM appointments
        WHERE employee_id = ?
          AND start_time < ?
          AND end_time   > ?
          AND status != 'CANCELED'
        LIMIT 1
    """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, employeeId);
            ps.setString(2, end.toString());
            ps.setString(3, start.toString());

            return ps.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface SqlConsumer<T> {
        void accept(T t) throws Exception;
    }
}