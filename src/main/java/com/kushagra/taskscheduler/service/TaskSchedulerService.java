package com.kushagra.taskscheduler.service;

import com.kushagra.taskscheduler.entity.Task;
import com.kushagra.taskscheduler.entity.TaskStatus;
import com.kushagra.taskscheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerService.class);

    private final TaskRepository taskRepository;
    private final TaskWorkerService taskWorkerService;

    public TaskSchedulerService(TaskRepository taskRepository, TaskWorkerService taskWorkerService) {
        this.taskRepository = taskRepository;
        this.taskWorkerService = taskWorkerService;
    }

    @Scheduled(fixedRate = 5000)
    public void pollAndExecuteDueTasks() {
        List<Task> dueTasks = taskRepository.findByStatusAndScheduledAtLessThanEqual(
                TaskStatus.PENDING, LocalDateTime.now());

        if (!dueTasks.isEmpty()) {
            logger.info("Found {} due task(s) to execute", dueTasks.size());
        }

        for (Task task : dueTasks) {
            taskWorkerService.executeTask(task.getId());
        }
    }
}