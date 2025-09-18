package com.edwinbaquiax.courseadministratorservice.models.mappers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.course.CourseResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;

public class CourseProfile {
    public static CourseResponseDTO courseEntityToCourseResponseDTO(Course entity){
        return CourseResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .active(entity.isActive())
                .nameTeacher(entity.getTeacher().getName()+" "+entity.getTeacher().getLastname())
                .build();
    }

    public static Course courseRequestDtoToCourseEntity(CourseRequestDTO dto){
        return Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
    }
}
