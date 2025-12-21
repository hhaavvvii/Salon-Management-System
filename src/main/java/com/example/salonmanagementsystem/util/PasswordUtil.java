package com.example.salonmanagementsystem.util;

public class PasswordUtil {
    public boolean matches(String password, String passwordHash) {
        return  password.equals(passwordHash);
    }
}
