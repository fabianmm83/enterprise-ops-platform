package com.torotech.enterpriseops.service;

import com.torotech.enterpriseops.dto.request.CreateTaskRequest;
import com.torotech.enterpriseops.dto.request.UpdateTaskRequest;
import com.torotech.enterpriseops.dto.request.UpdateTaskStatusRequest;
import com.torotech.enterpriseops.dto.response.TaskResponse;
import com.torotech.enterpriseops.dto.response.TaskSummaryResponse;
import com.torotech.enterpriseops.entity.*;
import com.torotech.enterpriseops.exception.TaskNotFoundException;
import com.torotech.enterpriseops.exception.UserNotFoundException;
import com.torotech.enterpriseops.repository.TaskRepository;
import com.torotech.enterpriseops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    @Transactional
    public TaskResponse createTask(UUID projectId, CreateTaskRequest request, String currentUserEmail) {
        Project project = projectService.findProjectById(projectId);
        User currentUser = findUserByEmail(currentUserEmail);

        boolean isAdminOrManager = currentUser.getRole() == Role.ADMIN
                || currentUser.getRole() == Role.MANAGER;
        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());

        if (!isAdminOrManager && !isOwner) {
            throw new SecurityException("Solo el dueño, MANAGER o ADMIN pueden crear tareas");
        }

        User assignee = null;
        if (request.assigneeId() != null) {
            assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new UserNotFoundException(request.assigneeId()));
        }

        Task task = Task.builder()
                .title(request.title().trim())
                .description(request.description())
                .project(project)
                .assignee(assignee)
                .status(TaskStatus.TODO)
                .priority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM)
                .dueDate(request.dueDate())
                .build();

        Task saved = taskRepository.save(task);
        log.info("Tarea creada: {} en proyecto {} por {}", saved.getTitle(), project.getName(), currentUserEmail);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TaskSummaryResponse> listTasksByProject(UUID projectId, Pageable pageable) {
        Project project = projectService.findProjectById(projectId);
        return taskRepository.findByProject(project, pageable).map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public Page<TaskSummaryResponse> listMyTasks(String currentUserEmail, Pageable pageable) {
        User currentUser = findUserByEmail(currentUserEmail);
        return taskRepository.findByAssignee(currentUser, pageable).map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID id, String currentUserEmail) {
        Task task = findTaskById(id);
        User currentUser = findUserByEmail(currentUserEmail);

        boolean isAdminOrManager = currentUser.getRole() == Role.ADMIN
                || currentUser.getRole() == Role.MANAGER;
        boolean isAssignee = task.getAssignee() != null
                && task.getAssignee().getId().equals(currentUser.getId());
        boolean isProjectOwner = task.getProject().getOwner().getId().equals(currentUser.getId());

        if (!isAdminOrManager && !isAssignee && !isProjectOwner) {
            throw new SecurityException("No tienes acceso a esta tarea");
        }

        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request, String currentUserEmail) {
        Task task = findTaskById(id);
        User currentUser = findUserByEmail(currentUserEmail);

        boolean isAdminOrManager = currentUser.getRole() == Role.ADMIN
                || currentUser.getRole() == Role.MANAGER;
        boolean isAssignee = task.getAssignee() != null
                && task.getAssignee().getId().equals(currentUser.getId());
        boolean isProjectOwner = task.getProject().getOwner().getId().equals(currentUser.getId());

        if (!isAdminOrManager && !isAssignee && !isProjectOwner) {
            throw new SecurityException("No puedes editar esta tarea");
        }

        if (request.title() != null) {
            task.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.priority() != null) {
            // Solo MANAGER+ o dueño pueden cambiar prioridad
            if (!isAdminOrManager && !isProjectOwner) {
                throw new SecurityException("Solo el dueño del proyecto o MANAGER+ pueden cambiar la prioridad");
            }
            task.setPriority(request.priority());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.assigneeId() != null) {
            // Solo MANAGER+ o dueño pueden reasignar
            if (!isAdminOrManager && !isProjectOwner) {
                throw new SecurityException("Solo el dueño del proyecto o MANAGER+ pueden reasignar tareas");
            }
            User newAssignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new UserNotFoundException(request.assigneeId()));
            task.setAssignee(newAssignee);
        }

        Task updated = taskRepository.save(task);
        log.info("Tarea actualizada: {} por {}", updated.getTitle(), currentUserEmail);

        return toResponse(updated);
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID id, UpdateTaskStatusRequest request, String currentUserEmail) {
        Task task = findTaskById(id);
        User currentUser = findUserByEmail(currentUserEmail);

        boolean isAdminOrManager = currentUser.getRole() == Role.ADMIN
                || currentUser.getRole() == Role.MANAGER;
        boolean isAssignee = task.getAssignee() != null
                && task.getAssignee().getId().equals(currentUser.getId());
        boolean isProjectOwner = task.getProject().getOwner().getId().equals(currentUser.getId());

        if (!isAdminOrManager && !isAssignee && !isProjectOwner) {
            throw new SecurityException("No puedes cambiar el status de esta tarea");
        }

        task.setStatus(request.status());
        Task updated = taskRepository.save(task);
        log.info("Status de tarea {} cambiado a {} por {}", updated.getTitle(), request.status(), currentUserEmail);

        return toResponse(updated);
    }

    @Transactional
    public void deleteTask(UUID id, String currentUserEmail) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
        log.info("Tarea eliminada: {} por {}", task.getTitle(), currentUserEmail);
    }

    // ============ HELPERS ============

    public Task findTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    private TaskResponse toResponse(Task task) {
        TaskSummaryResponse.AssigneeSummary assigneeSummary = null;
        if (task.getAssignee() != null) {
            User a = task.getAssignee();
            assigneeSummary = new TaskSummaryResponse.AssigneeSummary(
                    a.getId(), a.getEmail(), a.getFullName()
            );
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getProject().getId(),
                task.getProject().getName(),
                assigneeSummary,
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private TaskSummaryResponse toSummary(Task task) {
        TaskSummaryResponse.AssigneeSummary assigneeSummary = null;
        if (task.getAssignee() != null) {
            User a = task.getAssignee();
            assigneeSummary = new TaskSummaryResponse.AssigneeSummary(
                    a.getId(), a.getEmail(), a.getFullName()
            );
        }

        return new TaskSummaryResponse(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getPriority(),
                assigneeSummary,
                task.getDueDate()
        );
    }
}