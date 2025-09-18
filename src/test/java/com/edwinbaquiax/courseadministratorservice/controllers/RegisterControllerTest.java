package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.user.RegisterUserDTO;
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
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private IUserService userService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegisterController registerController;

    private RegisterUserDTO registerUserDTO;
    private UserResponseDTO userResponseDTO;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setUsername("testuser");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("Password123!");
        registerUserDTO.setName("Test");
        registerUserDTO.setLastname("User");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userId);
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setEmail("test@example.com");
        userResponseDTO.setName("Test");
        userResponseDTO.setLastname("User");
    }

    @Test
    void register_WithValidRequest_ShouldReturnCreated() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(userService.save(any())).thenReturn(userResponseDTO);

        // Act
        ResponseEntity<?> response = registerController.register(registerUserDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserResponseDTO);
        assertEquals(userId, ((UserResponseDTO) response.getBody()).getId());
    }

    @Test
    void register_WithValidationErrors_ShouldReturnBadRequest() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> response = registerController.register(registerUserDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}