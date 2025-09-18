package com.edwinbaquiax.courseadministratorservice.interceptors;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

@Component
public class ValidationBindingResult{
    public static ResponseEntity<?> validation(BindingResult bindingResult){
        Map<String,String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errors.put(fieldError.getField(), String.format("El campo %s %s", fieldError.getField(), fieldError.getDefaultMessage()));
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
