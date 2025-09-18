package com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {
    private Long id;
    private Long userId;
    private String usernameStudent;
    private String title;
    private Long courseId;
    private boolean active;
    private LocalDateTime enrolledAt;

}
