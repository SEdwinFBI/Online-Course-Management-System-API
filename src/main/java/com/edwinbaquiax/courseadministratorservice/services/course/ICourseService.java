package com.edwinbaquiax.courseadministratorservice.services.course;

import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICourseService {
    List<CourseResponseDTO> findAll();
    Page<CourseResponseDTO> findAllCourseByPages(int page, int size);
    Page<CourseResponseDTO> findCoursesByStudent(Long userId,int page, int size);
    CourseResponseDTO findById(Long courseId);
    CourseResponseDTO createCourse(Long userId, CourseRequestDTO request);
    CourseResponseDTO updateCourse(Long courseId, Long userId, CourseRequestDTO request);



    CourseResponseDTO addModuleToCourse(Long courseId, Long moduleId, Long userId);

    Page<CourseResponseDTO> findCoursesByTeacher(Long userId, int page, int size);
}
