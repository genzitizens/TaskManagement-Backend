package com.task_management.service.impl;


import com.task_management.dto.NoteCreateReq;
import com.task_management.dto.NoteRes;
import com.task_management.entity.Note;
import com.task_management.mapper.NoteMapper;
import com.task_management.repository.NoteRepository;
import com.task_management.repository.ProjectRepository;
import com.task_management.repository.TaskRepository;
import com.task_management.service.NoteService;
import com.task_management.exception.BadRequestException;
import com.task_management.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service @RequiredArgsConstructor @Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteRepository notes;
    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final NoteMapper mapper;

    @Override
    public NoteRes create(NoteCreateReq req) {
        boolean hasProject = req.projectId() != null;
        boolean hasTask = req.taskId() != null;
        if (hasProject == hasTask) throw new BadRequestException("Provide either projectId or taskId");

        var n = new Note();
        if (hasProject) {
            var p = projects.findById(req.projectId()).orElseThrow(() -> new NotFoundException("Project not found"));
            n.setProject(p);
        } else {
            var t = tasks.findById(req.taskId()).orElseThrow(() -> new NotFoundException("Task not found"));
            n.setTask(t);
        }
        n.setBody(req.body());
        return mapper.toRes(notes.save(n));
    }

    @Override
    public Page<NoteRes> listForProject(UUID projectId, Pageable pageable) {
        if (!projects.existsById(projectId)) throw new NotFoundException("Project not found");
        return notes.findByProjectIdOrderByCreatedAtDesc(projectId, pageable).map(mapper::toRes);
    }

    @Override
    public Page<NoteRes> listForTask(UUID taskId, Pageable pageable) {
        if (!tasks.existsById(taskId)) throw new NotFoundException("Task not found");
        return notes.findByTaskIdOrderByCreatedAtDesc(taskId, pageable).map(mapper::toRes);
    }

    @Override
    public void delete(UUID noteId) {
        if (!notes.existsById(noteId)) throw new NotFoundException("Note not found");
        notes.deleteById(noteId);
    }
}
