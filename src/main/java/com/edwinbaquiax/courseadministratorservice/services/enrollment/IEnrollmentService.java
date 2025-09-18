package com.edwinbaquiax.courseadministratorservice.services.enrollment;

import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IEnrollmentService {
     EnrollmentResponseDTO enrollStudent(EnrollmentRequestDTO request);
    void cancelEnrollment(Long enrollmentId);
    Page<EnrollmentResponseDTO> getEnrollmentsByStudent(Long userId, int page, int size);
    Page<EnrollmentResponseDTO> getEnrollmentsByCourse(Long courseId, int page, int size);
}
