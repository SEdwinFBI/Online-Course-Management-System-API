package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserResponseDTO;
import com.edwinbaquiax.courseadministratorservice.services.user.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponseDTO userResponseDTO;
    private final Long userId = 1L;
    private final String userRole = "test-user-id";

    @BeforeEach
    void setUp() {

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userId);
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setEmail("test@example.com");
        userResponseDTO.setName("Test");
        userResponseDTO.setLastname("User");


        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        var authentication = new UsernamePasswordAuthenticationToken(userRole, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void list_WithAdminRole_ShouldReturnListOfUsers() {
        // Arrange
        when(userService.findAll()).thenReturn(Collections.singletonList(userResponseDTO));

        // Act
        ResponseEntity<List<UserResponseDTO>> response = userController.list();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getId());
    }



    @Test
    void updateRoleName_WithValidRequest_ShouldReturnUpdatedUser() {
        // Arrange
        Set<String> roleNames = Set.of("TEACHER");
        when(userService.updateRoleUser(eq(userId), any(Set.class)))
                .thenReturn(userResponseDTO);

        // Act
        ResponseEntity<UserResponseDTO> response =
                userController.updateRoleName(userId, roleNames);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        verify(userService, times(1)).updateRoleUser(eq(userId), any(Set.class));
    }


}