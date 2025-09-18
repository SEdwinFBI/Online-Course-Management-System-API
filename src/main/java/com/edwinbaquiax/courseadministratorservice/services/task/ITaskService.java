package com.edwinbaquiax.courseadministratorservice.services.task;

import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskResponseDTO;
import org.springframework.data.domain.Page;

public interface ITaskService {
    TaskResponseDTO createTask(Long moduleId, TaskRequestDTO request);

    TaskResponseDTO updateTask(String taskId, TaskRequestDTO request);

    void deleteTask(String taskId);


    TaskResponseDTO findById(String taskId);


    Page<TaskResponseDTO> findTasksByModule(Long moduleId, int page, int size);

    Page<TaskResponseDTO> findTasksByStudent(Long userId, int page, int size);
}
