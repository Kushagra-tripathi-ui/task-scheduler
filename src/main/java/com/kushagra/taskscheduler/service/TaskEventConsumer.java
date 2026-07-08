package com.kushagra.taskscheduler.service;

import com.kushagra.taskscheduler.dto.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaskEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TaskEventConsumer.class);

    private final TaskWorkerService taskWorkerService;

    public TaskEventConsumer(TaskWorkerService taskWorkerService) {
        this.taskWorkerService = taskWorkerService;
    }

    @KafkaListener(topics = "task-events", groupId = "task-scheduler-group")
    public void consumeTaskEvent(TaskEvent event) {
        logger.info("Consumed event: taskId={}, eventType={}", event.getTaskId(), event.getEventType());

        if ("TASK_READY".equals(event.getEventType())) {
            taskWorkerService.executeTask(event.getTaskId());
        }
    }
}