package com.kushagra.taskscheduler.service;

import com.kushagra.taskscheduler.entity.Task;
import com.kushagra.taskscheduler.entity.TaskStatus;
import com.kushagra.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TaskWorkerService {

    private static final Logger logger = LoggerFactory.getLogger(TaskWorkerService.class);

    private final TaskRepository taskRepository;

    public TaskWorkerService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Async("taskExecutor")
    public void executeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found: " + taskId));

        logger.info("Starting execution of task {} on thread {}", taskId, Thread.currentThread().getName());

        task.setStatus(TaskStatus.RUNNING);
        taskRepository.save(task);

        try {
            simulateWork();
            task.setStatus(TaskStatus.COMPLETED);
            logger.info("Task {} completed successfully", taskId);
        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
            logger.error("Task {} failed: {}", taskId, e.getMessage());
        }

        taskRepository.save(task);
    }

    private void simulateWork() throws InterruptedException {
        Thread.sleep(3000);
    }
}