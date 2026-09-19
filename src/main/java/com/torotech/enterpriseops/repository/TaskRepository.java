package com.torotech.enterpriseops.repository;

import com.torotech.enterpriseops.entity.Project;
import com.torotech.enterpriseops.entity.Task;
import com.torotech.enterpriseops.entity.TaskPriority;
import com.torotech.enterpriseops.entity.TaskStatus;
import com.torotech.enterpriseops.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByProject(Project project, Pageable pageable);

    Page<Task> findByProjectAndStatus(Project project, TaskStatus status, Pageable pageable);

    Page<Task> findByAssignee(User assignee, Pageable pageable);

    Page<Task> findByAssigneeAndStatus(User assignee, TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    long countByProjectAndStatus(Project project, TaskStatus status);

    boolean existsByProjectAndAssignee(Project project, User assignee);
}