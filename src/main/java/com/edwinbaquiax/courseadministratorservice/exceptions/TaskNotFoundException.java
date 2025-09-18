package com.edwinbaquiax.courseadministratorservice.exceptions;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException() {
        super("Tarea no encontrada");
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
