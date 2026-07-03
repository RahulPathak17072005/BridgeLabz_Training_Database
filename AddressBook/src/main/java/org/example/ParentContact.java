package org.example;

public class ParentContact {
    private String parentName;
    private String parentEmail;
    private String contactPhone;
    private String gradeLevel;
    public ParentContact(String parentName, String parentEmail, String contactPhone, String gradeLevel) {
        this.parentName = parentName;
        this.parentEmail = parentEmail;
        this.contactPhone = contactPhone;
        this.gradeLevel = gradeLevel;
    }

    public String getParentName() {
        return parentName;
    }
    public String getParentEmail() {
        return parentEmail;}

    public String getContactPhone() {
        return contactPhone;

    }

    public String getGradeLevel() {
        return gradeLevel;

    }

    @Override
    public String toString() {
        return parentName + " - " + parentEmail + " - " + contactPhone + " - " + gradeLevel;
    }
}
