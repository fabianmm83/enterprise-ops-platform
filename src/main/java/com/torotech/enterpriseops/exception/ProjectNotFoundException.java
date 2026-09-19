package com.torotech.enterpriseops.exception;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(UUID id) {
        super("Proyecto no encontrado con id: " + id);
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}