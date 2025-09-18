package com.edwinbaquiax.courseadministratorservice.models.mappers;

import com.edwinbaquiax.courseadministratorservice.exceptions.TypeTaskNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Task;
import com.edwinbaquiax.courseadministratorservice.models.enums.TypeTask;

import java.time.LocalDateTime;

public class TaskProfile {
    public static Task taskRequestDtoToTaskEntity(TaskRequestDTO dto) {
        try{
            return Task.builder()
                    .title(dto.getTitle())
                    .value(dto.getValue())
                    .description(dto.getDescription())
                    .instructions(dto.getInstructions())
                    .active(true) //por defecto
                    .typeTask(TypeTask.valueOf(dto.getTypeTask()).toString())
                    .moduleId(dto.getModuleId())
                    .createdAt(LocalDateTime.now())
                    .build();
        }catch (IllegalArgumentException e){
            throw  new TypeTaskNotFoundException(String.format("El tipo %s no es soportado",dto.getTypeTask()));
        }
    }
    public static TaskResponseDTO taskEntityToResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .value(task.getValue())
                .description(task.getDescription())
                .instructions(task.getInstructions())
                .active(task.isActive())
                .typeTask(task.getTypeTask())
                .moduleId(task.getModuleId())
                .createdAt(task.getCreatedAt())
                .build();
    }

    public static void updateTaskFromDto(Task task, TaskRequestDTO dto) {
        try{
            task.setTitle(dto.getTitle());
            task.setValue(dto.getValue());
            task.setDescription(dto.getDescription());
            task.setInstructions(dto.getInstructions());
            task.setActive(dto.isActive());
            task.setTypeTask(TypeTask.valueOf(dto.getTypeTask()).toString());
            task.setModuleId(dto.getModuleId());
        }catch (IllegalArgumentException e){
            throw  new TypeTaskNotFoundException(String.format("El tipo %s no es soportado",dto.getTypeTask()));
        }


    }
}
