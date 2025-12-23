package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.ClientDao;
import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.*;
import java.util.*;

public class ClientDaoImpl implements ClientDao {

    @Override
    public List<Client> findAll() {
        List<Client> result = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY first_name";

        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                result.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Optional<Client> findById(long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        String sql = "SELECT * FROM clients WHERE phone = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? Optional.of(map(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Client client) {
        String sql = """
            INSERT INTO clients(first_name, last_name, phone, email, notes)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getPhone());
            ps.setString(4, client.getEmail());
            ps.setString(5, client.getNotes());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = """
        UPDATE clients
        SET first_name = ?, last_name = ?, phone = ?, email = ?, notes = ?
        WHERE id = ?
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getPhone());
            ps.setString(4, client.getEmail());
            ps.setString(5, client.getNotes());
            ps.setLong(6, client.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps =
                     c.prepareStatement("DELETE FROM clients WHERE id=?")) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Client> findByEmployee(long employeeId) {
        String sql = """
        SELECT DISTINCT c.*
        FROM clients c
        JOIN appointments a ON a.client_id = c.id
        WHERE a.employee_id = ?
        ORDER BY c.first_name
    """;

        List<Client> clients = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Client c = new Client();
                c.setId(rs.getLong("id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setNotes(rs.getString("notes"));
                clients.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clients;
    }


    private Client map(ResultSet rs) throws SQLException {
        Client c = new Client();
        c.setId(rs.getLong("id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setNotes(rs.getString("notes"));
        return c;
    }
}
