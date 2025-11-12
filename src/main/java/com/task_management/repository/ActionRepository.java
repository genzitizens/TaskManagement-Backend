package com.task_management.repository;

import com.task_management.entity.Action;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActionRepository extends JpaRepository<Action, UUID> {
    Page<Action> findByTaskIdOrderByDayAsc(UUID taskId, Pageable pageable);
    
    @Query("select count(a) from Action a where a.task.id = :taskId")
    long countByTaskId(@Param("taskId") UUID taskId);
    
    @Query("""
      select a from Action a
      where a.task.id = :taskId and
            lower(a.details) like lower(concat('%', :q, '%'))
      order by a.day asc
      """)
    Page<Action> searchInTask(@Param("taskId") UUID taskId, @Param("q") String q, Pageable pageable);
    
    boolean existsByIdAndTaskId(UUID actionId, UUID taskId);
}