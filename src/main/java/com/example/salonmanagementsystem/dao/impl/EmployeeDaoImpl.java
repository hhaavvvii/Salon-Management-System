package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.EmployeeDao;
import com.example.salonmanagementsystem.model.Employee;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoImpl implements EmployeeDao {

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employees ORDER BY first_name, last_name";
        return executeQuery(sql);
    }

    @Override
    public Optional<Employee> findById(long id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmployee(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee by id", e);
        }
    }

    @Override
    public List<Employee> findByFilters(String name, String position, Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND (first_name LIKE ? OR last_name LIKE ?)");
            String pattern = "%" + name.trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }

        if (position != null && !position.trim().isEmpty() && !position.equals("ALL")) {
            sql.append(" AND position = ?");
            params.add(position);
        }

        if (active != null) {
            sql.append(" AND active = ?");
            params.add(active ? 1 : 0);
        }

        sql.append(" ORDER BY first_name, last_name");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<Employee> employees = new ArrayList<>();

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }

            return employees;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding employees by filters", e);
        }
    }

    @Override
    public List<Employee> findActiveEmployees() {
        String sql = "SELECT * FROM employees WHERE active = 1 ORDER BY first_name, last_name";
        return executeQuery(sql);
    }

    @Override
    public void insert(Employee employee) {
        String sql = """
            INSERT INTO employees (first_name, last_name, position, active)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getPosition());
            ps.setInt(4, employee.isActive() ? 1 : 0);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                employee.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting employee", e);
        }
    }

    @Override
    public void update(Employee employee) {
        String sql = """
            UPDATE employees
            SET first_name = ?,
                last_name = ?,
                position = ?,
                active = ?
            WHERE id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getPosition());
            ps.setInt(4, employee.isActive() ? 1 : 0);
            ps.setLong(5, employee.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee", e);
        }
    }

    @Override
    public void setInactive(long id) {
        String sql = "UPDATE employees SET active = 0 WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating employee", e);
        }
    }

    @Override
    public boolean hasFutureAppointments(long employeeId) {
        String sql = """
            SELECT 1
            FROM appointments
            WHERE employee_id = ?
              AND start_time >= ?
              AND status != 'CANCELED'
            LIMIT 1
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, employeeId);
            ps.setString(2, LocalDateTime.now().toString());

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error checking future appointments", e);
        }
    }

    private List<Employee> executeQuery(String sql) {
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }

            return employees;

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getLong("id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setPosition(rs.getString("position"));
        employee.setActive(rs.getInt("active") == 1);

        return employee;
    }
}