package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.user.RegisterUserDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserResponseDTO;
import com.edwinbaquiax.courseadministratorservice.services.user.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.edwinbaquiax.courseadministratorservice.interceptors.ValidationBindingResult.validation;
import static com.edwinbaquiax.courseadministratorservice.models.mappers.UserProfile.registerUserDtoToUserEntity;

/**
 * Controlador REST para el registro de nuevos usuarios en la plataforma.
 *
 * <p>Expone el endpoint <b>/api/v1/register</b> para crear usuarios a partir
 * de la información enviada en el cuerpo de la petición.</p>
 */
@RestController
@RequestMapping("/api/v1/register")
public class RegisterController {

    @Autowired
    private IUserService userService;


    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param user datos del usuario a registrar
     * @param bindingResult resultado de la validación de campos
     * @return
     * <ul>
     *     <li><b>201 (CREATED)</b> con los datos del usuario creado si el registro es exitoso</li>
     *     <li><b>400 (BAD REQUEST)</b> con los errores de validación si los datos son inválidos</li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<?> register(@Valid  @RequestBody RegisterUserDTO user, BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()){
            return validation(bindingResult);
        }
        UserResponseDTO userCreated= userService.save(registerUserDtoToUserEntity(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }


}
