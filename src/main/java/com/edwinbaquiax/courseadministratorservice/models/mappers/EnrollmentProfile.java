package com.edwinbaquiax.courseadministratorservice.models.mappers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Enrollment;

public class EnrollmentProfile {
    public static EnrollmentResponseDTO enrollmentEntityToEnrollmentResponseDTO(Enrollment entity){
        return  EnrollmentResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getCourse().getTitle())
                .active(entity.isActive())
                .courseId(entity.getCourse().getId())
                .userId(entity.getUser().getId())
                .enrolledAt(entity.getCreatedAt())
                .usernameStudent(entity.getUser().getUsername())
                .build();
    }
    public static Enrollment enrollmentRequestDtoTOEnrollmentEntity(EnrollmentRequestDTO dto){
        return  Enrollment.builder()
                .build();
    }
}
