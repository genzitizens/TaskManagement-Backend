package com.task_management.repository;

import com.task_management.entity.Note;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    // Notes attached to a project
    Page<Note> findByProjectIdOrderByCreatedAtDesc(UUID projectId, Pageable pageable);
    List<Note> findByProjectId(UUID projectId);
    // Notes attached to a task
    Page<Note> findByTaskIdOrderByCreatedAtDesc(UUID taskId, Pageable pageable);
    
    // Find all task-specific notes for a project (for import functionality)
    @Query("select n from Note n join fetch n.task t where t.project.id = :projectId and n.task is not null")
    List<Note> findTaskNotesByProjectId(@Param("projectId") UUID projectId);

    // For cascade checks in service layer
    long countByProjectId(UUID projectId);
    long countByTaskId(UUID taskId);
}