package com.edwinbaquiax.courseadministratorservice.services.assignment;

import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentResponseDTO;
import org.springframework.data.domain.Page;

public interface IAssignmentService {

    AssignmentResponseDTO createAssignment(AssignmentRequestDTO request);


    AssignmentResponseDTO updateAssignment(String assignmentId, AssignmentRequestDTO request);


    void deleteAssignment(String assignmentId);


    AssignmentResponseDTO findById(String assignmentId);


    Page<AssignmentResponseDTO> findAssignmentsByStudent(Long userId, int page, int size);


    Page<AssignmentResponseDTO> findAssignmentsByTask(String taskId, int page, int size);


    AssignmentResponseDTO submitAssignment(String assignmentId, Double score);
}
