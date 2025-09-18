package com.edwinbaquiax.courseadministratorservice.services.module;

import com.edwinbaquiax.courseadministratorservice.exceptions.CourseNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;
import com.edwinbaquiax.courseadministratorservice.models.mappers.ModuleProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.ICourseRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private IModuleRepository moduleRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private ModuleService moduleService;

    private ModuleRequestDTO moduleRequestDTO;
    private Module module;
    private Course course;
    private final Long moduleId = 1L;
    private final Long courseId = 1L;

    @BeforeEach
    void setUp() {

        moduleRequestDTO = new ModuleRequestDTO();
        moduleRequestDTO.setModuleName("Test Module");
        moduleRequestDTO.setDescription("Test Description");
        moduleRequestDTO.setCourseId(courseId);

        course = new Course();
        course.setId(courseId);
        course.setTitle("Test Course");

        module = new Module();
        module.setId(moduleId);
        module.setModuleName("Test Module");
        module.setDescription("Test Description");
        module.setCourse(course);
        module.setActive(true);
    }

    @Test
    void createModule_WithValidRequest_ShouldReturnModule() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        // Act
        ModuleResponseDTO result = moduleService.createModule(moduleRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(moduleId, result.getId());
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void createModule_WithNonExistentCourse_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () ->
                moduleService.createModule(moduleRequestDTO));
    }

    @Test
    void updateModule_WithValidRequest_ShouldReturnUpdatedModule() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        // Act
        ModuleResponseDTO result = moduleService.updateModule(moduleId, moduleRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(moduleId, result.getId());
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void updateModule_WithNonExistentModule_ShouldThrowException() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ModuleNotFoundException.class, () ->
                moduleService.updateModule(moduleId, moduleRequestDTO));
    }

    @Test
    void updateModule_WithNonExistentCourse_ShouldThrowException() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CourseNotFoundException.class, () ->
                moduleService.updateModule(moduleId, moduleRequestDTO));
    }

    @Test
    void deleteModule_WithValidId_ShouldDeactivateModule() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        // Act
        moduleService.deleteModule(moduleId);

        // Assert
        assertFalse(module.isActive());
        verify(moduleRepository, times(1)).save(module);
    }

    @Test
    void deleteModule_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ModuleNotFoundException.class, () ->
                moduleService.deleteModule(moduleId));
    }

    @Test
    void findById_WithValidId_ShouldReturnModule() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));

        // Act
        ModuleResponseDTO result = moduleService.findById(moduleId);

        // Assert
        assertNotNull(result);
        assertEquals(moduleId, result.getId());
    }

    @Test
    void findById_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ModuleNotFoundException.class, () ->
                moduleService.findById(moduleId));
    }

    @Test
    void findAllModules_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<Module> modulePage = new PageImpl<>(Collections.singletonList(module));
        when(moduleRepository.findAll(any(PageRequest.class))).thenReturn(modulePage);

        // Act
        Page<ModuleResponseDTO> result = moduleService.findAllModules(0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(moduleId, result.getContent().get(0).getId());
    }

    @Test
    void findAllModulesByCourseId_WithValidParameters_ShouldReturnPage() {
        // Arrange
        Page<Module> modulePage = new PageImpl<>(Collections.singletonList(module));
        when(moduleRepository.findAllByCourse_IdAndActive(eq(courseId), eq(true), any(PageRequest.class)))
                .thenReturn(modulePage);

        // Act
        Page<ModuleResponseDTO> result = moduleService.findAllModulesByCourseId(courseId, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(moduleId, result.getContent().get(0).getId());
    }
}