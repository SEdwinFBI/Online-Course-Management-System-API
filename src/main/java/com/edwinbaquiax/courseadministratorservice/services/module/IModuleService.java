package com.edwinbaquiax.courseadministratorservice.services.module;

import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleResponseDTO;
import org.springframework.data.domain.Page;

public interface IModuleService {
    ModuleResponseDTO createModule(ModuleRequestDTO request);

    ModuleResponseDTO updateModule(Long moduleId, ModuleRequestDTO request);

    void deleteModule(Long moduleId);

    ModuleResponseDTO findById(Long moduleId);

    Page<ModuleResponseDTO> findAllModules(int page, int size);

    Page<ModuleResponseDTO> findAllModulesByCourseId(long courseId, int page, int size);

}
