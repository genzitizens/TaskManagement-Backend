package com.task_management.service;

import com.task_management.dto.*;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteService {
    NoteRes create(NoteCreateReq req);
    Page<NoteRes> listForProject(UUID projectId, Pageable pageable);
    Page<NoteRes> listForTask(UUID taskId, Pageable pageable);
    NoteRes update(UUID noteId, NoteUpdateReq req);
    void delete(UUID noteId);
}