package com.edwinbaquiax.courseadministratorservice.controllers;


import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.assignment.IAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de asignaciones (Assignments).
 * da endpoints para crear, actualizar, eliminar, consultar y enviar tareas.
 *
 * <p>Versionado  <b>/api/v1/assignments</b></p>
 *
 * Funcionalidades principales:
 * <ul>
 *     <li>CRUD de asignaciones</li>
 *     <li>Búsqueda de asignaciones por estudiante o por tarea</li>
 *     <li>Envío (submit) de una asignación con calificación</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/assignments")
public class AssignmentController {

    @Autowired
    private  IAssignmentService assignmentService;
    @Autowired
    private CurrentUser currentUser;

    /**
     * Crea una nueva asignación.
     *
     * @param request datos de la asignación a crear
     * @return la asignación creada con código HTTP 201 (CREATED)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<AssignmentResponseDTO> createAssignment(
            @Valid @RequestBody AssignmentRequestDTO request) {
        AssignmentResponseDTO body = assignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }


    /**
     * Actualiza una asignación existente.
     *
     * @param assignmentId identificador de la asignación
     * @param request datos actualizados de la asignación
     * @return la asignación actualizada con código HTTP 200 (OK)
     */
    @PutMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponseDTO> updateAssignment(
            @PathVariable String assignmentId,
            @Valid @RequestBody AssignmentRequestDTO request) {
        AssignmentResponseDTO body = assignmentService.updateAssignment(assignmentId, request);
        return ResponseEntity.ok(body);
    }


    /**
     * Elimina una asignación específica.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param assignmentId identificador de la asignación
     * @return código HTTP 204 (NO CONTENT) si se elimina correctamente
     */
    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }



    /**
     * Busca una asignación por su ID.
     *
     * @param assignmentId identificador de la asignación
     * @return la asignación encontrada con código HTTP 200 (OK)
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponseDTO> findById(@PathVariable String assignmentId) {
        AssignmentResponseDTO body = assignmentService.findById(assignmentId);
        return ResponseEntity.ok(body);
    }


    /**
     * Obtiene las asignaciones asociadas al estudiante autenticado.
     *
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de asignaciones del estudiante
     */
    @GetMapping("/student")
    public ResponseEntity<Page<AssignmentResponseDTO>> findAssignmentsByStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AssignmentResponseDTO> body = assignmentService.findAssignmentsByStudent(currentUser.getUserId(), page, size);
        return ResponseEntity.ok(body);
    }


    /**
     * Obtiene las asignaciones relacionadas con una tarea específica.
     *
      * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param taskId identificador de la tarea
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de asignaciones vinculadas a la tarea
     */
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Page<AssignmentResponseDTO>> findAssignmentsByTask(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AssignmentResponseDTO> body = assignmentService.findAssignmentsByTask(taskId, page, size);
        return ResponseEntity.ok(body);
    }

    /**
     * Envía (submit) una asignación con su calificación.
     *
     * @param assignmentId identificador de la asignación
     * @param score calificación asignada
     * @return la asignación actualizada con su estado de entrega
     */
    @PostMapping("/{assignmentId}/submit")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<AssignmentResponseDTO> submitAssignment(
            @PathVariable String assignmentId,
            @RequestParam(required = true) Double score) {
        AssignmentResponseDTO body = assignmentService.submitAssignment(assignmentId, score);
        return ResponseEntity.ok(body);
    }
}