package com.lms.model;

public class Course {
    private int id;
    private String title;
    private String description;

    public Course() {}

    public Course(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Course [id=" + id +
                ", title=" + title +
                ", description=" + description + "]";
    }
}