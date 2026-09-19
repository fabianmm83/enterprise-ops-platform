package com.torotech.enterpriseops.controller;

import com.torotech.enterpriseops.dto.request.CreateProjectRequest;
import com.torotech.enterpriseops.dto.request.UpdateProjectRequest;
import com.torotech.enterpriseops.dto.response.ProjectResponse;
import com.torotech.enterpriseops.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Gestión de proyectos")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Crear proyecto",
               description = "Solo MANAGER o ADMIN pueden crear proyectos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proyecto creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication
    ) {
        ProjectResponse response = projectService.createProject(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar proyectos",
               description = "MANAGER/ADMIN ven todos. USER ve solo donde participa.")
    public ResponseEntity<Page<ProjectResponse>> listProjects(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectService.listProjects(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver proyecto por ID")
    public ResponseEntity<ProjectResponse> getProject(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectService.getProject(id, authentication.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proyecto",
               description = "Solo el dueño, MANAGER o ADMIN pueden editar")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectService.updateProject(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Archivar proyecto (solo ADMIN)",
               description = "Soft delete: cambia status a ARCHIVED")
    public ResponseEntity<Void> archiveProject(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        projectService.archiveProject(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}