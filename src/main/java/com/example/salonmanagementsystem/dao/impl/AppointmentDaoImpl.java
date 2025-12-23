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
        String sql = "SELECT * FROM appointments ORDER BY start_time";
        return query(sql, ps -> {});
    }

    @Override
    public List<Appointment> findByEmployee(long employeeId) {
        String sql = """
            SELECT *
            FROM appointments
            WHERE employee_id = ?
            ORDER BY start_time
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
                        LocalDateTime.parse(rs.getString("start_time"))
                );
                a.setEndTime(
                        LocalDateTime.parse(rs.getString("end_time"))
                );

                a.setStatus(
                        AppointmentStatus.valueOf(rs.getString("status"))
                );

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
