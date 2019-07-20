package com.example.waves_app.model;

import java.util.List;

public class Category {

    private String categoryName;
    private int categorySize;

    public Category() { }

    // Getter methods
    public String getCategoryName() {
        return categoryName;
    }

    public int getCategorySize() {
        // TODO - read lines or something from user persistence file
        return categorySize;
    }

    // Setter methods
    public void setCategoryName(String name) {
        categoryName = name;
    }

    public void setCategorySize() {
        // TODO - READ THROUGH THE FILE FROM USER PERSISTENCE
    }
}