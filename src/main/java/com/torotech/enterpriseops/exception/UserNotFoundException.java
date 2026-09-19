package com.torotech.enterpriseops.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(UUID id) {
        super("Usuario no encontrado con id: " + id);
    }

    public UserNotFoundException(String field, String value) {
        super("Usuario no encontrado con " + field + ": " + value);
    }
}