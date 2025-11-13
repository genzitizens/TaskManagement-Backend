package com.task_management.repository;

import com.task_management.entity.Note;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    // Notes attached to a project
    Page<Note> findByProjectIdOrderByCreatedAtDesc(UUID projectId, Pageable pageable);
    List<Note> findByProjectId(UUID projectId);
    // Notes attached to a task
    Page<Note> findByTaskIdOrderByCreatedAtDesc(UUID taskId, Pageable pageable);

    // For cascade checks in service layer
    long countByProjectId(UUID projectId);
    long countByTaskId(UUID taskId);
}