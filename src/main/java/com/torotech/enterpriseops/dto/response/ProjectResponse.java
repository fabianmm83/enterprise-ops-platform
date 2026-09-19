package com.torotech.enterpriseops.dto.response;

import com.torotech.enterpriseops.entity.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        ProjectStatus status,
        UserSummary owner,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt,
        Instant updatedAt
) {
    public record UserSummary(UUID id, String email, String fullName) {}
}