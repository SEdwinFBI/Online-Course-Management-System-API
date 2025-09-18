package com.edwinbaquiax.courseadministratorservice.exceptions;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException() {
        super("El rol a usar no existe");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
