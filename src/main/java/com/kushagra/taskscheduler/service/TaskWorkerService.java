package com.kushagra.taskscheduler.service;

import com.kushagra.taskscheduler.entity.Task;
import com.kushagra.taskscheduler.entity.TaskStatus;
import com.kushagra.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        logger.info("Starting execution of task {} on thread {} (attempt {}/{})",
                taskId, Thread.currentThread().getName(), task.getRetryCount() + 1, task.getMaxRetries() + 1);

        task.setStatus(TaskStatus.RUNNING);
        taskRepository.save(task);

        try {
            simulateWork(task);
            task.setStatus(TaskStatus.COMPLETED);
            logger.info("Task {} completed successfully", taskId);
        } catch (Exception e) {
            handleFailure(task, e);
        }

        taskRepository.save(task);
    }

    private void handleFailure(Task task, Exception e) {
        int newRetryCount = task.getRetryCount() + 1;
        task.setRetryCount(newRetryCount);

        if (newRetryCount <= task.getMaxRetries()) {
            logger.warn("Task {} failed (attempt {}/{}), scheduling retry: {}",
                    task.getId(), newRetryCount, task.getMaxRetries() + 1, e.getMessage());
            task.setStatus(TaskStatus.PENDING);
            task.setScheduledAt(LocalDateTime.now().plusSeconds(10));
        } else {
            logger.error("Task {} permanently failed after {} attempts: {}",
                    task.getId(), newRetryCount, e.getMessage());
            task.setStatus(TaskStatus.FAILED);
        }
    }

    private void simulateWork(Task task) throws InterruptedException {
        Thread.sleep(2000);
        if ("FORCE_FAIL".equalsIgnoreCase(task.getTitle())) {
            throw new RuntimeException("Simulated failure for testing retries");
        }
    }
}