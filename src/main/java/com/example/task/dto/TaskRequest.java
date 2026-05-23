package com.example.task.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;

    public TaskRequest() {}
    public TaskRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
