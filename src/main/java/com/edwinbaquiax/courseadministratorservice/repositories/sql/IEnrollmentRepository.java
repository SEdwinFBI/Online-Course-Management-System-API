package com.edwinbaquiax.courseadministratorservice.repositories.sql;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEnrollmentRepository extends JpaRepository<Enrollment,Long> {

    Optional<Enrollment> findByUser_IdAndCourse_IdAndActiveTrueAndCourse_ActiveTrue(Long userId, Long courseId);


    Page<Enrollment> findAllByUser_Id(Long userId, Pageable pageable);

    Page<Enrollment> findAllByCourse_Id(Long courseId, Pageable pageable);
    @Query("""
                SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
                FROM Enrollment e
                JOIN e.course c
                JOIN c.modules m
                WHERE e.user.id = :userId AND m.id = :moduleId AND c.teacher.id = :teacherId
            """)
    boolean isAsignableToTaskStudent(@Param("userId") Long userId,
                                     @Param("moduleId") Long moduleId,
                                     @Param("teacherId") Long teacherId
    );
}
