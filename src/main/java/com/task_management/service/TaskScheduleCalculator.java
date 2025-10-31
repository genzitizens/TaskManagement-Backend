package com.task_management.service;

import com.task_management.entity.Task;
import com.task_management.exception.BadRequestException;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class TaskScheduleCalculator {

    public void applyScheduleDays(Task task) {
        var project = task.getProject();
        if (project == null || project.getStartDate() == null) {
            throw new BadRequestException("Project start date is required");
        }
        var projectStart = project.getStartDate();
        var startDate = task.getStartAt().atZone(ZoneOffset.UTC).toLocalDate();
        var endDate = task.getEndAt().atZone(ZoneOffset.UTC).toLocalDate();

        int startDay = Math.toIntExact(ChronoUnit.DAYS.between(projectStart, startDate) + 1);
        if (startDay < 1) {
            throw new BadRequestException("startAt cannot be before the project start date");
        }
        int endDay = Math.toIntExact(ChronoUnit.DAYS.between(projectStart, endDate) + 1);
        if (endDay < startDay) {
            throw new BadRequestException("endAt cannot be before startAt");
        }

        task.setStartDay(startDay);
        task.setEndDay(endDay);
    }
}
