package com.task_management.repository;

import com.task_management.entity.Project;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @EntityGraph(attributePaths = {"tasks", "tags"})
    Page<Project> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"tasks", "tags"})
    java.util.Optional<Project> findById(UUID id);

    boolean existsByNameIgnoreCase(String name);
}
