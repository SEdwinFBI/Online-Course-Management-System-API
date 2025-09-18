package com.edwinbaquiax.courseadministratorservice.exceptions;

public class AssignmentAlreadyExistException extends RuntimeException{
    public AssignmentAlreadyExistException() {
        super("La ya existe una Asignacion");
    }

    public AssignmentAlreadyExistException(String message) {
        super(message);
    }
}
