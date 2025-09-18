package com.edwinbaquiax.courseadministratorservice.models.dtos.course;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {

    private Long id;
    private String title;
    private String description;
    private boolean active;
    private String nameTeacher;
}
