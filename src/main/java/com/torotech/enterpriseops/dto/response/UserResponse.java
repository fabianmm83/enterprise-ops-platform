package com.torotech.enterpriseops.dto.response;

import com.torotech.enterpriseops.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        Role role,
        Boolean active,
        Instant createdAt
) {
}