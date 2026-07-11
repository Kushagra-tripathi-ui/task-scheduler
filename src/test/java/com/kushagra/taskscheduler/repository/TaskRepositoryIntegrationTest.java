package com.kushagra.taskscheduler.repository;

import com.kushagra.taskscheduler.entity.Task;
import com.kushagra.taskscheduler.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
class TaskRepositoryIntegrationTest {
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/task_scheduler_db");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "kushagra123");
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveAndRetrieveTask() {
        Task task = new Task();
        task.setTitle("Integration Test Task");
        task.setDescription("Testing against real Postgres via Docker Compose");

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindDueTasks_whenScheduledTimeHasPassed() {
        Task pastTask = new Task();
        pastTask.setTitle("Past Task Integration");
        pastTask.setScheduledAt(LocalDateTime.now().minusMinutes(5));
        taskRepository.save(pastTask);

        Task futureTask = new Task();
        futureTask.setTitle("Future Task Integration");
        futureTask.setScheduledAt(LocalDateTime.now().plusHours(1));
        taskRepository.save(futureTask);

        List<Task> dueTasks = taskRepository.findByStatusAndScheduledAtLessThanEqual(
                TaskStatus.PENDING, LocalDateTime.now());

        assertThat(dueTasks).extracting(Task::getTitle).contains("Past Task Integration");
        assertThat(dueTasks).extracting(Task::getTitle).doesNotContain("Future Task Integration");
    }
}