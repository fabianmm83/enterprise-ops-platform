package com.torotech.enterpriseops.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(UUID id) {
        super("Tarea no encontrada con id: " + id);
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}