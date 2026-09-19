package com.torotech.enterpriseops.dto.response;

import com.torotech.enterpriseops.entity.TaskPriority;
import com.torotech.enterpriseops.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TaskSummaryResponse(
        UUID id,
        String title,
        TaskStatus status,
        TaskPriority priority,
        AssigneeSummary assignee,
        LocalDate dueDate
) {
    public record AssigneeSummary(UUID id, String email, String fullName) {}
}