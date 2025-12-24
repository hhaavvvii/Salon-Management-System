package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.ServiceDao;
import com.example.salonmanagementsystem.model.Service;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceDaoImpl implements ServiceDao {

    @Override
    public List<Service> findAll() {
        String sql = "SELECT * FROM services ORDER BY category, name";
        return executeQuery(sql);
    }

    @Override
    public Optional<Service> findById(long id) {
        String sql = "SELECT * FROM services WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToService(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding service by id", e);
        }
    }

    @Override
    public List<Service> findByFilters(String name, String category, Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT * FROM services WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name.trim() + "%");
        }

        if (category != null && !category.trim().isEmpty() && !category.equals("ALL")) {
            sql.append(" AND category = ?");
            params.add(category);
        }

        if (active != null) {
            sql.append(" AND active = ?");
            params.add(active ? 1 : 0);
        }

        sql.append(" ORDER BY category, name");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<Service> services = new ArrayList<>();

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }

            return services;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding services by filters", e);
        }
    }

    @Override
    public List<Service> findActiveServices() {
        String sql = "SELECT * FROM services WHERE active = 1 ORDER BY category, name";
        return executeQuery(sql);
    }

    @Override
    public void insert(Service service) {
        String sql = """
            INSERT INTO services (name, category, price, duration_minutes, active)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, service.getName());
            ps.setString(2, service.getCategory());
            ps.setDouble(3, service.getPrice());
            ps.setInt(4, service.getDurationMinutes());
            ps.setInt(5, service.isActive() ? 1 : 0);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                service.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting service", e);
        }
    }

    @Override
    public void update(Service service) {
        String sql = """
            UPDATE services
            SET name = ?,
                category = ?,
                price = ?,
                duration_minutes = ?,
                active = ?
            WHERE id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, service.getName());
            ps.setString(2, service.getCategory());
            ps.setDouble(3, service.getPrice());
            ps.setInt(4, service.getDurationMinutes());
            ps.setInt(5, service.isActive() ? 1 : 0);
            ps.setLong(6, service.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating service", e);
        }
    }

    @Override
    public void setInactive(long serviceId) {
        String sql = "UPDATE services SET is_active = 0 WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, serviceId);
            int updated = ps.executeUpdate();

            if (updated == 0) {
                throw new RuntimeException("Service not found with ID: " + serviceId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating service with ID: " + serviceId, e);
        }
    }

    @Override
    public boolean serviceExists(String name, String category, Long excludeId) {
        String sql = "SELECT 1 FROM services WHERE name = ? AND category = ? AND id != ? LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setLong(3, excludeId == null ? -1 : excludeId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error checking service existence", e);
        }
    }

    @Override
    public void delete(long serviceId) {
        String sql = "DELETE FROM services WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, serviceId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting service with ID: " + serviceId, e);
        }
    }

    @Override
    public void setActive(long serviceId) {
        String sql = "UPDATE services SET is_active = 1 WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, serviceId);
            int updated = ps.executeUpdate();

            if (updated == 0) {
                throw new RuntimeException("Service not found with ID: " + serviceId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error activating service with ID: " + serviceId, e);
        }
    }

    private List<Service> executeQuery(String sql) {
        List<Service> services = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }

            return services;

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getLong("id"));
        service.setName(rs.getString("name"));
        service.setCategory(rs.getString("category"));
        service.setPrice(rs.getDouble("price"));
        service.setDurationMinutes(rs.getInt("duration_minutes"));
        service.setActive(rs.getInt("active") == 1);

        return service;
    }
}