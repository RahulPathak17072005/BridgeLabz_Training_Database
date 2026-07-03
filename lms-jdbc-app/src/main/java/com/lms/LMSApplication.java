package com.lms;

import com.lms.dao.AdminDAO;
import com.lms.dao.CandidateDAO;
import com.lms.dao.OnboardingDAO;
import java.util.Scanner;

public class LMSApplication {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        AdminDAO adminDAO           = new AdminDAO();
        CandidateDAO candidateDAO   = new CandidateDAO();
        OnboardingDAO onboardingDAO = new OnboardingDAO();

        while (true) {
            System.out.println("\n===== LMS MAIN MENU =====");
            System.out.println("1. Admin Dashboard");
            System.out.println("2. Candidate Hiring Portal");
            System.out.println("3. Candidate Onboarding Portal");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> adminPortal(adminDAO);
                case 2 -> hiringPortal(candidateDAO);
                case 3 -> onboardingPortal(onboardingDAO);
                case 4 -> { System.out.println("Bye!"); System.exit(0); }
                default -> System.out.println("Enter 1-4.");
            }
        }
    }

    static void adminPortal(AdminDAO dao) {
        System.out.print("Email: ");    String email = sc.nextLine().trim();
        System.out.print("Password: "); String pass  = sc.nextLine().trim();

        if (!dao.authenticateAdmin(email, pass)) {
            System.out.println("Login failed.");
            return;
        }
        System.out.println("Welcome Admin!");

        while (true) {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Add Job Opening");
            System.out.println("2. Add Training Course");
            System.out.println("3. Add Interviewer");
            System.out.println("4. View Hiring Pipeline");
            System.out.println("5. View Onboarding Summary");
            System.out.println("6. Logout");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> {
                    System.out.print("Title: ");       String title = sc.nextLine().trim();
                    System.out.print("Department: ");  String dept  = sc.nextLine().trim();
                    dao.addJob(title, dept);
                }
                case 2 -> {
                    System.out.print("Title: ");       String title = sc.nextLine().trim();
                    System.out.print("Description: "); String desc  = sc.nextLine().trim();
                    dao.addCourse(title, desc);
                }
                case 3 -> {
                    System.out.print("Name: ");  String name  = sc.nextLine().trim();
                    System.out.print("Email: "); String email2 = sc.nextLine().trim();
                    dao.addInterviewer(name, email2);
                }
                case 4 -> dao.viewHiringPipeline();
                case 5 -> dao.viewOnboardingSummary();
                case 6 -> { return; }
                default -> System.out.println("Enter 1-6.");
            }
        }
    }

    static void hiringPortal(CandidateDAO dao) {
        while (true) {
            System.out.println("\n===== HIRING PORTAL =====");
            System.out.println("1. View Open Jobs");
            System.out.println("2. Apply for a Job");
            System.out.println("3. List Candidates");
            System.out.println("4. View Candidate Full Profile");
            System.out.println("5. List Interviewers");
            System.out.println("6. Schedule Interview");
            System.out.println("7. List Interviews");
            System.out.println("8. Submit Interview Feedback & Score");
            System.out.println("9. Back");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> dao.listOpenJobs();
                case 2 -> {
                    System.out.print("Name: ");    String name  = sc.nextLine().trim();
                    System.out.print("Email: ");   String email = sc.nextLine().trim();
                    System.out.print("Phone: ");   String phone = sc.nextLine().trim();
                    dao.listOpenJobs();
                    System.out.print("Job ID: ");  int jobId = readInt();
                    System.out.print("Skills (comma separated): ");
                    String[] skills = sc.nextLine().trim().split(",");
                    System.out.print("Previous Companies (comma separated): ");
                    String[] companies = sc.nextLine().trim().split(",");
                    dao.applyJob(name, email, phone, jobId, skills, companies);
                }
                case 3 -> dao.listAllCandidates();
                case 4 -> {
                    dao.listAllCandidates();
                    System.out.print("Candidate ID: "); int cid = readInt();
                    dao.viewCandidateDetails(cid);
                }
                case 5 -> dao.listInterviewers();
                case 6 -> {
                    dao.listAllCandidates();
                    System.out.print("Candidate ID: ");   int cid  = readInt();
                    dao.listInterviewers();
                    System.out.print("Interviewer ID: "); int ivId = readInt();
                    System.out.print("Date Time (yyyy-MM-dd HH:mm:ss): ");
                    String dt = sc.nextLine().trim();
                    dao.scheduleInterview(cid, ivId, dt);
                }
                case 7 -> dao.listInterviews();
                case 8 -> {
                    dao.listInterviews();
                    System.out.print("Interview ID: ");    int iid   = readInt();
                    System.out.print("Score (0-100): ");   int score = readInt();
                    System.out.print("Feedback: ");        String fb = sc.nextLine().trim();
                    System.out.print("Status (Passed/Failed): "); String st = sc.nextLine().trim();
                    dao.submitInterviewScore(iid, score, fb, st);
                }
                case 9 -> { return; }
                default -> System.out.println("Enter 1-9.");
            }
        }
    }

    static void onboardingPortal(OnboardingDAO dao) {
        while (true) {
            System.out.println("\n===== ONBOARDING PORTAL =====");
            System.out.println("1. Hire Candidate");
            System.out.println("2. View Candidate Training Courses");
            System.out.println("3. Update Training Course Progress");
            System.out.println("4. Back");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> {
                    System.out.print("Candidate ID: "); int cid = readInt();
                    dao.hireCandidate(cid);
                }
                case 2 -> {
                    System.out.print("Candidate ID: "); int cid = readInt();
                    dao.viewCandidateCourses(cid);
                }
                case 3 -> {
                    System.out.print("Candidate ID: ");  int cid      = readInt();
                    dao.viewCandidateCourses(cid);
                    System.out.print("Course ID: ");     int courseId = readInt();
                    System.out.print("Status (Not Started/In Progress/Completed): ");
                    String status = sc.nextLine().trim();
                    System.out.print("Percentage (0-100): "); int pct = readInt();
                    dao.updateCourseProgress(cid, courseId, status, pct);
                }
                case 4 -> { return; }
                default -> System.out.println("Enter 1-4.");
            }
        }
    }

    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}