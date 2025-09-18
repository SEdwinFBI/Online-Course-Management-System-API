package com.edwinbaquiax.courseadministratorservice.models.dtos.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponseDTO {
    private Long id;
    private Long courseId;
    private String moduleName;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

}