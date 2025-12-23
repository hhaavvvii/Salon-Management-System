package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.AppointmentDao;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AppointmentDaoImpl implements AppointmentDao {

    @Override
    public boolean hasFutureAppointments(long clientId) {
        String sql = """
            SELECT 1
            FROM appointments
            WHERE client_id = ?
              AND start_time >= CURRENT_DATE
            LIMIT 1
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, clientId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
