package com.torotech.enterpriseops.service;

import com.torotech.enterpriseops.dto.request.CreateProjectRequest;
import com.torotech.enterpriseops.dto.request.UpdateProjectRequest;
import com.torotech.enterpriseops.dto.response.ProjectResponse;
import com.torotech.enterpriseops.entity.*;
import com.torotech.enterpriseops.exception.ProjectNotFoundException;
import com.torotech.enterpriseops.exception.UserNotFoundException;
import com.torotech.enterpriseops.repository.ProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, String currentUserEmail) {
        User owner = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("email", currentUserEmail));

        Project project = Project.builder()
                .name(request.name().trim())
                .description(request.description())
                .status(ProjectStatus.ACTIVE)
                .owner(owner)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        Project saved = projectRepository.save(project);
        log.info("Proyecto creado: {} por {}", saved.getName(), currentUserEmail);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> listProjects(String currentUserEmail, Pageable pageable) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("email", currentUserEmail));

        // ADMIN y MANAGER ven todos los proyectos
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.MANAGER) {
            return projectRepository.findAll(pageable).map(this::toResponse);
        }

        // USER solo ve proyectos donde tiene tareas asignadas
        return projectRepository
                .findProjectsByTaskAssignee(currentUser.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID id, String currentUserEmail) {
        Project project = findProjectById(id);
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("email", currentUserEmail));

        // USER solo puede ver proyectos donde participa
        if (currentUser.getRole() == Role.USER) {
            boolean hasAccess = project.getOwner().getId().equals(currentUser.getId())
                    || taskRepository.existsByProjectAndAssignee(project, currentUser);

            if (!hasAccess) {
                throw new SecurityException("No tienes acceso a este proyecto");
            }
        }

        return toResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(UUID id, UpdateProjectRequest request, String currentUserEmail) {
        Project project = findProjectById(id);
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("email", currentUserEmail));

        boolean isAdminOrManager = currentUser.getRole() == Role.ADMIN
                || currentUser.getRole() == Role.MANAGER;
        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());

        if (!isAdminOrManager && !isOwner) {
            throw new SecurityException("Solo el dueño, MANAGER o ADMIN pueden editar este proyecto");
        }

        if (request.name() != null) {
            project.setName(request.name().trim());
        }
        if (request.description() != null) {
            project.setDescription(request.description());
        }
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        if (request.startDate() != null) {
            project.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            project.setEndDate(request.endDate());
        }

        Project updated = projectRepository.save(project);
        log.info("Proyecto actualizado: {} por {}", updated.getName(), currentUserEmail);

        return toResponse(updated);
    }

    @Transactional
    public void archiveProject(UUID id, String currentUserEmail) {
        Project project = findProjectById(id);
        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
        log.info("Proyecto archivado: {} por {}", project.getName(), currentUserEmail);
    }

    // ============ HELPERS ============

    public Project findProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    private ProjectResponse toResponse(Project project) {
        User owner = project.getOwner();
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                new ProjectResponse.UserSummary(
                        owner.getId(),
                        owner.getEmail(),
                        owner.getFullName()
                ),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}