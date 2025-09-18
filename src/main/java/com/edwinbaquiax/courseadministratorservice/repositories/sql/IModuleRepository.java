package com.edwinbaquiax.courseadministratorservice.repositories.sql;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Enrollment;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface IModuleRepository extends JpaRepository<Module, Long> {
    Page<Module> findAllByCourse_IdAndActive(Long courseId, boolean active, Pageable pageable);
    Optional<Module> findByIdAndCourse_Teacher_Id(Long id, Long courseTeacherId);


}
