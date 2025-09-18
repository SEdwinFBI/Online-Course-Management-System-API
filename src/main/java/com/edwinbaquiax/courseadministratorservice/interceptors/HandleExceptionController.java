package com.edwinbaquiax.courseadministratorservice.interceptors;


import com.edwinbaquiax.courseadministratorservice.exceptions.CourseNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.RoleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UsernameIsExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.Error;

import java.util.Date;


@RestControllerAdvice
public class HandleExceptionController {

    @ExceptionHandler({UsernameIsExistsException.class})
    public ResponseEntity<Error> userExist(Exception e) {
        Error err = new Error();
        err.setDate(new Date());
        err.setError("Username en uso, Digite uno diferente");
        err.setMessage(e.getMessage());
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Error> userNotFound(Exception e) {
        Error err = new Error();
        err.setDate(new Date());
        err.setError("Error, ocurrio un problema al realizar la solicitud");
        err.setMessage(e.getMessage());
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler({RoleNotFoundException.class})
    public ResponseEntity<Error> roleNotFound(Exception e) {
        Error err = new Error();
        err.setDate(new Date());
        err.setError("El rol no esta disponible o no existe");
        err.setMessage(e.getMessage());
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler({CourseNotFoundException.class})

    public ResponseEntity<Error> courseNotFound(Exception e) {
        Error err = new Error();
        err.setDate(new Date());
        err.setError("El curso no esta disponible o no existe");
        err.setMessage(e.getMessage());
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Error> authDenied(Exception e) {
        Error err = new Error();
        err.setDate(new Date());
        err.setError("Sin Autorizacion para acceder a este recurso");
        err.setMessage(e.getMessage());
        err.setStatus(HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Error> integrityViolation(Exception e) {
        Error err = new Error();
        err.setDate(new Date());
        err.setError("Ocurrio un problema al crear algun recurso en BD");
        err.setMessage(e.getMessage());
        err.setStatus(HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> genericError(Exception e) {
        Error err = new Error();
        err.setDate(new Date());
        err.setError("Error Interno, porfavor intente de nuevo mas tarde");
        err.setMessage(e.getMessage());
        err.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.internalServerError().body(err);
    }

}
