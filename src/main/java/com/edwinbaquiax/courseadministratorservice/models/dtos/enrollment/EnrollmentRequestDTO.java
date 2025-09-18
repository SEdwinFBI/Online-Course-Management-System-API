package com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestDTO {
    private Long userId;
    private Long courseId;
}
