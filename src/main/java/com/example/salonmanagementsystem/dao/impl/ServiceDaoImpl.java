package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.ServiceDao;
import com.example.salonmanagementsystem.model.ServiceItem;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServiceDaoImpl implements ServiceDao {

    @Override
    public List<ServiceItem> findAll() {

        String sql = """
            SELECT id, name, category, price, duration_minutes
            FROM services
            WHERE active = 1
            ORDER BY name
        """;

        List<ServiceItem> list = new ArrayList<>();

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ServiceItem s = new ServiceItem();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setCategory(rs.getString("category"));
                s.setPrice(rs.getBigDecimal("price"));
                s.setDurationMinutes(rs.getInt("duration_minutes"));
                list.add(s);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
