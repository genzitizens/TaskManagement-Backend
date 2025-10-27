package com.task_management.mapper;

import com.task_management.entity.Project;
import com.task_management.entity.Task;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void mapsActivityFlagCorrectly() {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).build();

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setProject(project);
        task.setTitle("Title");
        task.setDescription("Desc");
        task.setActivity(true);
        task.setStartAt(Instant.now());
        task.setEndAt(Instant.now());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());

        var res = mapper.toRes(task);

        assertThat(res.projectId()).isEqualTo(projectId);
        assertThat(res.activity()).isTrue();
    }
}
