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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private IEnrollmentRepository enrollmentRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private EnrollmentRequestDTO enrollmentRequestDTO;
    private Enrollment enrollment;
    private User user;
    private Course course;
    private final Long enrollmentId = 1L;
    private final Long userId = 1L;
    private final Long courseId = 1L;

    @BeforeEach
    void setUp() {

        enrollmentRequestDTO = new EnrollmentRequestDTO();
        enrollmentRequestDTO.setUserId(userId);
        enrollmentRequestDTO.setCourseId(courseId);

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        course = new Course();
        course.setId(courseId);
        course.setTitle("Test Course");

        enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setActive(true);
    }

    @Test
    void enrollStudent_WithValidRequest_ShouldReturnEnrollment() {
        // Arrange
        when(enrollmentRepository.findByUser_IdAndCourse_IdAndActiveTrueAndCourse_ActiveTrue(userId, courseId))
                .thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        // Act
        EnrollmentResponseDTO result = enrollmentService.enrollStudent(enrollmentRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(enrollmentId, result.getId());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_WithExistingEnrollment_ShouldThrowException() {
        // Arrange
        when(enrollmentRepository.findByUser_IdAndCourse_IdAndActiveTrueAndCourse_ActiveTrue(userId, courseId))
                .thenReturn(Optional.of(enrollment));

        // Act & Assert
        assertThrows(EnrollmentExistException.class, () ->
                enrollmentService.enrollStudent(enrollmentRequestDTO));
    }

    @Test
    void enrollStudent_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(enrollmentRepository.findByUser_IdAndCourse_IdAndActiveTrueAndCourse_ActiveTrue(userId, courseId))
                .thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                enrollmentService.enrollStudent(enrollmentRequestDTO));
    }

    @Test
    void enrollStudent_WithNonExistentCourse_ShouldThrowException() {
        // Arrange
        when(enrollmentRepository.findByUser_IdAndCourse_IdAndActiveTrueAndCourse_ActiveTrue(userId, courseId))
                .thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () ->
                enrollmentService.enrollStudent(enrollmentRequestDTO));
    }

    @Test
    void cancelEnrollment_WithValidId_ShouldDeactivateEnrollment() {
        // Arrange
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        // Act
        enrollmentService.cancelEnrollment(enrollmentId);

        // Assert
        assertFalse(enrollment.isActive());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    void cancelEnrollment_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EnrollmentNotFoundException.class, () ->
                enrollmentService.cancelEnrollment(enrollmentId));
    }

    @Test
    void getEnrollmentsByStudent_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<Enrollment> enrollmentPage = new PageImpl<>(Collections.singletonList(enrollment));
        when(enrollmentRepository.findAllByUser_Id(eq(userId), any(PageRequest.class)))
                .thenReturn(enrollmentPage);

        // Act
        Page<EnrollmentResponseDTO> result = enrollmentService.getEnrollmentsByStudent(userId, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(enrollmentId, result.getContent().get(0).getId());
    }

    @Test
    void getEnrollmentsByCourse_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<Enrollment> enrollmentPage = new PageImpl<>(Collections.singletonList(enrollment));
        when(enrollmentRepository.findAllByCourse_Id(eq(courseId), any(PageRequest.class)))
                .thenReturn(enrollmentPage);

        // Act
        Page<EnrollmentResponseDTO> result = enrollmentService.getEnrollmentsByCourse(courseId, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(enrollmentId, result.getContent().get(0).getId());
    }
}