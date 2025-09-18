package com.edwinbaquiax.courseadministratorservice.services.assignment;

import com.edwinbaquiax.courseadministratorservice.exceptions.*;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Assignment;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Task;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.models.enums.TaskStatus;
import com.edwinbaquiax.courseadministratorservice.models.mappers.AssignmentProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.mongo.IAssignmentRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.mongo.ITaskRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IEnrollmentRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private IAssignmentRepository assignmentRepository;

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private IEnrollmentRepository enrollmentRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private AssignmentService assignmentService;

    private AssignmentRequestDTO assignmentRequestDTO;
    private Assignment assignment;
    private Task task;
    private User user;
    private final String assignmentId = "test-assignment-id";
    private final String taskId = "test-task-id";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {

        assignmentRequestDTO = new AssignmentRequestDTO();
        assignmentRequestDTO.setUserId(1L);
        assignmentRequestDTO.setTaskId("test-task-id");

        assignment = new Assignment();
        assignment.setId("test-assignment-id");
        assignment.setUserId(1L);
        assignment.setTaskId("test-task-id");
        assignment.setStatus(TaskStatus.PENDING.name());

        task = new Task();
        task.setId("test-task-id");
        task.setValue(100.0);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        lenient().when(currentUser.getUserId()).thenReturn(2L);
    }

    @Test
    void createAssignment_WithValidRequest_ShouldReturnAssignment() {
        // Arrange
        when(assignmentRepository.existsAssignmentByUserIdAndTaskId(userId, taskId))
                .thenReturn(false);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(enrollmentRepository.isAsignableToTaskStudent(eq(userId), any(), eq(2L)))
                .thenReturn(true);
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

        // Act
        AssignmentResponseDTO result = assignmentService.createAssignment(assignmentRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(assignmentId, result.getId());
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    void createAssignment_WithExistingAssignment_ShouldThrowException() {
        // Arrange
        when(assignmentRepository.existsAssignmentByUserIdAndTaskId(userId, taskId))
                .thenReturn(true);

        // Act & Assert
        assertThrows(AssignmentAlreadyExistException.class, () ->
                assignmentService.createAssignment(assignmentRequestDTO));
    }

    @Test
    void updateAssignment_WithValidRequest_ShouldReturnUpdatedAssignment() {
        // Arrange
        assignmentRequestDTO.setScore(85.0);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(enrollmentRepository.isAsignableToTaskStudent(
                eq(userId), any(), eq(2L)))
                .thenReturn(true);
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

        // Act
        AssignmentResponseDTO result = assignmentService.updateAssignment(assignmentId, assignmentRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(assignmentId, result.getId());
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    void updateAssignment_WithInvalidScore_ShouldThrowException() {
        // Arrange
        assignmentRequestDTO.setScore(120.0);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        lenient().when(enrollmentRepository.isAsignableToTaskStudent(eq(userId), any(), eq(2L)))
                .thenReturn(true);

        lenient().when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));


        // Act & Assert
        assertThrows(AssignmentInvalidScoreException.class, () ->
                assignmentService.updateAssignment(assignmentId, assignmentRequestDTO));
    }

    @Test
    void deleteAssignment_WithValidId_ShouldDeleteAssignment() {
        // Arrange
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        doNothing().when(assignmentRepository).delete(assignment);

        // Act
        assignmentService.deleteAssignment(assignmentId);

        // Assert
        verify(assignmentRepository, times(1)).delete(assignment);
    }

    @Test
    void findById_WithValidId_ShouldReturnAssignment() {
        // Arrange
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        // Act
        AssignmentResponseDTO result = assignmentService.findById(assignmentId);

        // Assert
        assertNotNull(result);
        assertEquals(assignmentId, result.getId());
    }

    @Test
    void findAssignmentsByStudent_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<Assignment> assignmentPage = new PageImpl<>(Collections.singletonList(assignment));
        when(assignmentRepository.findAllByUserId(eq(userId), any())).thenReturn(assignmentPage);

        // Act
        Page<AssignmentResponseDTO> result = assignmentService.findAssignmentsByStudent(userId, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(assignmentId, result.getContent().getFirst().getId());
    }

    @Test
    void submitAssignment_WithValidParameters_ShouldUpdateAssignment() {
        // Arrange
        Double score = 95.5;
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

        // Act
        AssignmentResponseDTO result = assignmentService.submitAssignment(assignmentId, score);

        // Assert
        assertNotNull(result);
        assertEquals(assignmentId, result.getId());
        assertEquals(TaskStatus.COMPLETED.name(), assignment.getStatus());
        assertEquals(score, assignment.getScore());
        assertNotNull(assignment.getSubmittedAt());
    }
}