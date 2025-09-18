package com.edwinbaquiax.courseadministratorservice.exceptions;

public class EnrollmentExistException extends RuntimeException{
    public EnrollmentExistException() {
        super("ya existe una inscripcion activa en este curso");
    }

    public EnrollmentExistException(String message) {
        super(message);
    }
}
