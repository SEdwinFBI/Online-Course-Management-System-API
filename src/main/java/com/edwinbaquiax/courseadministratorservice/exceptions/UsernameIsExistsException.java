package com.edwinbaquiax.courseadministratorservice.exceptions;

import org.springframework.web.server.ServerErrorException;

public class UsernameIsExistsException extends RuntimeException {
    public UsernameIsExistsException() {
        super("Nombre de usuario o correo ya registrados");
    }
}
