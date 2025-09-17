package com.task_management.repository;

import com.task_management.entity.Task;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    Page<Task> findByProjectIdOrderByEndAtAsc(UUID projectId, Pageable pageable);
    List<Task> findByProjectId(UUID projectId);
    Page<Task> findByEndAtBetweenOrderByEndAtAsc(Instant start, Instant end, Pageable pageable);
    @Query("""
      select t from Task t
      where t.project.id = :projectId and
            (lower(t.title) like lower(concat('%', :q, '%'))
             or lower(t.description) like lower(concat('%', :q, '%')))
      """)
    Page<Task> searchInProject(@Param("projectId") UUID projectId, @Param("q") String q, Pageable pageable);

    @Query("select count(t) from Task t where t.project.id = :projectId and t.isActivity = true")
    long countActivities(@Param("projectId") UUID projectId);

    boolean existsByIdAndProjectId(UUID taskId, UUID projectId);
}