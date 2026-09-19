package com.torotech.enterpriseops.repository;

import com.torotech.enterpriseops.entity.Project;
import com.torotech.enterpriseops.entity.ProjectStatus;
import com.torotech.enterpriseops.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findByOwner(User owner, Pageable pageable);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    Page<Project> findByOwnerAndStatus(User owner, ProjectStatus status, Pageable pageable);

    /**
     * Proyectos donde el usuario tiene tareas asignadas.
     *
     * Se usa EXISTS en lugar de JOIN DISTINCT porque PostgreSQL no permite
     * SELECT DISTINCT con ORDER BY por columnas que no están en el select.
     * Con EXISTS evitamos los duplicados sin necesidad de DISTINCT.
     */
    @Query("""
            SELECT p FROM Project p
            WHERE EXISTS (
                SELECT 1 FROM Task t
                WHERE t.project = p AND t.assignee.id = :userId
            )
            """)
    Page<Project> findProjectsByTaskAssignee(@Param("userId") UUID userId, Pageable pageable);
}