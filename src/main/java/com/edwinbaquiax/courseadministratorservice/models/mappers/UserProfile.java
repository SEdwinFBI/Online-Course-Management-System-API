package com.edwinbaquiax.courseadministratorservice.models.mappers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.user.RegisterUserDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;

public class UserProfile {

    public static User registerUserDtoToUserEntity(RegisterUserDTO dto){
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .name(dto.getName())
                .lastname(dto.getLastname())
//                .createdAt(LocalDateTime.now())
                .build();
    }
    public static UserResponseDTO userEntityToUserResponseDTO(User entity){
        return UserResponseDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .name(entity.getName())
                .lastname(entity.getLastname())
                .enabled(entity.isEnabled())
                .roles(entity.getRoles())
                .build();
    }
}
