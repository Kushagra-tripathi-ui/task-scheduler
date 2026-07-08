package com.kushagra.taskscheduler.dto;

public class TaskEvent {

    private Long taskId;
    private String eventType;

    public TaskEvent() {
    }

    public TaskEvent(Long taskId, String eventType) {
        this.taskId = taskId;
        this.eventType = eventType;
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}