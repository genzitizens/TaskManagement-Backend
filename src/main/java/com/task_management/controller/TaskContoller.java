package com.task_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TaskContoller {

    @GetMapping
    public ResponseEntity<?> getTask() {
        return ResponseEntity.ok("Can be done");
    }

    // ! Why is my pLugin not working
    @PostMapping
    public ResponseEntity<?> addTask() {
        return ResponseEntity.ok("Can be done");
    }
}
