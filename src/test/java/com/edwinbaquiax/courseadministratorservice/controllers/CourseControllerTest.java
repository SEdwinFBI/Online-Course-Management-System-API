package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserPrincipal;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.course.ICourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@EnableMethodSecurity(prePostEnabled = true)
@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @Mock
    private ICourseService courseService;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CourseController courseController;

    private CourseRequestDTO courseRequestDTO;
    private CourseResponseDTO courseResponseDTO;
    private final Long courseId = 1L;
    private final Long moduleId = 1L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {

        courseRequestDTO = new CourseRequestDTO();
        courseRequestDTO.setTitle("Test Course");
        courseRequestDTO.setDescription("Test Description");

        courseResponseDTO = new CourseResponseDTO();
        courseResponseDTO.setId(courseId);
        courseResponseDTO.setTitle("Test Course");
        courseResponseDTO.setDescription("Test Description");


        var authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
        var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void findAll_ShouldReturnListOfCourses() {
        // Arrange
        when(courseService.findAll()).thenReturn(Collections.singletonList(courseResponseDTO));

        // Act
        ResponseEntity<List<CourseResponseDTO>> response = courseController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(courseId, response.getBody().get(0).getId());
    }

    @Test
    void findAllCourses_ShouldReturnPageOfCourses() {
        // Arrange
        Page<CourseResponseDTO> page = new PageImpl<>(
                Collections.singletonList(courseResponseDTO),
                PageRequest.of(0, 20), 1);

        when(courseService.findAllCourseByPages(0, 20)).thenReturn(page);

        // Act
        ResponseEntity<Page<CourseResponseDTO>> response = courseController.findAllCourses(0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(courseId, response.getBody().getContent().get(0).getId());
    }

    @Test
    void addModuleToCourse_WithValidParameters_ShouldReturnUpdatedCourse() {
        // Arrange
        when(courseService.addModuleToCourse(eq(courseId), eq(moduleId), any()))
                .thenReturn(courseResponseDTO);

        // Act
        ResponseEntity<CourseResponseDTO> response =
                courseController.addModuleToCourse(courseId, moduleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(courseId, response.getBody().getId());
        verify(courseService, times(1))
                .addModuleToCourse(eq(courseId), eq(moduleId), any());
    }

    @Test
    void findById_WithValidId_ShouldReturnCourse() {
        // Arrange
        when(courseService.findById(courseId)).thenReturn(courseResponseDTO);

        // Act
        ResponseEntity<CourseResponseDTO> response = courseController.findById(courseId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(courseId, response.getBody().getId());
    }

    @Test
    void findMyLearning_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<CourseResponseDTO> page = new PageImpl<>(
                Collections.singletonList(courseResponseDTO),
                PageRequest.of(0, 20), 1);

        when(currentUser.getUserId()).thenReturn(1L);
        when(courseService.findCoursesByStudent(eq(1L), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<CourseResponseDTO>> response =
                courseController.findMyLearning(0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(courseId, response.getBody().getContent().get(0).getId());
    }

    @Test
    void findMyCourses_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<CourseResponseDTO> page = new PageImpl<>(
                Collections.singletonList(courseResponseDTO),
                PageRequest.of(0, 20), 1);

        when(currentUser.getUserId()).thenReturn(1L);
        when(courseService.findCoursesByTeacher(eq(1L), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<CourseResponseDTO>> response =
                courseController.findMyCourses(0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(courseId, response.getBody().getContent().get(0).getId());
    }

    @Test
    void createCourse_WithValidRequest_ShouldReturnCreatedCourse() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(currentUser.getUserId()).thenReturn(1L);
        when(courseService.createCourse(eq(1L), any(CourseRequestDTO.class)))
                .thenReturn(courseResponseDTO);

        // Act
        ResponseEntity<?> response = courseController.createCourse(courseRequestDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof CourseResponseDTO);
        assertEquals(courseId, ((CourseResponseDTO) response.getBody()).getId());
    }

    @Test
    void createCourse_WithValidationErrors_ShouldReturnValidationErrors() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> response = courseController.createCourse(courseRequestDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateCourse_WithValidRequest_ShouldReturnUpdatedCourse() {
        // Arrange
        when(currentUser.getUserId()).thenReturn(1L);
        when(courseService.updateCourse(eq(courseId), eq(1L), any(CourseRequestDTO.class)))
                .thenReturn(courseResponseDTO);

        // Act
        ResponseEntity<CourseResponseDTO> response =
                courseController.updateCourse(courseId, courseRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(courseId, response.getBody().getId());
    }


}