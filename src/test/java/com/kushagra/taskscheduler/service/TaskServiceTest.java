package com.kushagra.taskscheduler.service;

import com.kushagra.taskscheduler.dto.TaskRequest;
import com.kushagra.taskscheduler.dto.TaskResponse;
import com.kushagra.taskscheduler.entity.Task;
import com.kushagra.taskscheduler.entity.TaskStatus;
import com.kushagra.taskscheduler.exception.TaskNotFoundException;
import com.kushagra.taskscheduler.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventProducer taskEventProducer;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Sample Task");
        sampleTask.setDescription("Sample Description");
        sampleTask.setStatus(TaskStatus.PENDING);
    }

    @Test
    void createTask_shouldSaveAndPublishEvent_whenNotScheduled() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");

        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.createTask(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Sample Task");
        verify(taskEventProducer, times(1)).publishTaskReady(1L);
    }

    @Test
    void getTaskById_shouldReturnTask_whenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponse response = taskService.getTaskById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Sample Task");
    }

    @Test
    void getTaskById_shouldThrowException_whenTaskDoesNotExist() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getAllTasks_shouldReturnEmptyList_whenNoTasksExist() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<TaskResponse> responses = taskService.getAllTasks();

        assertThat(responses).isEmpty();
    }

    @Test
    void deleteTask_shouldThrowException_whenTaskDoesNotExist() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).deleteById(anyLong());
    }
}