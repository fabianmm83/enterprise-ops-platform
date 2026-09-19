package com.torotech.enterpriseops.dto.request;

import com.torotech.enterpriseops.entity.Role;
import jakarta.validation.constraints.Size;

/**
 * DTO de actualización de usuario.
 * Todos los campos son opcionales (patch semantics):
 * solo se actualiza lo que viene con valor no-null.
 */
public record UpdateUserRequest(

        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
        String fullName,

        Role role,

        Boolean active
) {
}