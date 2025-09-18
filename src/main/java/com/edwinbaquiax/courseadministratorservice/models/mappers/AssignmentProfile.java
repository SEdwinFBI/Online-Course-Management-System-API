package com.edwinbaquiax.courseadministratorservice.models.mappers;

import com.edwinbaquiax.courseadministratorservice.exceptions.TaskStatusNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.assignment.AssignmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Assignment;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Task;
import com.edwinbaquiax.courseadministratorservice.models.enums.TaskStatus;

public class AssignmentProfile {
    public static Assignment assignmentRequestDtoToEntity(AssignmentRequestDTO dto) {
        try {
            return Assignment.builder()
                    .userId(dto.getUserId())
                    .taskId(dto.getTaskId())
                    .status(dto.getStatus() != null ? TaskStatus.valueOf(dto.getStatus()).toString() : TaskStatus.PENDING.name())
                    .score(dto.getScore())
                    .submittedAt(null)
                    .build();
        }catch (IllegalArgumentException exception){
            throw new TaskStatusNotFoundException(String.format("El estado %s no es soportado",dto.getStatus()));
        }

    }


    public static AssignmentResponseDTO entityToAssignmentResponseDTO(Assignment assignment) {
        return AssignmentResponseDTO.builder()
                .id(assignment.getId())
                .userId(assignment.getUserId())
                .taskId(assignment.getTaskId())
                .status(assignment.getStatus())
                .score(assignment.getScore())
                .submittedAt(assignment.getSubmittedAt())
                .build();
    }


    public static void updateEntityFromDto(Assignment assignment, AssignmentRequestDTO dto) {
      try {
          if (dto.getStatus() != null) assignment.setStatus(TaskStatus.valueOf(dto.getStatus()).toString());
          if (dto.getScore() != null) assignment.setScore(dto.getScore());
          if (dto.getSubmittedAt() != null) assignment.setSubmittedAt(dto.getSubmittedAt());
      }catch (IllegalArgumentException exception){
          throw new TaskStatusNotFoundException(String.format("El estado %s no es soportado",dto.getStatus()));
      }
    }
}
