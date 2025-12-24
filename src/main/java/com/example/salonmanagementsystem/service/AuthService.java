package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.UserDao;
import com.example.salonmanagementsystem.exceptions.AuthException;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;
import com.example.salonmanagementsystem.util.PasswordUtil;


public class AuthService {
    private UserDao userDao;
    private PasswordUtil passwordUtil;

    public AuthService(UserDao userDao, PasswordUtil passwordUtil) {
        this.userDao = userDao;
        this.passwordUtil = passwordUtil;
    }



    public User login(String username, String password, Role role) {
        if (username == null || username.isBlank()) {
            throw new AuthException("Username is empty");
        }
        if (password == null || password.isBlank()) {
            throw new AuthException("Password is empty");
        }
        if (role == null) {
            throw new AuthException("Role is not selected");
        }

        User user = userDao.findByLogin(username)
                .orElseThrow(() -> new AuthException("User not found"));

        if (user.getRole() != role) {
            throw new AuthException("Invalid role selected");
        }

        if (!passwordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new AuthException("Invalid password");
        }

        return user;
    }

}