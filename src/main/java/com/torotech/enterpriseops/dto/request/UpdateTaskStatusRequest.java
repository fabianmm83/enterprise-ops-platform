package com.torotech.enterpriseops.dto.request;

import com.torotech.enterpriseops.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(

        @NotNull(message = "El status es obligatorio")
        TaskStatus status
) {
}