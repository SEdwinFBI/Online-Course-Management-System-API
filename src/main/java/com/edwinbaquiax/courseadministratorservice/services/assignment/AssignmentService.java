package com.edwinbaquiax.courseadministratorservice.services.assignment;

import com.edwinbaquiax.courseadministratorservice.exceptions.*;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Assignment;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Task;

import static com.edwinbaquiax.courseadministratorservice.models.mappers.AssignmentProfile.*;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.models.enums.TaskStatus;
import com.edwinbaquiax.courseadministratorservice.models.mappers.AssignmentProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.mongo.IAssignmentRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.mongo.ITaskRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.ICourseRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IEnrollmentRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IModuleRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AssignmentService implements IAssignmentService {
@Autowired
    private IAssignmentRepository assignmentRepository;
    @Autowired
    private ITaskRepository taskRepository;
    @Autowired
    private IEnrollmentRepository enrollmentRepository;
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private CurrentUser currentUser;


    @Override
    public AssignmentResponseDTO createAssignment(AssignmentRequestDTO request) {

        if (assignmentRepository.existsAssignmentByUserIdAndTaskId(request.getUserId(), request.getTaskId())) {
            throw new AssignmentAlreadyExistException();
        }

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(TaskNotFoundException::new);
        userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);

       if(!enrollmentRepository.isAsignableToTaskStudent(request.getUserId(),task.getModuleId(),currentUser.getUserId())){
           throw new TaskNotFoundException();
       }


        Assignment assignment = assignmentRequestDtoToEntity(request);
        Assignment saved = assignmentRepository.save(assignment);

        return entityToAssignmentResponseDTO(saved);
    }

    @Override
    public AssignmentResponseDTO updateAssignment(String assignmentId, AssignmentRequestDTO request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(TaskNotFoundException::new);
        if(!enrollmentRepository.isAsignableToTaskStudent(request.getUserId(),task.getModuleId(),currentUser.getUserId())){
            throw new TaskNotFoundException();
        }

        if(request.getScore() > task.getValue()){
            throw  new AssignmentInvalidScoreException("La puntuacion sobrepasa el limite a obtener");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(AssignmentNotFoundException::new);

        updateEntityFromDto(assignment, request);
        Assignment updated = assignmentRepository.save(assignment);

        return entityToAssignmentResponseDTO(updated);
    }

    @Override
    public void deleteAssignment(String assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(AssignmentNotFoundException::new);
        assignmentRepository.delete(assignment);
    }

    @Override
    public AssignmentResponseDTO findById(String assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(AssignmentNotFoundException::new);
        return entityToAssignmentResponseDTO(assignment);
    }

    @Override
    public Page<AssignmentResponseDTO> findAssignmentsByStudent(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        Page<Assignment> assignments = assignmentRepository.findAllByUserId(userId, pageable);

        return assignments.map(AssignmentProfile::entityToAssignmentResponseDTO);
    }

    @Override
    public Page<AssignmentResponseDTO> findAssignmentsByTask(String taskId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        Page<Assignment> assignments = assignmentRepository.findAllByTaskId(taskId, pageable);

        return assignments.map(AssignmentProfile::entityToAssignmentResponseDTO);
    }

    @Override
    public AssignmentResponseDTO submitAssignment(String assignmentId, Double score) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(AssignmentNotFoundException::new);

        Task task = taskRepository.findById(assignment.getTaskId()).orElseThrow(TaskNotFoundException::new);
        if(score > task.getValue()){
            throw  new AssignmentInvalidScoreException("La puntuacion sobrepasa el limite a obtener");
        }

        assignment.setStatus(TaskStatus.COMPLETED.name());
        assignment.setScore(score);
        assignment.setSubmittedAt(LocalDateTime.now());

        Assignment saved = assignmentRepository.save(assignment);
        return entityToAssignmentResponseDTO(saved);
    }
}