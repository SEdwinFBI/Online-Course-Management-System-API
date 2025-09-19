package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.enrollment.IEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador REST para la gestión de inscripciones (enrollments).
 *
 * <p>Expone endpoints bajo la ruta <b>/api/v1/enrollments</b> para:</p>
 * <ul>
 *     <li>Inscribir a un estudiante en un curso</li>
 *     <li>Cancelar una inscripción</li>
 *     <li>Obtener inscripciones de un estudiante autenticado</li>
 *     <li>Obtener inscripciones de un curso específico (solo docentes o administradores)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {
    @Autowired
    private IEnrollmentService enrollmentService;
    @Autowired
    private CurrentUser currentUser;

    /**
     * Inscribe a un estudiante en un curso.
     *
     * @param request datos de la inscripción (contiene IDs de curso y estudiante)
     * @return inscripción creada con código HTTP 201 (CREATED)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(@RequestBody EnrollmentRequestDTO request) {
        EnrollmentResponseDTO body = enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    /**
     * Inscribe a un estudiante en un curso.
     *
     * @param courseId  ID de curso
     * @return inscripción creada con código HTTP 201 (CREATED)
     */
    @PostMapping("/{courseId}")
    public ResponseEntity<EnrollmentResponseDTO> enrollment(@PathVariable Long courseId) {

        EnrollmentRequestDTO request= new EnrollmentRequestDTO(currentUser.getUserId(),courseId);
        EnrollmentResponseDTO body = enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Cancela una inscripción existente.
     *
     * @param enrollmentId identificador de la inscripción
     * @return código HTTP 204 (NO CONTENT) si se cancela correctamente
     */
    @DeleteMapping("/cancel/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }



    /**
     * Obtiene las inscripciones del estudiante autenticado.
     *
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de inscripciones del estudiante
     */
    @GetMapping("/students")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getEnrollmentsByStudent(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<EnrollmentResponseDTO> body = enrollmentService.getEnrollmentsByStudent(currentUser.getUserId(), page, size);
        return ResponseEntity.ok(body);
    }


    /**
     * Obtiene las inscripciones de un curso específico.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param courseId identificador del curso
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return lista paginada de inscripciones del curso
     */
    @GetMapping("/courses/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getEnrollmentsByCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<EnrollmentResponseDTO> body = enrollmentService.getEnrollmentsByCourse(courseId, page, size);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long enrollmentId) {
        EnrollmentResponseDTO body = enrollmentService.getEnrollmentById(enrollmentId);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{enrollmentId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollment(
            @PathVariable Long enrollmentId,
            @RequestBody EnrollmentRequestDTO request) {
        EnrollmentResponseDTO body = enrollmentService.updateEnrollment(enrollmentId, request);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getAllEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EnrollmentResponseDTO> body = enrollmentService.getAllEnrollments(page, size);
        return ResponseEntity.ok(body);
    }
}
