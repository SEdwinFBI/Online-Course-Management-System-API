package com.edwinbaquiax.courseadministratorservice.models.dtos.course;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {

    @NotBlank
    @Column(name = "title", nullable = false,unique = true)
    @Size(min = 3, max = 30)
    private String title;
    @NotBlank
    private String description;

}
