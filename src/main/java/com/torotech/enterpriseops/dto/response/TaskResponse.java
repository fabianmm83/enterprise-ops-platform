package com.torotech.enterpriseops.dto.response;

import com.torotech.enterpriseops.entity.TaskPriority;
import com.torotech.enterpriseops.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        UUID projectId,
        String projectName,
        TaskSummaryResponse.AssigneeSummary assignee,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {
}