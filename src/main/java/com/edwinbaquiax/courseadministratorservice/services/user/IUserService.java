package com.edwinbaquiax.courseadministratorservice.services.user;

import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;

import java.util.List;
import java.util.Set;

public interface IUserService {

    public UserResponseDTO save(User user);

    boolean existsByUsername(String username);
     List<UserResponseDTO> findAll();

    UserResponseDTO updateRoleUser(Long userId, Set<String> roleNames);
     UserResponseDTO findById(Long id);
    UserResponseDTO findByUsername(String username);
    void updateLastLogin(String username);
}
