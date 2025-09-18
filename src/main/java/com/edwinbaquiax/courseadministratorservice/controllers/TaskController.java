package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskResponseDTO;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.task.ITaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de tareas (tasks).
 *
 * <p>Expone endpoints bajo la ruta <b>/api/v1/tasks</b> para:</p>
 * <ul>
 *     <li>Crear, actualizar y eliminar tareas (solo docentes y administradores)</li>
 *     <li>Consultar una tarea por su identificador</li>
 *     <li>Listar tareas de un módulo específico</li>
 *     <li>Listar tareas asignadas a un estudiante</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;
    @Autowired
    private CurrentUser currentUser;

    /**
     * Crea una nueva tarea dentro de un módulo específico.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param moduleId identificador del módulo al que pertenece la tarea
     * @param request datos de la tarea a crear
     * @return tarea creada con código HTTP 201 (CREATED)
     */
    @PostMapping("/module")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO body = taskService.createTask(request.getModuleId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Actualiza una tarea existente.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param taskId identificador de la tarea
     * @param request datos actualizados de la tarea
     * @return tarea actualizada con código HTTP 200 (OK)
     */
    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable String taskId,
            @Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO body = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(body);
    }

    /**
     * Elimina una tarea existente.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param taskId identificador de la tarea
     * @return código HTTP 204 (NO CONTENT) si la eliminación es exitosa
     */
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca una tarea por su identificador.
     *
     * @param taskId identificador de la tarea
     * @return tarea encontrada con código HTTP 200 (OK)
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable String taskId) {
        TaskResponseDTO body = taskService.findById(taskId);
        return ResponseEntity.ok(body);
    }

    /**
     * Lista todas las tareas de un módulo específico.
     *
     * @param moduleId identificador del módulo
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de tareas del módulo
     */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<Page<TaskResponseDTO>> findTasksByModule(
            @PathVariable Long moduleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TaskResponseDTO> body = taskService.findTasksByModule(moduleId, page, size);
        return ResponseEntity.ok(body);
    }

    /**
     * Lista todas las tareas asignadas al estudiante autenticado.
     *
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de tareas del estudiante
     */
    @GetMapping("/student")
    public ResponseEntity<Page<TaskResponseDTO>> findTasksByStudent(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TaskResponseDTO> body = taskService.findTasksByStudent(currentUser.getUserId(), page, size);
        return ResponseEntity.ok(body);
    }
}