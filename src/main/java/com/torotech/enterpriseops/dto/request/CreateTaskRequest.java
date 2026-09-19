package com.torotech.enterpriseops.dto.request;

import com.torotech.enterpriseops.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(min = 2, max = 255, message = "El título debe tener entre 2 y 255 caracteres")
        String title,

        @Size(max = 5000, message = "La descripción no puede superar 5000 caracteres")
        String description,

        UUID assigneeId,

        TaskPriority priority,

        LocalDate dueDate
) {
}