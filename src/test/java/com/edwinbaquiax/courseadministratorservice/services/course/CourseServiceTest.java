package com.edwinbaquiax.courseadministratorservice.services.course;

import com.edwinbaquiax.courseadministratorservice.exceptions.CourseNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleAlreadyExistException;
import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.models.mappers.CourseProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.ICourseRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IModuleRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IModuleRepository moduleRepository;

    @InjectMocks
    private CourseService courseService;

    private CourseRequestDTO courseRequestDTO;
    private Course course;
    private User teacher;
    private Module module;
    private final Long courseId = 1L;
    private final Long moduleId = 1L;
    private final Long teacherId = 1L;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        courseRequestDTO = new CourseRequestDTO();
        courseRequestDTO.setTitle("Test Course");
        courseRequestDTO.setDescription("Test Description");

        teacher = new User();
        teacher.setId(teacherId);
        teacher.setUsername("teacher");

        course = new Course();
        course.setId(courseId);
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        course.setTeacher(teacher);

        module = new Module();
        module.setId(moduleId);
        module.setModuleName("Test Module");
    }

    @Test
    void findAll_ShouldReturnListOfCourses() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Collections.singletonList(course));

        // Act
        List<CourseResponseDTO> result = courseService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseId, result.get(0).getId());
    }

    @Test
    void findAllCourseByPages_ShouldReturnPageOfCourses() {
        // Arrange
        Page<Course> coursePage = new PageImpl<>(Collections.singletonList(course));
        when(courseRepository.findAll(any(PageRequest.class))).thenReturn(coursePage);

        // Act
        Page<CourseResponseDTO> result = courseService.findAllCourseByPages(0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(courseId, result.getContent().get(0).getId());
    }

    @Test
    void findCoursesByStudent_WithValidStudent_ShouldReturnPage() {
        // Arrange
        Page<Course> coursePage = new PageImpl<>(Collections.singletonList(course));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.findCoursesByStudent(eq(teacherId), any(PageRequest.class)))
                .thenReturn(coursePage);

        // Act
        Page<CourseResponseDTO> result = courseService.findCoursesByStudent(teacherId, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(courseId, result.getContent().get(0).getId());
    }

    @Test
    void findCoursesByStudent_WithNonExistentStudent_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(teacherId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                courseService.findCoursesByStudent(teacherId, 0, 20));
    }

    @Test
    void findById_WithValidId_ShouldReturnCourse() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        CourseResponseDTO result = courseService.findById(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
    }

    @Test
    void findById_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () ->
                courseService.findById(courseId));
    }

    @Test
    void createCourse_WithValidRequest_ShouldReturnCreatedCourse() {
        // Arrange
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        CourseResponseDTO result = courseService.createCourse(teacherId, courseRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_WithNonExistentTeacher_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(teacherId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                courseService.createCourse(teacherId, courseRequestDTO));
    }

    @Test
    void updateCourse_WithValidRequest_ShouldReturnUpdatedCourse() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        CourseResponseDTO result = courseService.updateCourse(courseId, teacherId, courseRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourse_WithNonExistentCourse_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () ->
                courseService.updateCourse(courseId, teacherId, courseRequestDTO));
    }

    @Test
    void updateCourse_WithDifferentTeacher_ShouldThrowException() {
        // Arrange
        Long differentTeacherId = 2L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () ->
                courseService.updateCourse(courseId, differentTeacherId, courseRequestDTO));
    }

    @Test
    void addModuleToCourse_WithValidRequest_ShouldReturnUpdatedCourse() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        // Act
        CourseResponseDTO result = courseService.addModuleToCourse(courseId, moduleId, teacherId);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void addModuleToCourse_WithModuleAlreadyInCourse_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        // Simular que el módulo ya está en el curso
        course.getModules().add(module);

        // Act & Assert
        assertThrows(ModuleAlreadyExistException.class, () ->
                courseService.addModuleToCourse(courseId, moduleId, teacherId));
    }

    @Test
    void findCoursesByTeacher_WithValidTeacher_ShouldReturnPage() {
        // Arrange
        Page<Course> coursePage = new PageImpl<>(Collections.singletonList(course));
        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.findCoursesByTeacher_Id(eq(teacherId), any(PageRequest.class)))
                .thenReturn(coursePage);

        // Act
        Page<CourseResponseDTO> result = courseService.findCoursesByTeacher(teacherId, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(courseId, result.getContent().get(0).getId());
    }
}