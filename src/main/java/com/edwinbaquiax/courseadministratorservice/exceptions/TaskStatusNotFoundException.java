package com.edwinbaquiax.courseadministratorservice.exceptions;

public class TaskStatusNotFoundException extends RuntimeException {
    public TaskStatusNotFoundException(String message) {
        super(message);
    }
}
