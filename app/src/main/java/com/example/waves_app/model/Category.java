/*
 * Project: Waves
 *
 * Purpose: Models a category object
 *
 * Reference(s): Angela Liu
 */

package com.example.waves_app.model;

public class Category {

    private String categoryName;

    public Category() { }

    // Getter methods
    public String getCategoryName() {
        return categoryName;
    }

    // Setter methods
    public void setCategoryName(String name) {
        categoryName = name;
    }
}