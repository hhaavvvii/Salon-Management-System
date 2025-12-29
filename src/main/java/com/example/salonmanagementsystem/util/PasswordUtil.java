package com.example.salonmanagementsystem.util;

public class PasswordUtil {

    public PasswordUtil() {
    }

    public static boolean verifyPassword(String rawPassword, String storedHash) {
        return rawPassword.equals(storedHash);
    }
}
