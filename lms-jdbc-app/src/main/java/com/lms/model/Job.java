package com.lms.model;

public class Job {
    private int id;
    private String title;
    private String department;
    private String status;

    public Job() {}

    public Job(int id, String title, String department, String status) {
        this.id = id;
        this.title = title;
        this.department = department;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDepartment(String department) { this.department = department; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", department='" + department + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}