package com.task_management.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) { super(msg); }
}