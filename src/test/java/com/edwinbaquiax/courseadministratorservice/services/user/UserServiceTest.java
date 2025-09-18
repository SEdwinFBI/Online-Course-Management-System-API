package com.edwinbaquiax.courseadministratorservice.services.user;

import com.edwinbaquiax.courseadministratorservice.exceptions.RoleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UserNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.UsernameIsExistsException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.user.UserResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Role;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.User;
import com.edwinbaquiax.courseadministratorservice.models.mappers.UserProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IRoleRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;
    private final Long userId = 1L;
    private final Long roleId = 1L;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test");
        user.setLastname("User");
        user.setEnabled(true);

        role = new Role();
        role.setId(roleId);
        role.setName("ROLE_STUDENT");
        role.setDescription("Student role");

        user.setRoles(new HashSet<>(Collections.singletonList(role)));
    }

    @Test
    void findAll_ShouldReturnListOfUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getId());
    }

    @Test
    void findById_WithValidId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void findById_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void findByUsername_WithValidUsername_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void findByUsername_WithNonExistentUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("testuser"));
    }

    @Test
    void save_WithValidUser_ShouldReturnSavedUser() {
        // Arrange
        when(userRepository.existsByUsernameOrEmail("testuser", "test@example.com"))
                .thenReturn(false);
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDTO result = userService.save(user);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void save_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsernameOrEmail("testuser", "test@example.com"))
                .thenReturn(true);

        // Act & Assert
        assertThrows(UsernameIsExistsException.class, () -> userService.save(user));
    }

    @Test
    void updateRoleUser_WithValidRequest_ShouldReturnUpdatedUser() {
        // Arrange
        Set<String> roleNames = Set.of("TEACHER");
        Role teacherRole = new Role();
        teacherRole.setId(2L);
        teacherRole.setName("ROLE_TEACHER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.of(teacherRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDTO result = userService.updateRoleUser(userId, roleNames);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateRoleUser_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        Set<String> roleNames = Set.of("TEACHER");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                userService.updateRoleUser(userId, roleNames));
    }

    @Test
    void updateRoleUser_WithNonExistentRole_ShouldThrowException() {
        // Arrange
        Set<String> roleNames = Set.of("INVALID_ROLE");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_INVALID_ROLE")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoleNotFoundException.class, () ->
                userService.updateRoleUser(userId, roleNames));
    }

    @Test
    void existsByUsername_WithExistingUsername_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertTrue(result);
    }

    @Test
    void existsByUsername_WithNonExistingUsername_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertFalse(result);
    }
}