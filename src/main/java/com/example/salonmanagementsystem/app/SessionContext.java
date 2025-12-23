package com.example.salonmanagementsystem.app;

import com.example.salonmanagementsystem.model.User;

public class SessionContext {

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }

    // Опционально: проверка авторизации
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}