package com.kushagra.taskscheduler.controller;
import com.kushagra.taskscheduler.service.TaskEventProducer;
import com.kushagra.taskscheduler.dto.TaskRequest;
import com.kushagra.taskscheduler.dto.TaskResponse;
import com.kushagra.taskscheduler.service.TaskService;
import com.kushagra.taskscheduler.service.TaskWorkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskWorkerService taskWorkerService;
    private final TaskEventProducer taskEventProducer;

    public TaskController(TaskService taskService, TaskWorkerService taskWorkerService, TaskEventProducer taskEventProducer) {
        this.taskService = taskService;
        this.taskWorkerService = taskWorkerService;
        this.taskEventProducer = taskEventProducer;
    }
    @PostMapping("/{id}/simulate-consumer-crash")
    public ResponseEntity<String> simulateConsumerCrash(@PathVariable Long id) {
        taskEventProducer.publishConsumerCrashTest(id);
        return ResponseEntity.accepted().body("Consumer crash test event published for id: " + id);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<String> executeTask(@PathVariable Long id) {
        taskWorkerService.executeTask(id);
        return ResponseEntity.accepted().body("Task execution started for id: " + id);
    }
}