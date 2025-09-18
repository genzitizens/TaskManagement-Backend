package com.task_management.repository;

import com.task_management.entity.Project;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Page<Project> findAllByOrderByCreatedAtDesc(Pageable pageable);
    boolean existsByNameIgnoreCase(String name);
}