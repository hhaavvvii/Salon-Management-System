package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.ClientDao;
import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDaoImpl implements ClientDao {

    @Override
    public List<Client> findAll() {
        String sql = "SELECT * FROM clients ORDER BY first_name, last_name";
        return executeQuery(sql);
    }

    @Override
    public Optional<Client> findById(long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding client by id", e);
        }
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        String sql = "SELECT * FROM clients WHERE phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToClient(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding client by phone", e);
        }
    }

    @Override
    public List<Client> findByFilters(String name, String phone, Boolean active) {
        StringBuilder sql = new StringBuilder("SELECT * FROM clients WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND (first_name LIKE ? OR last_name LIKE ?)");
            String pattern = "%" + name.trim() + "%";
            params.add(pattern);
            params.add(pattern);
        }

        if (phone != null && !phone.trim().isEmpty()) {
            sql.append(" AND phone LIKE ?");
            params.add("%" + phone.trim() + "%");
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
            List<Client> clients = new ArrayList<>();

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }

            return clients;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding clients by filters", e);
        }
    }

    @Override
    public List<Client> findActiveClients() {
        String sql = "SELECT * FROM clients WHERE active = 1 ORDER BY first_name, last_name";
        return executeQuery(sql);
    }

    @Override
    public void insert(Client client) {
        String sql = """
            INSERT INTO clients (first_name, last_name, phone, email, notes)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getPhone());
            ps.setString(4, client.getEmail());
            ps.setString(5, client.getNotes());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                client.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting client", e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = """
            UPDATE clients
            SET first_name = ?,
                last_name = ?,
                phone = ?,
                email = ?,
                notes = ?
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
            throw new RuntimeException("Error updating client", e);
        }
    }

    @Override
    public void setInactive(long id) {
        String sql = "UPDATE clients SET active = 0 WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating client", e);
        }
    }

    @Override
    public boolean phoneExists(String phone, Long excludeId) {
        String sql = "SELECT 1 FROM clients WHERE phone = ? AND id != ? LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setLong(2, excludeId == null ? -1 : excludeId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error checking phone existence", e);
        }
    }

    private List<Client> executeQuery(String sql) {
        List<Client> clients = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }

            return clients;

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getLong("id"));
        client.setFirstName(rs.getString("first_name"));
        client.setLastName(rs.getString("last_name"));
        client.setPhone(rs.getString("phone"));
        client.setEmail(rs.getString("email"));
        client.setNotes(rs.getString("notes"));

        return client;
    }
}