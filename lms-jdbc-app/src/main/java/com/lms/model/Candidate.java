package com.lms.model;

public class Candidate {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int jobId;
    private String status;

    public Candidate() {}

    public Candidate(int id, String name, String email, String phone, int jobId, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.jobId = jobId;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getJobId() { return jobId; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public String toString() {
        return "Candidate [id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", phone=" + phone +
                ", jobId=" + jobId +
                ", status=" + status + "]";
    }
}