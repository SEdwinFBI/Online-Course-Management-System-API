package com.edwinbaquiax.courseadministratorservice.exceptions;

public class ModuleAlreadyExistException extends RuntimeException {
    public ModuleAlreadyExistException(String message) {
        super(message);
    }
}
