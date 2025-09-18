package com.edwinbaquiax.courseadministratorservice.services.task;

import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.TaskNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Task;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;
import com.edwinbaquiax.courseadministratorservice.models.mappers.TaskProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.mongo.ITaskRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IModuleRepository;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
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
class TaskServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private IModuleRepository moduleRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private TaskService taskService;

    private TaskRequestDTO taskRequestDTO;
    private Task task;
    private Module module;
    private final String taskId = "test-task-id";
    private final Long moduleId = 1L;
    private final Long teacherId = 1L;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        taskRequestDTO = new TaskRequestDTO();
        taskRequestDTO.setTitle("Test Task");
        taskRequestDTO.setDescription("Test Description");
        taskRequestDTO.setModuleId(moduleId);
        taskRequestDTO.setTypeTask("EXAM");

        module = new Module();
        module.setId(moduleId);
        module.setModuleName("Test Module");

        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setModuleId(moduleId);
        task.setActive(true);

        lenient().when(currentUser.getUserId()).thenReturn(teacherId);
    }

    @Test
    void createTask_WithValidRequest_ShouldReturnTask() {
        // Arrange
        when(moduleRepository.findByIdAndCourse_Teacher_Id(moduleId, teacherId))
                .thenReturn(Optional.of(module));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskResponseDTO result = taskService.createTask(moduleId, taskRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_WithNonExistentModule_ShouldThrowException() {
        // Arrange
        when(moduleRepository.findByIdAndCourse_Teacher_Id(moduleId, teacherId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ModuleNotFoundException.class, () ->
                taskService.createTask(moduleId, taskRequestDTO));
    }

    @Test
    void updateTask_WithValidRequest_ShouldReturnUpdatedTask() {
        // Arrange
        when(moduleRepository.findByIdAndCourse_Teacher_Id(moduleId, teacherId))
                .thenReturn(Optional.of(module));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskResponseDTO result = taskService.updateTask(taskId, taskRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WithNonExistentTask_ShouldThrowException() {
        // Arrange
        when(moduleRepository.findByIdAndCourse_Teacher_Id(moduleId, teacherId))
                .thenReturn(Optional.of(module));
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () ->
                taskService.updateTask(taskId, taskRequestDTO));
    }

    @Test
    void updateTask_WithNonExistentModule_ShouldThrowException() {
        // Arrange
        when(moduleRepository.findByIdAndCourse_Teacher_Id(moduleId, teacherId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ModuleNotFoundException.class, () ->
                taskService.updateTask(taskId, taskRequestDTO));
    }

    @Test
    void deleteTask_WithValidId_ShouldDeactivateTask() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.deleteTask(taskId);

        // Assert
        assertFalse(task.isActive());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void deleteTask_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () ->
                taskService.deleteTask(taskId));
    }

    @Test
    void findById_WithValidId_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        TaskResponseDTO result = taskService.findById(taskId);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
    }

    @Test
    void findById_WithNonExistentId_ShouldThrowException() {
        // Arrange
        lenient().when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () ->
                taskService.findById(taskId));
    }

    @Test
    void findTasksByModule_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.findAllByModuleIdAndActiveTrue(eq(moduleId), any(PageRequest.class)))
                .thenReturn(taskPage);

        // Act
        Page<TaskResponseDTO> result = taskService.findTasksByModule(moduleId, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(taskId, result.getContent().get(0).getId());
    }


}