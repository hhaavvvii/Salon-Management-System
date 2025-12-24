package com.example.salonmanagementsystem.app;

import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;

/**
 * Контекст текущей сессии пользователя
 * Хранит информацию о залогиненном пользователе
 */
public class SessionContext {

    private static User currentUser;

    /**
     * Установить текущего пользователя
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Получить текущего пользователя
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Проверка: пользователь залогинен?
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Проверка: текущий пользователь - администратор?
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    /**
     * Проверка: текущий пользователь - мастер?
     */
    public static boolean isMaster() {
        return currentUser != null && currentUser.getRole() == Role.MASTER;
    }

    /**
     * Получить ID сотрудника текущего пользователя
     * Для роли MASTER возвращает ID связанного сотрудника
     * Для роли ADMIN возвращает null
     */
    public static Long getCurrentEmployeeId() {
        if (currentUser != null && currentUser.getRole() == Role.MASTER) {
            return currentUser.getEmployeeId();
        }
        return null;
    }

    /**
     * Проверка: имеет ли пользователь доступ к данным конкретного сотрудника
     */
    public static boolean hasAccessToEmployee(Long employeeId) {
        if (isAdmin()) {
            return true; // Админ имеет доступ ко всем
        }

        if (isMaster()) {
            Long currentEmpId = getCurrentEmployeeId();
            return currentEmpId != null && currentEmpId.equals(employeeId);
        }

        return false;
    }

    /**
     * Очистить сессию при logout
     */
    public static void clearSession() {
        currentUser = null;
    }
}