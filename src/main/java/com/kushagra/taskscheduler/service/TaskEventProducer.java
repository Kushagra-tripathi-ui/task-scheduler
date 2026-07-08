package com.kushagra.taskscheduler.service;

import com.kushagra.taskscheduler.dto.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(TaskEventProducer.class);
    private static final String TOPIC = "task-events";

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, TaskEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskReady(Long taskId) {
        TaskEvent event = new TaskEvent(taskId, "TASK_READY");
        logger.info("Publishing event for task {} to topic {}", taskId, TOPIC);
        kafkaTemplate.send(TOPIC, taskId.toString(), event);
    }
}