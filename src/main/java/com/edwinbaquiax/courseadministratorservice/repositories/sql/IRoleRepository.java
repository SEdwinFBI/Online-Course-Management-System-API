package com.edwinbaquiax.courseadministratorservice.repositories.sql;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(@NotBlank @Size(min = 3, max = 20) String name);
}
