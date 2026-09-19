package com.torotech.enterpriseops.controller;

import com.torotech.enterpriseops.dto.request.CreateTaskRequest;
import com.torotech.enterpriseops.dto.request.UpdateTaskRequest;
import com.torotech.enterpriseops.dto.request.UpdateTaskStatusRequest;
import com.torotech.enterpriseops.dto.response.TaskResponse;
import com.torotech.enterpriseops.dto.response.TaskSummaryResponse;
import com.torotech.enterpriseops.service.TaskService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Gestión de tareas por proyecto")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Crear tarea en proyecto",
               description = "Solo MANAGER, ADMIN o el dueño del proyecto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication
    ) {
        TaskResponse response = taskService.createTask(projectId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/projects/{projectId}/tasks")
    @Operation(summary = "Listar tareas de un proyecto")
    public ResponseEntity<Page<TaskSummaryResponse>> listTasksByProject(
            @PathVariable UUID projectId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.listTasksByProject(projectId, pageable));
    }

    @GetMapping("/tasks/me")
    @Operation(summary = "Mis tareas asignadas",
               description = "Devuelve las tareas asignadas al usuario autenticado")
    public ResponseEntity<Page<TaskSummaryResponse>> listMyTasks(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.listMyTasks(authentication.getName(), pageable));
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Ver tarea por ID")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.getTask(id, authentication.getName()));
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "Actualizar tarea",
               description = "Asignado, dueño del proyecto, MANAGER o ADMIN")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, request, authentication.getName()));
    }

    @PatchMapping("/tasks/{id}/status")
    @Operation(summary = "Cambiar status de tarea",
               description = "Asignado, dueño del proyecto, MANAGER o ADMIN")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request, authentication.getName()));
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar tarea (solo ADMIN)")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}