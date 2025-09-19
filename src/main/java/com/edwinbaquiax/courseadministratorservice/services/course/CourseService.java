package com.edwinbaquiax.courseadministratorservice.services.course;

import com.edwinbaquiax.courseadministratorservice.exceptions.CourseNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleAlreadyExistException;
import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.models.mappers.CourseProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.ICourseRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IModuleRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.edwinbaquiax.courseadministratorservice.models.mappers.CourseProfile.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourseService implements ICourseService {

    @Autowired
    private ICourseRepository courseRepository;

    @Autowired
    private IUserRepository userRepository;


    @Autowired
    private IModuleRepository moduleRepository;



    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> findAll() {
        return courseRepository
                .findAll()
                .stream()
                .map(CourseProfile::courseEntityToCourseResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> findAllCourseByPages(int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("title").ascending());

        return courseRepository
                .findAll(pageable)
                .map(CourseProfile::courseEntityToCourseResponseDTO);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> findCoursesByStudent(Long userId,int page, int size) {

        User student = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Pageable pageable = PageRequest.of(page,size, Sort.by("title").ascending());

        Page<Course> coursesAvailable = courseRepository.findCoursesByStudent(student.getId(),pageable);


        return coursesAvailable.map(CourseProfile::courseEntityToCourseResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO findById(Long courseId) {
        Course existCourse = courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);

        return  courseEntityToCourseResponseDTO(existCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO createCourse(Long userId, CourseRequestDTO request) {
        Course courseRequest = courseRequestDtoToCourseEntity(request);
        User teacher = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        courseRequest.setTeacher(teacher);

        Course newCourse = courseRepository.save(courseRequest);

        return courseEntityToCourseResponseDTO(newCourse);
    }

    @Override
    public CourseResponseDTO deleteCourse(Long userId, Long courseId) {
        Course course = courseRepository.findByIdAndTeacher_Id(courseId,userId).orElseThrow(CourseNotFoundException::new);
        courseRepository.delete(course);
        return courseEntityToCourseResponseDTO(course);
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourse(Long courseId, Long userId, CourseRequestDTO request) {
        Course existCourse = courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);
        if(!Objects.equals(existCourse.getTeacher().getId(), userId)){
            throw new CourseNotFoundException("Solo el creador del curso puede modificarlo");
        }
        existCourse.setTitle(request.getTitle());
        existCourse.setDescription(request.getDescription());
        Course updated = courseRepository.save(existCourse);


        return courseEntityToCourseResponseDTO(updated);
    }

    @Transactional
    @Override
    public CourseResponseDTO addModuleToCourse(Long courseId, Long moduleId, Long userId) {
        Course existCourse = courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);
        Module existModule = moduleRepository.findById(moduleId).orElseThrow(ModuleNotFoundException::new);
        if(!Objects.equals(existCourse.getTeacher().getId(), userId)){
            throw new CourseNotFoundException("Solo el creador del curso puede modificarlo");
        }
        if(existCourse.getModules().contains(existModule)){
            throw new ModuleAlreadyExistException(String.format("El modulo %s ya pertenece al curso",existModule.getModuleName()));
        }
        if(existModule.getCourse() != null){
            throw new ModuleAlreadyExistException(String.format("El modulo %s ya pertenece a un curso",existModule.getModuleName()));
        }

        existModule.setCourse(existCourse);


        moduleRepository.save(existModule);



        return CourseProfile.courseEntityToCourseResponseDTO(existCourse );
    }

    @Override
    public Page<CourseResponseDTO> findCoursesByTeacher(Long userId, int page, int size) {
        User teacher = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Pageable pageable = PageRequest.of(page,size, Sort.by("title").ascending());

        Page<Course> coursesAvailable = courseRepository.findCoursesByTeacher_Id(teacher.getId(),pageable);


        return coursesAvailable.map(CourseProfile::courseEntityToCourseResponseDTO);
    }
}
