/*
 * Project: Waves
 *
 * Purpose: Models a task object
 *
 * Reference(s): Angela Liu
 */

package com.example.waves_app.model;

public class Task {

    private String taskDetail;
    private String dueDate;

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