package com.task_management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.task_management.entity.Project;
import com.task_management.entity.Task;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryIntegrationTest {
    @Autowired
    private TaskRepository tasks;

    @Autowired
    private ProjectRepository projects;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setName("Demo");
        project.setDescription("desc");
        project = projects.saveAndFlush(project);
    }

    @Test
    void persistsAndReadsActivityFlag() {
        var task = buildTask(true);
        tasks.saveAndFlush(task);

        var reloaded = tasks.findById(task.getId()).orElseThrow();
        assertThat(reloaded.isActivity()).isTrue();
    }

    @Test
    void countActivitiesHonorsFlag() {
        var activeTask = tasks.saveAndFlush(buildTask(true));
        tasks.saveAndFlush(buildTask(false));

        long count = tasks.countActivities(project.getId());
        assertThat(count).isEqualTo(1L);

        var fetched = tasks.findById(activeTask.getId()).orElseThrow();
        assertThat(fetched.isActivity()).isTrue();
    }

    private Task buildTask(boolean activity) {
        var task = new Task();
        task.setProject(project);
        task.setTitle(activity ? "Active" : "Passive");
        task.setDescription("desc");
        task.setActivity(activity);
        task.setEndAt(Instant.parse("2024-01-01T00:00:00Z"));
        return task;
    }
}
