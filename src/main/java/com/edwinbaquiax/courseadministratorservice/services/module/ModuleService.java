package com.edwinbaquiax.courseadministratorservice.services.module;

import com.edwinbaquiax.courseadministratorservice.exceptions.CourseNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleResponseDTO;

import static com.edwinbaquiax.courseadministratorservice.models.mappers.ModuleProfile.*;

import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Course;
import com.edwinbaquiax.courseadministratorservice.models.mappers.ModuleProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.ICourseRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IModuleRepository;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ModuleService implements IModuleService {

    @Autowired
    private IModuleRepository moduleRepository;
    @Autowired
    private ICourseRepository courseRepository;

    @Override
    public ModuleResponseDTO createModule(ModuleRequestDTO request) {
        Module module = moduleRequestDtoToEntity(request);
        Course existCourse = courseRepository.findById(request.getCourseId()).orElseThrow(CourseNotFoundException::new);
        module.setCourse(existCourse);

        Module saved = moduleRepository.save(module);
        return entityToModuleResponseDTO(saved);
    }

    @Override
    public ModuleResponseDTO updateModule(Long moduleId, ModuleRequestDTO request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(ModuleNotFoundException::new);
        Course existCourse = courseRepository.findById(request.getCourseId()).orElseThrow(CourseNotFoundException::new);
        module.setCourse(existCourse);
        updateEntityFromDto(module, request);
        Module updated = moduleRepository.save(module);
        return entityToModuleResponseDTO(updated);
    }

    @Override
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(ModuleNotFoundException::new);
        module.setActive(false);
        moduleRepository.save(module);
    }

    @Override
    public ModuleResponseDTO findById(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(ModuleNotFoundException::new);
        return entityToModuleResponseDTO(module);
    }

    @Override
    public Page<ModuleResponseDTO> findAllModules(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("moduleName").ascending());
        Page<Module> modules = moduleRepository.findAll(pageable);
        return modules.map(ModuleProfile::entityToModuleResponseDTO);
    }

    @Override
    public Page<ModuleResponseDTO> findAllModulesByCourseId(long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("moduleName").ascending());
        Page<Module> modules = moduleRepository.findAllByCourse_IdAndActive(courseId, true, pageable);

        return modules.map(ModuleProfile::entityToModuleResponseDTO);
    }
}