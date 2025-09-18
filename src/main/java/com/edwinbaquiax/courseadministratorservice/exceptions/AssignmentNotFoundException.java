package com.edwinbaquiax.courseadministratorservice.exceptions;

public class AssignmentNotFoundException extends RuntimeException{
    public AssignmentNotFoundException() {
        super("Asignacion no disponible o no existe");
    }

    public AssignmentNotFoundException(String message) {
        super(message);
    }
}
