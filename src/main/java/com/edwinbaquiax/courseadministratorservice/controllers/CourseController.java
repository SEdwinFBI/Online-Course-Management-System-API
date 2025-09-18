package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseResponseDTO;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.course.ICourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.edwinbaquiax.courseadministratorservice.interceptors.ValidationBindingResult.validation;

/**
 * Controlador REST para la gestión de cursos.
 *
 * <p>Expone endpoints bajo la ruta <b>/api/v1/courses</b> para:</p>
 * <ul>
 *     <li>Listar cursos (paginados o completos)</li>
 *     <li>Consultar cursos por ID</li>
 *     <li>Asignar módulos a cursos</li>
 *     <li>Obtener cursos en los que un estudiante está inscrito</li>
 *     <li>Obtener cursos creados por un docente</li>
 *     <li>Crear y actualizar cursos</li>
 * </ul>
 *
 * <p>Algunos endpoints requieren permisos con rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
 */
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    @Autowired
    private ICourseService courseService;
    @Autowired
    private CurrentUser currentUser;


    /**
     * Obtiene la lista completa de cursos sin paginación.
     *
     * @return lista de todos los cursos con código HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> findAll(){
        List<CourseResponseDTO> body=courseService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
    /**
     * Obtiene la lista paginada de cursos.
     *
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return cursos en formato paginado con código HTTP 200 (OK)
     */
    //?page=0&size=10
    @GetMapping("list")
    public ResponseEntity<Page<CourseResponseDTO>> findAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){

        Page<CourseResponseDTO> body=courseService.findAllCourseByPages(page,size);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Asigna un módulo a un curso existente.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param courseId identificador del curso
     * @param moduleId identificador del módulo
     * @return curso actualizado con el módulo agregado
     */
    @PostMapping("/{courseId}/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<CourseResponseDTO> addModuleToCourse(
            @PathVariable Long courseId,
            @PathVariable Long moduleId) {

        CourseResponseDTO body = courseService.addModuleToCourse(courseId, moduleId, currentUser.getUserId());
        return ResponseEntity.ok(body);
    }

    /**
     * Busca un curso por su identificador.
     *
     * @param courseId identificador del curso
     * @return curso encontrado con código HTTP 200 (OK)
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> findById(@PathVariable Long courseId){
        CourseResponseDTO body=courseService.findById(courseId);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Obtiene los cursos en los que el estudiante autenticado está inscrito.
     *
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return cursos del estudiante en formato paginado
     */
    @GetMapping("/my_learn")
    public ResponseEntity<Page<CourseResponseDTO>> findMyLearning(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){

        Page<CourseResponseDTO> body=courseService.findCoursesByStudent(currentUser.getUserId(),page,size);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }


    /**
     * Obtiene los cursos creados por el docente autenticado.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 20)
     * @return cursos del docente en formato paginado
     */
    @GetMapping("/my_courses")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<Page<CourseResponseDTO>> findMyCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){

        Page<CourseResponseDTO> body=courseService.findCoursesByTeacher(currentUser.getUserId(),page,size);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Crea un nuevo curso asociado al docente autenticado.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param request datos del curso a crear
     * @param bindingResult validaciones de los campos
     * @return curso creado con código HTTP 200 (OK),
     *         o errores de validación si los hubiera
     */
    @PostMapping("create")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<?> createCourse(
            @Valid
            @RequestBody CourseRequestDTO request,
            BindingResult bindingResult
    ){
        if(bindingResult.hasFieldErrors()){
            return validation(bindingResult);
        }

        CourseResponseDTO body=courseService.createCourse(currentUser.getUserId(),request);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }


    /**
     * Actualiza los datos de un curso.
     *
     * <p>Requiere rol <b>TEACHER</b> o <b>ADMIN</b>.</p>
     *
     * @param courseId identificador del curso
     * @param request datos actualizados del curso
     * @return curso actualizado con código HTTP 200 (OK)
     */
    @PutMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDTO request) {

        CourseResponseDTO body = courseService.updateCourse(courseId, currentUser.getUserId(), request);
        return ResponseEntity.ok(body);
    }



}
