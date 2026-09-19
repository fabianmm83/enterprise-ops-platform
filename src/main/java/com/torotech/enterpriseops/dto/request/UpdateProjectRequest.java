package com.torotech.enterpriseops.dto.request;

import com.torotech.enterpriseops.entity.ProjectStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProjectRequest(

        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
        String name,

        @Size(max = 5000, message = "La descripción no puede superar 5000 caracteres")
        String description,

        ProjectStatus status,

        LocalDate startDate,

        LocalDate endDate
) {
}