package com.edwinbaquiax.courseadministratorservice.models.mappers;
import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;

import com.edwinbaquiax.courseadministratorservice.models.dtos.module.ModuleRequestDTO;

public class ModuleProfile {


    public static Module moduleRequestDtoToEntity(ModuleRequestDTO dto) {
        return Module.builder()
                .moduleName(dto.getModuleName())
                .description(dto.getDescription())

                .build();

    }

    public static ModuleResponseDTO entityToModuleResponseDTO(Module module) {
        return ModuleResponseDTO.builder()
                .id(module.getId())
                .moduleName(module.getModuleName())
                .description(module.getDescription())
                .active(module.isActive())
                .createdAt(module.getCreatedAt())
                .courseId(module.getCourse().getId())
                .build();
    }


    public static void updateEntityFromDto(Module module, ModuleRequestDTO dto) {
        module.setModuleName(dto.getModuleName());
        module.setDescription(dto.getDescription());
    }
}