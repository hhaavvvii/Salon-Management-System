package com.example.salonmanagementsystem.dao.impl;

import com.example.salonmanagementsystem.dao.UserDao;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;
import com.example.salonmanagementsystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final String FIND_BY_USERNAME_SQL = """
        SELECT id, username, password_hash, role, employee_id
        FROM users
        WHERE username = ?
        """;

    @Override
    public Optional<User> findByLogin(String login) {

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERNAME_SQL)) {

            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    return Optional.empty();
                }

                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(Role.valueOf(rs.getString("role")));

                Long employeeId = null;
                long empId = rs.getLong("employee_id");
                if (!rs.wasNull()) {
                    employeeId = empId;
                }
                user.setEmployeeId(employeeId);


                return Optional.of(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by login", e);
        }
    }
}
