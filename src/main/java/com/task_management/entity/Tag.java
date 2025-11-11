package com.task_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tag",
        indexes = {
                @Index(name = "idx_tag_project", columnList = "project_id"),
                @Index(name = "idx_tag_start_at", columnList = "start_at"),
                @Index(name = "idx_tag_end_at", columnList = "end_at")
        }
)
public class Tag {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tag_project"))
    private Project project;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "is_activity", nullable = false)
    private boolean activity;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "start_day", nullable = false)
    private Integer startDay;

    @Column(name = "end_day", nullable = false)
    private Integer endDay;

    @Column(length = 32)
    private String color;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
