package com.edwinbaquiax.courseadministratorservice.services.enrollment;

import com.edwinbaquiax.courseadministratorservice.exceptions.CourseNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.EnrollmentExistException;
import com.edwinbaquiax.courseadministratorservice.exceptions.EnrollmentNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.enrollment.EnrollmentResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Enrollment;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.models.mappers.EnrollmentProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.ICourseRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IEnrollmentRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.edwinbaquiax.courseadministratorservice.models.mappers.EnrollmentProfile.*;

@Service
public class EnrollmentService implements IEnrollmentService{
    @Autowired
    private IEnrollmentRepository enrollmentRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ICourseRepository courseRepository;


    @Override
    @Transactional
    public EnrollmentResponseDTO enrollStudent(EnrollmentRequestDTO request) {
       enrollmentRepository
                .findByUser_IdAndCourse_IdAndActiveTrueAndCourse_ActiveTrue(request.getUserId(),request.getCourseId())
                .ifPresent(enrollment -> {throw  new EnrollmentExistException();});



        User student = userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(CourseNotFoundException::new);

        Enrollment newEnrollment = enrollmentRequestDtoTOEnrollmentEntity(request);

        newEnrollment.setCourse(course);
        newEnrollment.setUser(student);

        newEnrollment =  enrollmentRepository.save(newEnrollment);

        return enrollmentEntityToEnrollmentResponseDTO(newEnrollment);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
       Enrollment enrollmentToCancel = enrollmentRepository.findById(enrollmentId).orElseThrow(EnrollmentNotFoundException::new);
       enrollmentToCancel.setActive(false);
       enrollmentRepository.save(enrollmentToCancel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponseDTO> getEnrollmentsByStudent(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").ascending());

        Page<Enrollment> enrollments =  enrollmentRepository.findAllByUser_Id(userId,pageable);

        return enrollments.map(EnrollmentProfile::enrollmentEntityToEnrollmentResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponseDTO> getEnrollmentsByCourse(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").ascending());

        Page<Enrollment> enrollments =  enrollmentRepository.findAllByCourse_Id(courseId,pageable);

        return enrollments.map(EnrollmentProfile::enrollmentEntityToEnrollmentResponseDTO);
    }
    @Transactional(readOnly = true)
    @Override
    public EnrollmentResponseDTO getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(EnrollmentNotFoundException::new);
        return enrollmentEntityToEnrollmentResponseDTO(enrollment);
    }

    @Transactional
    @Override
    public EnrollmentResponseDTO updateEnrollment(Long enrollmentId, EnrollmentRequestDTO request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(EnrollmentNotFoundException::new);

        if(!enrollment.getUser().getId().equals(request.getUserId())) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(UserNotFoundException::new);
            enrollment.setUser(user);
        }

        if(!enrollment.getCourse().getId().equals(request.getCourseId())) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(CourseNotFoundException::new);
            enrollment.setCourse(course);
        }



        Enrollment updated = enrollmentRepository.save(enrollment);
        return enrollmentEntityToEnrollmentResponseDTO(updated);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EnrollmentResponseDTO> getAllEnrollments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Enrollment> enrollments = enrollmentRepository.findAll(pageable);
        return enrollments.map(EnrollmentProfile::enrollmentEntityToEnrollmentResponseDTO);
    }
}
