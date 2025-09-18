package com.edwinbaquiax.courseadministratorservice.services.task;

import com.edwinbaquiax.courseadministratorservice.exceptions.ModuleNotFoundException;
import com.edwinbaquiax.courseadministratorservice.exceptions.TaskNotFoundException;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskRequestDTO;
import com.edwinbaquiax.courseadministratorservice.models.dtos.task.TaskResponseDTO;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Assignment;
import com.edwinbaquiax.courseadministratorservice.models.entities.mongo.Task;
import com.edwinbaquiax.courseadministratorservice.models.entities.sql.Module;

import static com.edwinbaquiax.courseadministratorservice.models.mappers.TaskProfile.*;

import com.edwinbaquiax.courseadministratorservice.models.enums.TypeTask;
import com.edwinbaquiax.courseadministratorservice.models.mappers.TaskProfile;
import com.edwinbaquiax.courseadministratorservice.repositories.mongo.IAssignmentRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.mongo.ITaskRepository;
import com.edwinbaquiax.courseadministratorservice.repositories.sql.IModuleRepository;
import com.edwinbaquiax.courseadministratorservice.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements ITaskService {
    @Autowired
    private ITaskRepository taskRepository;
    @Autowired
    private IModuleRepository moduleRepository;
    @Autowired
    private IAssignmentRepository assignmentRepository;

    @Autowired
    private CurrentUser currentUser;


    @Override
    public TaskResponseDTO createTask(Long moduleId, TaskRequestDTO request) {

        Module module = moduleRepository.findByIdAndCourse_Teacher_Id(moduleId,currentUser.getUserId())
                .orElseThrow(ModuleNotFoundException::new);


        Task task = taskRequestDtoToTaskEntity(request);
        task.setModuleId(module.getId());

        Task saved = taskRepository.save(task);
        return taskEntityToResponseDTO(saved);
    }

    @Override
    public TaskResponseDTO updateTask(String taskId, TaskRequestDTO request) {
        moduleRepository.findByIdAndCourse_Teacher_Id(request.getModuleId(),currentUser.getUserId())
                .orElseThrow(ModuleNotFoundException::new);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        updateTaskFromDto(task, request);
        Task updated = taskRepository.save(task);

        return taskEntityToResponseDTO(updated);
    }

    @Override
    public void deleteTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        task.setActive(false);
        taskRepository.save(task);
    }

    @Override
    public TaskResponseDTO findById(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        return taskEntityToResponseDTO(task);
    }

    @Override
    public Page<TaskResponseDTO> findTasksByModule(Long moduleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Task> tasks = taskRepository.findAllByModuleIdAndActiveTrue(moduleId, pageable);
        return tasks.map(TaskProfile::taskEntityToResponseDTO);
    }

    @Override
    public Page<TaskResponseDTO> findTasksByStudent(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Assignment> assignments = assignmentRepository.findAllByUserId(userId, pageable);

        List<String> taskIds = assignments
                .stream()
                .map((Assignment::getTaskId))
                .toList();
        List<Task> tasks = taskRepository.findAllById(taskIds);


        return new PageImpl<>(
                tasks
                        .stream()
                        .map(TaskProfile::taskEntityToResponseDTO)
                        .toList(),
                pageable,
                tasks.size()
        );
    }
}
