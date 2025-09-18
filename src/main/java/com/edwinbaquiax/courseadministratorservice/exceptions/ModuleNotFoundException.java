package com.edwinbaquiax.courseadministratorservice.exceptions;

public class ModuleNotFoundException extends RuntimeException{
    public ModuleNotFoundException() {
        super("Modulo no encontrado");
    }

    public ModuleNotFoundException(String message) {
        super(message);
    }
}
