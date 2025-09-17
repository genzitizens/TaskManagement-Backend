package com.task_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(
        name = "note",
        indexes = {
                @Index(name = "idx_note_project", columnList = "project_id"),
                @Index(name = "idx_note_task", columnList = "task_id")
        }
)
public class Note {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    /** exactly one of project or task must be set (enforced below in @PrePersist/@PreUpdate) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",
            foreignKey = @ForeignKey(name = "fk_note_project"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",
            foreignKey = @ForeignKey(name = "fk_note_task"))
    private Task task;

    @Column(columnDefinition = "text", nullable = false)
    private String body;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** guard: exactly one target (project XOR task) */
    @PrePersist @PreUpdate
    private void validateTarget() {
        boolean hasProject = this.project != null;
        boolean hasTask = this.task != null;
        if (hasProject == hasTask) { // both true or both false
            throw new IllegalStateException("Note must reference exactly one of {project, task}");
        }
    }
}
