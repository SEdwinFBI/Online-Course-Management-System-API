package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleResponseDTO;
import com.edwinbaquiax.courseadministratorservice.services.module.IModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.edwinbaquiax.courseadministratorservice.interceptors.ValidationBindingResult.validation;

/**
 * Controlador REST para la gestión de módulos dentro de cursos.
 *
 * <p>Expone endpoints bajo la ruta <b>/api/v1/modules</b> para:</p>
 * <ul>
 *     <li>Crear, actualizar y eliminar módulos (solo docentes y administradores)</li>
 *     <li>Consultar módulos por ID</li>
 *     <li>Listar módulos de un curso específico</li>
 *     <li>Listar todos los módulos (solo administradores)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/modules")
public class ModuleController {

    @Autowired
    private IModuleService moduleService;

    /**
     * Crea un nuevo módulo.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param request datos del módulo a crear
     * @return módulo creado con código HTTP 201 (CREATED)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<?> createModule(@Valid @RequestBody ModuleRequestDTO request, BindingResult bindingResult) {
        if(bindingResult.hasFieldErrors()){
            return validation(bindingResult);
        }
        ModuleResponseDTO body = moduleService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }


    /**
     * Actualiza un módulo existente.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param moduleId identificador del módulo
     * @param request datos actualizados del módulo
     * @return módulo actualizado con código HTTP 200 (OK)
     */
    @PutMapping("/{moduleId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ModuleResponseDTO> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleRequestDTO request) {
        ModuleResponseDTO body = moduleService.updateModule(moduleId, request);
        return ResponseEntity.ok(body);
    }

    /**
     * Elimina un módulo existente.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param moduleId identificador del módulo
     * @return código HTTP 204 (NO CONTENT) si la eliminación es exitosa
     */
    @DeleteMapping("/{moduleId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca un módulo por su identificador.
     *
     * @param moduleId identificador del módulo
     * @return módulo encontrado con código HTTP 200 (OK)
     */
    @GetMapping("/{moduleId}")
    public ResponseEntity<ModuleResponseDTO> findById(@PathVariable Long moduleId) {
        ModuleResponseDTO body = moduleService.findById(moduleId);
        return ResponseEntity.ok(body);
    }

    /**
     * Lista todos los módulos de un curso específico.
     *
     * @param courseId identificador del curso
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de módulos del curso
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<ModuleResponseDTO>> findByCourseId(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ModuleResponseDTO> body = moduleService.findAllModulesByCourseId(courseId, page, size);
        return ResponseEntity.ok(body);
    }

    /**
     * Lista todos los módulos registrados en el sistema.
     *
     * <p>Requiere rol <b>ADMIN</b>.</p>
     *
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de todos los módulos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<ModuleResponseDTO>> findAllModules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ModuleResponseDTO> body = moduleService.findAllModules(page, size);
        return ResponseEntity.ok(body);
    }
}