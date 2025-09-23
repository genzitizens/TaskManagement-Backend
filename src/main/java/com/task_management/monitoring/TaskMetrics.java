package com.task_management.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Central place to register and update custom Micrometer counters for the application.
 */
@Component
public class TaskMetrics {

    private final Counter tasksCreated;
    private final Counter tasksUpdated;
    private final Counter tasksDeleted;

    public TaskMetrics(MeterRegistry meterRegistry) {
        this.tasksCreated = Counter.builder("task_management.tasks.created")
                .description("Number of tasks created")
                .register(meterRegistry);
        this.tasksUpdated = Counter.builder("task_management.tasks.updated")
                .description("Number of tasks updated")
                .register(meterRegistry);
        this.tasksDeleted = Counter.builder("task_management.tasks.deleted")
                .description("Number of tasks deleted")
                .register(meterRegistry);
    }

    public void incrementCreated() {
        tasksCreated.increment();
    }

    public void incrementUpdated() {
        tasksUpdated.increment();
    }

    public void incrementDeleted() {
        tasksDeleted.increment();
    }
}
