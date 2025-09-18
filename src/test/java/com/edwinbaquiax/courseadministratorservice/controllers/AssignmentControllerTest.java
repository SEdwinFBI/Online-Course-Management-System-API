package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.assignment.IAssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class AssignmentControllerTest {

    @Mock
    private IAssignmentService assignmentService;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private AssignmentController assignmentController;

    private AssignmentRequestDTO assignmentRequestDTO;
    private AssignmentResponseDTO assignmentResponseDTO;
    private final String assignmentId = "test-assignment-id";
    private final String taskId = "test-task-id";
    private final String userId = "test-user-id";

    @BeforeEach
    void setUp() {

        assignmentRequestDTO = new AssignmentRequestDTO();
        assignmentRequestDTO.setUserId(1L);
        assignmentRequestDTO.setTaskId("task-123");

        assignmentResponseDTO = new AssignmentResponseDTO();
        assignmentResponseDTO.setId(assignmentId);
        assignmentResponseDTO.setUserId(1L);
        assignmentResponseDTO.setTaskId("task-123");


        var authorities = List.of(new SimpleGrantedAuthority("ROLE_TEACHER"));
        var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createAssignment_WithValidRequest_ShouldReturnCreated() {
        // Arrange
        when(assignmentService.createAssignment(any(AssignmentRequestDTO.class)))
                .thenReturn(assignmentResponseDTO);

        // Act
        ResponseEntity<AssignmentResponseDTO> response =
                assignmentController.createAssignment(assignmentRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(assignmentId, response.getBody().getId());
        verify(assignmentService, times(1)).createAssignment(assignmentRequestDTO);
    }

    @Test
    void updateAssignment_WithValidRequest_ShouldReturnOk() {
        // Arrange
        when(assignmentService.updateAssignment(eq(assignmentId), any(AssignmentRequestDTO.class)))
                .thenReturn(assignmentResponseDTO);

        // Act
        ResponseEntity<AssignmentResponseDTO> response =
                assignmentController.updateAssignment(assignmentId, assignmentRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(assignmentId, response.getBody().getId());
        verify(assignmentService, times(1))
                .updateAssignment(assignmentId, assignmentRequestDTO);
    }

    @Test
    void deleteAssignment_WithValidId_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(assignmentService).deleteAssignment(assignmentId);

        // Act
        ResponseEntity<Void> response = assignmentController.deleteAssignment(assignmentId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(assignmentService, times(1)).deleteAssignment(assignmentId);
    }

    @Test
    void findById_WithValidId_ShouldReturnAssignment() {
        // Arrange
        when(assignmentService.findById(assignmentId))
                .thenReturn(assignmentResponseDTO);

        // Act
        ResponseEntity<AssignmentResponseDTO> response =
                assignmentController.findById(assignmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(assignmentId, response.getBody().getId());
        verify(assignmentService, times(1)).findById(assignmentId);
    }



    @Test
    void findAssignmentsByTask_WithValidParameters_ShouldReturnPage() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_TEACHER"));
        // Arrange
        Page<AssignmentResponseDTO> page = new PageImpl<>(
                Collections.singletonList(assignmentResponseDTO),
                PageRequest.of(0, 20), 1);

        when(assignmentService.findAssignmentsByTask(eq(taskId), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<AssignmentResponseDTO>> response =
                assignmentController.findAssignmentsByTask(taskId, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(assignmentId, response.getBody().getContent().getFirst().getId());
    }

    @Test
    void submitAssignment_WithValidParameters_ShouldReturnUpdatedAssignment() {
        // Arrange
        Double score = 95.5;
        assignmentResponseDTO.setScore(score);

        when(assignmentService.submitAssignment(assignmentId, score))
                .thenReturn(assignmentResponseDTO);

        // Act
        ResponseEntity<AssignmentResponseDTO> response =
                assignmentController.submitAssignment(assignmentId, score);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(assignmentId, response.getBody().getId());
        assertEquals(score, response.getBody().getScore());
        verify(assignmentService, times(1)).submitAssignment(assignmentId, score);
    }

}