package com.example.salonmanagementsystem.util;

public class PasswordUtil {

    public PasswordUtil() {
    }

    // ВРЕМЕННО: простой текстовый пароль (как в seed.sql)
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        return rawPassword.equals(storedHash);
    }
}
