package com.example.salonmanagementsystem.app;

import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;

public class SessionContext {

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }


    public static User getCurrentUser() {
        return currentUser;
    }


    public static boolean isLoggedIn() {
        return currentUser != null;
    }


    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }


    public static boolean isMaster() {
        return currentUser != null && currentUser.getRole() == Role.MASTER;
    }


    public static Long getCurrentEmployeeId() {
        if (currentUser != null && currentUser.getRole() == Role.MASTER) {
            return currentUser.getEmployeeId();
        }
        return null;
    }


    public static boolean hasAccessToEmployee(Long employeeId) {
        if (isAdmin()) {
            return true;
        }

        if (isMaster()) {
            Long currentEmpId = getCurrentEmployeeId();
            return currentEmpId != null && currentEmpId.equals(employeeId);
        }

        return false;
    }

    public static void clearSession() {
        currentUser = null;
    }
}