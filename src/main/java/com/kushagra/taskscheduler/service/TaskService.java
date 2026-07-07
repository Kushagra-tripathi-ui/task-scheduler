package com.kushagra.taskscheduler.service;

import com.kushagra.taskscheduler.dto.TaskRequest;
import com.kushagra.taskscheduler.dto.TaskResponse;
import com.kushagra.taskscheduler.entity.Task;
import com.kushagra.taskscheduler.exception.TaskNotFoundException;
import com.kushagra.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setScheduledAt(request.getScheduledAt());
        Task saved = taskRepository.save(task);
        return new TaskResponse(saved);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "tasks", key = "#id")
    public TaskResponse getTaskById(Long id) {
        logger.info("Fetching task {} from DATABASE (cache miss)", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return new TaskResponse(task);
    }

    @CacheEvict(value = "tasks", key = "#id")
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        Task updated = taskRepository.save(task);
        return new TaskResponse(updated);
    }

    @CacheEvict(value = "tasks", key = "#id")
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}