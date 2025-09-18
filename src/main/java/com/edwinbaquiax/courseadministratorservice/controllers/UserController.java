package com.edwinbaquiax.courseadministratorservice.controllers;


import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserResponseDTO;
import com.edwinbaquiax.courseadministratorservice.services.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


//@CrossOrigin(origins = "http://")
/**
 * Controlador REST para la gestión de usuarios.
 *
 * <p>Expone endpoints bajo la ruta <b>/api/v1/users</b> para:</p>
 * <ul>
 *     <li>Listar todos los usuarios (solo administradores)</li>
 *     <li>Actualizar los roles de un usuario (solo administradores)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * <p>Requiere rol <b>ADMIN</b>.</p>
     *
     * @return lista de usuarios con código HTTP 200 (OK)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> list(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }


    /**
     * Actualiza los roles de un usuario específico.
     *
     * <p>Requiere rol <b>ADMIN</b>.</p>
     *
     * @param id identificador del usuario
     * @param roleNames conjunto de nombres de roles a asignar
     * @return usuario actualizado con código HTTP 200 (OK)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update_role/{id}")
    public ResponseEntity<UserResponseDTO> updateRoleName(@PathVariable Long id, @RequestBody Set<String> roleNames){

        UserResponseDTO user = userService.updateRoleUser(id, roleNames);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
