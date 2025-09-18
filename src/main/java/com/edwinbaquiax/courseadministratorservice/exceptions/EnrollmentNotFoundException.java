package com.edwinbaquiax.courseadministratorservice.exceptions;

public class EnrollmentNotFoundException extends RuntimeException{
    public EnrollmentNotFoundException() {
        super("No se encontro la inscripcion para el curso");
    }

    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}
