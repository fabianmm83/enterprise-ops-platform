package com.torotech.enterpriseops.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$",
                message = "La contraseña debe incluir mayúscula, minúscula, número y un carácter especial"
        )
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
        String fullName
) {
}