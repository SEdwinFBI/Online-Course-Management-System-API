package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.enrollment.IEnrollmentService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    @Mock
    private IEnrollmentService enrollmentService;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private EnrollmentRequestDTO enrollmentRequestDTO;
    private EnrollmentResponseDTO enrollmentResponseDTO;
    private final Long enrollmentId = 1L;
    private final Long courseId = 1L;
    private final String userId = "test-user-id";

    @BeforeEach
    void setUp() {

        enrollmentRequestDTO = new EnrollmentRequestDTO();
        enrollmentRequestDTO.setUserId(1L);
        enrollmentRequestDTO.setCourseId(1L);

        enrollmentResponseDTO = new EnrollmentResponseDTO();
        enrollmentResponseDTO.setId(enrollmentId);
        enrollmentResponseDTO.setUserId(1L);
        enrollmentResponseDTO.setCourseId(1L);
        enrollmentResponseDTO.setUsernameStudent("testuser");
        enrollmentResponseDTO.setTitle("Test Course");
        enrollmentResponseDTO.setActive(true);

        // Configurar contexto de seguridad para pruebas
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
        var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void enrollStudent_WithValidRequest_ShouldReturnCreated() {
        // Arrange
        when(enrollmentService.enrollStudent(any(EnrollmentRequestDTO.class)))
                .thenReturn(enrollmentResponseDTO);

        // Act
        ResponseEntity<EnrollmentResponseDTO> response =
                enrollmentController.enrollStudent(enrollmentRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(enrollmentId, response.getBody().getId());
        verify(enrollmentService, times(1)).enrollStudent(enrollmentRequestDTO);
    }

    @Test
    void enrollment_WithValidCourseId_ShouldReturnCreated() {
        // Arrange
        when(currentUser.getUserId()).thenReturn(1L);
        when(enrollmentService.enrollStudent(any(EnrollmentRequestDTO.class)))
                .thenReturn(enrollmentResponseDTO);

        // Act
        ResponseEntity<EnrollmentResponseDTO> response =
                enrollmentController.enrollment(courseId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(enrollmentId, response.getBody().getId());
        verify(enrollmentService, times(1)).enrollStudent(any(EnrollmentRequestDTO.class));
    }

    @Test
    void cancelEnrollment_WithValidId_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(enrollmentService).cancelEnrollment(enrollmentId);

        // Act
        ResponseEntity<Void> response = enrollmentController.cancelEnrollment(enrollmentId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(enrollmentService, times(1)).cancelEnrollment(enrollmentId);
    }

    @Test
    void getEnrollmentsByStudent_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<EnrollmentResponseDTO> page = new PageImpl<>(
                Collections.singletonList(enrollmentResponseDTO),
                PageRequest.of(0, 20), 1);

        when(currentUser.getUserId()).thenReturn(1L);
        when(enrollmentService.getEnrollmentsByStudent(eq(1L), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<EnrollmentResponseDTO>> response =
                enrollmentController.getEnrollmentsByStudent(0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(enrollmentId, response.getBody().getContent().get(0).getId());
    }

    @Test
    void getEnrollmentsByCourse_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<EnrollmentResponseDTO> page = new PageImpl<>(
                Collections.singletonList(enrollmentResponseDTO),
                PageRequest.of(0, 20), 1);

        when(enrollmentService.getEnrollmentsByCourse(eq(courseId), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<EnrollmentResponseDTO>> response =
                enrollmentController.getEnrollmentsByCourse(courseId, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(enrollmentId, response.getBody().getContent().get(0).getId());
    }
}