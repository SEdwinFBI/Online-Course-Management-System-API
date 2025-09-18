package com.edwinbaquiax.courseadministratorservice.repositories.sql;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String userName);

    boolean existsByUsername( String username);

    boolean existsByUsernameOrEmail(String username,String email);


}
