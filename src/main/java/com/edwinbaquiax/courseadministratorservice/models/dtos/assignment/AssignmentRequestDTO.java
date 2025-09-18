package com.edwinbaquiax.courseadministratorservice.models.dtos.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentRequestDTO {
    private Long userId;
    private String taskId;
    private String status;
    private Double score;
    private LocalDateTime submittedAt;
}