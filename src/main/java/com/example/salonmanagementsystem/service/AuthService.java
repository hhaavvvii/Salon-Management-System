package com.example.salonmanagementsystem.service;

import com.example.salonmanagementsystem.dao.UserDao;
import com.example.salonmanagementsystem.exceptions.AuthException;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;
import com.example.salonmanagementsystem.util.PasswordUtil;

import java.util.Optional;

public class AuthService {
    private UserDao userDao;
    private PasswordUtil passwordUtil;

    public AuthService(UserDao userDao, PasswordUtil passwordUtil) {
        this.userDao = userDao;
        this.passwordUtil = passwordUtil;
    }



    public void login(String login, String password, Role role){
        //temporary validation

        if (login == null || login.isBlank()) throw new AuthException("Login is required");
        if (password == null || password.isBlank()) throw new AuthException("password is required");
        if (role == null) throw new AuthException("role is not selected");

        //searching the user
        Optional<User> optionalUser = userDao.findByLogin(login);

        if (optionalUser.isEmpty()) throw new AuthException("Invalid login or password");

        User user = optionalUser.get();

        //checking password
        if(!passwordUtil.matches(password, user.getPasswordHash())) throw new AuthException("Invalid password");

        //checking role
        if(user.getRole() != role) throw new AuthException("Invalid role");

        //Success - method finished
    }
}