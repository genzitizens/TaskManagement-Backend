package com.task_management.monitoring;

import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Contributes domain specific information to the actuator health endpoint while
 * also ensuring the persistence layer can be reached.
 */
@Component
@RequiredArgsConstructor
public class ApplicationHealthIndicator implements HealthIndicator {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Override
    public Health health() {
        try {
            long projectCount = projectRepository.count();
            long taskCount = taskRepository.count();
            return Health.up()
                    .withDetail("projects.count", projectCount)
                    .withDetail("tasks.count", taskCount)
                    .build();
        } catch (Exception exception) {
            return Health.down(exception).build();
        }
    }
}
