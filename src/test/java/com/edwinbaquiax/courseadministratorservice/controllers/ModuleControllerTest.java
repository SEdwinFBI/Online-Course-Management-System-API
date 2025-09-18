package com.edwinbaquiax.courseadministratorservice.controllers;

import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleResponseDTO;
import com.edwinbaquiax.courseadministratorservice.services.module.IModuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleControllerTest {

    @Mock
    private IModuleService moduleService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ModuleController moduleController;

    private ModuleRequestDTO moduleRequestDTO;
    private ModuleResponseDTO moduleResponseDTO;
    private final Long moduleId = 1L;
    private final Long courseId = 1L;

    @BeforeEach
    void setUp() {

        moduleRequestDTO = new ModuleRequestDTO();
        moduleRequestDTO.setModuleName("Test Module");
        moduleRequestDTO.setDescription("Test Description");
        moduleRequestDTO.setCourseId(courseId);

        moduleResponseDTO = new ModuleResponseDTO();
        moduleResponseDTO.setId(moduleId);
        moduleResponseDTO.setModuleName("Test Module");
        moduleResponseDTO.setDescription("Test Description");
        moduleResponseDTO.setCourseId(courseId);
    }

    @Test
    void createModule_WithValidRequest_ShouldReturnCreated() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(moduleService.createModule(any(ModuleRequestDTO.class)))
                .thenReturn(moduleResponseDTO);

        // Act
        ResponseEntity<?> response = moduleController.createModule(moduleRequestDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ModuleResponseDTO);
        assertEquals(moduleId, ((ModuleResponseDTO) response.getBody()).getId());
    }

    @Test
    void createModule_WithValidationErrors_ShouldReturnValidationErrors() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> response = moduleController.createModule(moduleRequestDTO, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateModule_WithValidRequest_ShouldReturnOk() {
        // Arrange
        when(moduleService.updateModule(eq(moduleId), any(ModuleRequestDTO.class)))
                .thenReturn(moduleResponseDTO);

        // Act
        ResponseEntity<ModuleResponseDTO> response =
                moduleController.updateModule(moduleId, moduleRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(moduleId, response.getBody().getId());
    }

    @Test
    void deleteModule_WithValidId_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(moduleService).deleteModule(moduleId);

        // Act
        ResponseEntity<Void> response = moduleController.deleteModule(moduleId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(moduleService, times(1)).deleteModule(moduleId);
    }

    @Test
    void findById_WithValidId_ShouldReturnModule() {
        // Arrange
        when(moduleService.findById(moduleId)).thenReturn(moduleResponseDTO);

        // Act
        ResponseEntity<ModuleResponseDTO> response = moduleController.findById(moduleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(moduleId, response.getBody().getId());
    }

    @Test
    void findByCourseId_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<ModuleResponseDTO> page = new PageImpl<>(
                Collections.singletonList(moduleResponseDTO),
                PageRequest.of(0, 20), 1);

        when(moduleService.findAllModulesByCourseId(eq(courseId), eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<ModuleResponseDTO>> response =
                moduleController.findByCourseId(courseId, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(moduleId, response.getBody().getContent().get(0).getId());
    }

    @Test
    void findAllModules_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<ModuleResponseDTO> page = new PageImpl<>(
                Collections.singletonList(moduleResponseDTO),
                PageRequest.of(0, 20), 1);

        when(moduleService.findAllModules(eq(0), eq(20)))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<ModuleResponseDTO>> response =
                moduleController.findAllModules(0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(moduleId, response.getBody().getContent().get(0).getId());
    }
}