package com.kushagra.taskscheduler.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDateTime scheduledAt;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
}