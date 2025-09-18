package com.edwinbaquiax.courseadministratorservice.models.dtos.module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleRequestDTO {
    @NotBlank
    @Size(min = 3, max = 30)
    private String moduleName;

    @NotBlank
    private String description;
    private Long courseId;

}