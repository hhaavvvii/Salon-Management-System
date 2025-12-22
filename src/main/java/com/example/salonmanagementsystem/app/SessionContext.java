package com.example.salonmanagementsystem.app;

import com.example.salonmanagementsystem.model.User;

public final class SessionContext {

    private static User currentUser;

    private SessionContext() {
        // запрещаем создание экземпляров
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }
}
