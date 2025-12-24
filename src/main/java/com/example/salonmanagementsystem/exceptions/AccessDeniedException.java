package com.example.salonmanagementsystem.exceptions;

/**
 * Исключение выбрасывается при попытке доступа к ресурсу без необходимых прав
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("Access denied: insufficient permissions");
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}