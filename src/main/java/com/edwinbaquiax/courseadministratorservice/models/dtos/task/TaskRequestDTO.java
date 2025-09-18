package com.edwinbaquiax.courseadministratorservice.models.dtos.task;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {
    private String title;
    private Double value;
    private String description;
    private String instructions;
    private String typeTask;
    private Long moduleId;
    private boolean active;
}
