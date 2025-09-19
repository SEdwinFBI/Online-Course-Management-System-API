package com.edwinbaquiax.courseadministratorservice.repositories.sql;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Long> {

    @Query("""
            select C from Course C
            inner join Enrollment E on E.course = C
            where E.user.id = :userId
              and E.active = true
              and C.active = true
            """)
    Page<Course> findCoursesByStudent(@Param("userId") Long userId, Pageable pageable);


    Page<Course> findCoursesByTeacher_Id(Long teacherId, Pageable pageable);
    
    Optional<Course> findByModulesContaining(Set<Module> modules);
    Optional<Course> findByIdAndTeacher_Id(Long id, Long teacherId);
}
