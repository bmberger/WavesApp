package com.example.waves_app.model;

public class Task {

    public String taskDetail;
    public String dueDate;

    public Task() { }

    // Getter methods
    public String getTaskDetail() {
        return taskDetail;
    }

    public String getDueDate() {
        return dueDate;
    }

    // Setter methods
    public void setTaskDetail(String taskInfo) {
        taskDetail = taskInfo;
    }

    public void setDueDate(String date) {
        dueDate = date;
    }
}