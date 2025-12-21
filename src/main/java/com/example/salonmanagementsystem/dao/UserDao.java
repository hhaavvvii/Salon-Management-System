package com.example.salonmanagementsystem.dao;

import com.example.salonmanagementsystem.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByLogin(String login);
}
