package com.edwinbaquiax.courseadministratorservice.models.dtos.user;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String lastname;
    private boolean enabled;
    private Set<Role> roles;
}
