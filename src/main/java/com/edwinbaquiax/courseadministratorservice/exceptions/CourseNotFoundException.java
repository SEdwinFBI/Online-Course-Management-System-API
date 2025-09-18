package com.edwinbaquiax.courseadministratorservice.exceptions;

public class CourseNotFoundException extends RuntimeException{
    public CourseNotFoundException() {
        super("No se encontro ningun recurso disponible");
    }

    public CourseNotFoundException(String message) {
        super(message);
    }
}
