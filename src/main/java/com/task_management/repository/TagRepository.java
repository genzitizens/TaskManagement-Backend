package com.task_management.repository;

import com.task_management.entity.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Page<Tag> findByProjectIdOrderByEndAtAsc(UUID projectId, Pageable pageable);

    List<Tag> findByProjectId(UUID projectId);

    Page<Tag> findByEndAtBetweenOrderByEndAtAsc(Instant start, Instant end, Pageable pageable);

    @Query("""
      select t from Tag t
      where t.project.id = :projectId and
            (lower(t.title) like lower(concat('%', :q, '%'))
             or lower(t.description) like lower(concat('%', :q, '%')))
      """)
    Page<Tag> searchInProject(@Param("projectId") UUID projectId, @Param("q") String q, Pageable pageable);

    boolean existsByIdAndProjectId(UUID tagId, UUID projectId);
}
