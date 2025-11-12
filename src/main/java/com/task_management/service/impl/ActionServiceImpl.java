package com.task_management.service.impl;

import com.task_management.dto.ActionCreateReq;
import com.task_management.dto.ActionRes;
import com.task_management.dto.ActionUpdateReq;
import com.task_management.entity.Action;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import com.task_management.mapper.ActionMapper;
import com.task_management.repository.ActionRepository;
import com.task_management.repository.TaskRepository;
import com.task_management.service.ActionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service @RequiredArgsConstructor @Transactional
public class ActionServiceImpl implements ActionService {
    private final ActionRepository actions;
    private final TaskRepository tasks;
    private final ActionMapper mapper;

    @Override
    public ActionRes create(ActionCreateReq req) {
        var task = tasks.findById(req.taskId())
                .orElseThrow(() -> new NotFoundException("Task not found"));

        var action = new Action();
        action.setTask(task);
        action.setDetails(req.details().trim());
        if (action.getDetails().isBlank()) {
            throw new BadRequestException("Action details cannot be blank");
        }
        action.setDay(req.day());
        if (req.day() < 1) {
            throw new BadRequestException("Day must be at least 1");
        }

        var savedAction = actions.save(action);
        return mapper.toRes(savedAction);
    }

    @Override
    public ActionRes get(UUID id) {
        return mapper.toRes(actions.findById(id)
                .orElseThrow(() -> new NotFoundException("Action not found")));
    }

    @Override
    public Page<ActionRes> listInTask(UUID taskId, Pageable pageable) {
        if (!tasks.existsById(taskId)) {
            throw new NotFoundException("Task not found");
        }
        return actions.findByTaskIdOrderByDayAsc(taskId, pageable).map(mapper::toRes);
    }

    @Override
    public ActionRes update(UUID id, ActionUpdateReq req) {
        var action = actions.findById(id)
                .orElseThrow(() -> new NotFoundException("Action not found"));

        if (req.details() != null) {
            String details = req.details().trim();
            if (details.isBlank()) {
                throw new BadRequestException("Action details cannot be blank");
            }
            action.setDetails(details);
        }

        if (req.day() != null) {
            if (req.day() < 1) {
                throw new BadRequestException("Day must be at least 1");
            }
            action.setDay(req.day());
        }

        var updatedAction = actions.save(action);
        return mapper.toRes(updatedAction);
    }

    @Override
    public void delete(UUID id) {
        if (!actions.existsById(id)) {
            throw new NotFoundException("Action not found");
        }
        actions.deleteById(id);
    }
}