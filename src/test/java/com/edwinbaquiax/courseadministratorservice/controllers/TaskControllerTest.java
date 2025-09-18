package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskResponseDTO;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import com.edwinbaquiax.courseadministratorservice.services.task.ITaskService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private ITaskService taskService;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private TaskController taskController;

    private TaskRequestDTO taskRequestDTO;
    private TaskResponseDTO taskResponseDTO;
    private final String taskId = "test-task-id";
    private final Long moduleId = 1L;
    private final String userId = "test-user-id";

    @BeforeEach
    void setUp() {

        taskRequestDTO = new TaskRequestDTO();
        taskRequestDTO.setTitle("Test Task");
        taskRequestDTO.setDescription("Test Description");
        taskRequestDTO.setModuleId(moduleId);

        taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(taskId);
        taskResponseDTO.setTitle("Test Task");
        taskResponseDTO.setDescription("Test Description");
        taskResponseDTO.setModuleId(moduleId);


        var authorities = List.of(new SimpleGrantedAuthority("ROLE_TEACHER"));
        var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createTask_WithValidRequest_ShouldReturnCreated() {
        // Arrange
        when(taskService.createTask(eq(moduleId), any(TaskRequestDTO.class)))
                .thenReturn(taskResponseDTO);

        // Act
        ResponseEntity<TaskResponseDTO> response =
                taskController.createTask(taskRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().getId());
        verify(taskService, times(1)).createTask(eq(moduleId), any(TaskRequestDTO.class));
    }

    @Test
    void updateTask_WithValidRequest_ShouldReturnOk() {
        // Arrange
        when(taskService.updateTask(eq(taskId), any(TaskRequestDTO.class)))
                .thenReturn(taskResponseDTO);

        // Act
        ResponseEntity<TaskResponseDTO> response =
                taskController.updateTask(taskId, taskRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().getId());
        verify(taskService, times(1)).updateTask(eq(taskId), any(TaskRequestDTO.class));
    }

    @Test
    void deleteTask_WithValidId_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(taskService).deleteTask(taskId);

        // Act
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    void findById_WithValidId_ShouldReturnTask() {
        // Arrange
        when(taskService.findById(taskId)).thenReturn(taskResponseDTO);

        // Act
        ResponseEntity<TaskResponseDTO> response = taskController.findById(taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().getId());
    }

    @Test
    void findTasksByModule_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<TaskResponseDTO> page = new PageImpl<>(
                Collections.singletonList(taskResponseDTO),
                PageRequest.of(0, 20), 1);

        when(taskService.findTasksByModule(eq(moduleId), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<TaskResponseDTO>> response =
                taskController.findTasksByModule(moduleId, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(taskId, response.getBody().getContent().get(0).getId());
    }

    @Test
    void findTasksByStudent_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<TaskResponseDTO> page = new PageImpl<>(
                Collections.singletonList(taskResponseDTO),
                PageRequest.of(0, 20), 1);

        when(currentUser.getUserId()).thenReturn(1L);
        when(taskService.findTasksByStudent(eq(1L), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<TaskResponseDTO>> response =
                taskController.findTasksByStudent(0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(taskId, response.getBody().getContent().get(0).getId());
    }


}