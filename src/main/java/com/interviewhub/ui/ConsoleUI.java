package com.interviewhub.ui;

import com.interviewhub.collections.CollectionManager;
import com.interviewhub.database.DBConnection;
import com.interviewhub.exceptions.*;
import com.interviewhub.model.*;
import com.interviewhub.service.AdminService;
import com.interviewhub.service.AuthService;
import com.interviewhub.service.CandidateService;
import com.interviewhub.service.InterviewerService;
import com.interviewhub.threads.AsyncPipelineManager;
import com.interviewhub.threads.InterviewReservationTask;
import com.interviewhub.utils.InputValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Menu-Driven Console User Interface for InterviewHub.
 * Handles user input gracefully, provides clear headings, menus, error handling,
 * and seamless transitions between roles.
 */
public class ConsoleUI {

    private final Scanner scanner;
    private final AuthService authService;
    private final CandidateService candidateService;
    private final InterviewerService interviewerService;
    private final AdminService adminService;
    private final CollectionManager collectionManager;

    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
        this.authService = new AuthService();
        this.candidateService = new CandidateService();
        this.interviewerService = new InterviewerService();
        this.adminService = new AdminService();
        this.collectionManager = new CollectionManager();
    }

    public void start() {
        boolean running = true;
        while (running) {
            printHeader("INTERVIEWHUB - MAIN MENU");
            System.out.println("1.  Candidate Registration");
            System.out.println("2.  Interviewer Registration");
            System.out.println("3.  Candidate Login");
            System.out.println("4.  Interviewer Login");
            System.out.println("5.  Admin Login");
            System.out.println("6.  Run Milestone 3: Multithreading & Synchronization Demo");
            System.out.println("7.  Run Milestone 4: Producer-Consumer & Executor Framework Demo");
            System.out.println("8.  Run Milestone 2: Collections In-Memory Analytics Demo");
            System.out.println("9.  Test Database Connection");
            System.out.println("10. Exit Application");
            System.out.print("\nEnter your choice (1-10): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleCandidateRegistration();
                    break;
                case "2":
                    handleInterviewerRegistration();
                    break;
                case "3":
                    handleCandidateLogin();
                    break;
                case "4":
                    handleInterviewerLogin();
                    break;
                case "5":
                    handleAdminLogin();
                    break;
                case "6":
                    InterviewReservationTask.runConcurrencySimulation();
                    pauseForEnter();
                    break;
                case "7":
                    AsyncPipelineManager.runProducerConsumerSimulation();
                    System.out.println();
                    AsyncPipelineManager.runExecutorServiceSimulation();
                    pauseForEnter();
                    break;
                case "8":
                    runCollectionsDemo();
                    pauseForEnter();
                    break;
                case "9":
                    testDb();
                    pauseForEnter();
                    break;
                case "10":
                    System.out.println("\nThank you for using InterviewHub. Goodbye!");
                    running = false;
                    break;
                default:
                    printError("Invalid option. Please choose a number from 1 to 10.");
            }
        }
    }

    // ==================== AUTHENTICATION HANDLERS ====================

    private void handleCandidateRegistration() {
        printHeader("CANDIDATE REGISTRATION");
        try {
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine();

            System.out.print("Create Password (min 6 characters): ");
            String password = scanner.nextLine();

            System.out.print("Enter College Student ID (e.g. STU202501): ");
            String studentId = scanner.nextLine();

            System.out.print("Enter Department (e.g. Computer Science): ");
            String dept = scanner.nextLine();

            System.out.print("Enter Current CGPA (0.00 - 10.00): ");
            double cgpa = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Expected Graduation Year (e.g. 2026): ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Specialization (e.g. Software Engineering): ");
            String spec = scanner.nextLine();

            Candidate registered = authService.registerCandidate(name, email, password, studentId, dept, cgpa, year, spec);
            printSuccess("Registration successful! Welcome, " + registered.getName() + " (Candidate ID: " + registered.getCandidateId() + ").");
            printSuccess("You may now log in using option 2.");

        } catch (NumberFormatException e) {
            printError("Invalid number format for CGPA or Graduation Year.");
        } catch (InvalidEmailException | DuplicateRecordException | IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Database error during registration: " + e.getMessage());
        }
        pauseForEnter();
    }

    private void handleInterviewerRegistration() {
        printHeader("INTERVIEWER REGISTRATION");
        try {
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine();

            System.out.print("Create Password (min 6 characters): ");
            String password = scanner.nextLine();

            System.out.print("Enter Organization / Company Name: ");
            String org = scanner.nextLine();

            System.out.print("Enter Specialization (e.g. Software Engineering, Human Resources): ");
            String spec = scanner.nextLine();

            System.out.print("Enter Years of Experience: ");
            int expYears = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Expertise / Technical Skills (e.g. Java, Spring Boot, Algorithms): ");
            String expertise = scanner.nextLine();

            Interviewer registered = authService.registerInterviewer(name, email, password, expertise, expYears, spec, org);
            printSuccess("Interviewer registration successful! Welcome, " + registered.getName()
                    + " (Interviewer ID: " + registered.getInterviewerId() + ").");
            printSuccess("You may now log in using option 4.");

        } catch (NumberFormatException e) {
            printError("Invalid number format for Years of Experience.");
        } catch (InvalidEmailException | DuplicateRecordException | IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Database error during registration: " + e.getMessage());
        }
        pauseForEnter();
    }

    private void handleCandidateLogin() {
        printHeader("CANDIDATE LOGIN");
        System.out.print("Email Address: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = authService.login(email, password);
            if (!(user instanceof Candidate)) {
                printError("This account does not have CANDIDATE privileges.");
                return;
            }
            Candidate candidate = (Candidate) user;
            printSuccess("Login successful! Welcome back, " + candidate.getName() + ".");
            candidateDashboard(candidate);
        } catch (UnauthorizedAccessException | InvalidEmailException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Database error during login: " + e.getMessage());
        }
    }

    private void handleInterviewerLogin() {
        printHeader("INTERVIEWER LOGIN");
        System.out.print("Email Address: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = authService.login(email, password);
            if (!(user instanceof Interviewer)) {
                printError("This account does not have INTERVIEWER privileges.");
                return;
            }
            Interviewer interviewer = (Interviewer) user;
            printSuccess("Login successful! Welcome, " + interviewer.getName() + ".");
            interviewerDashboard(interviewer);
        } catch (UnauthorizedAccessException | InvalidEmailException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Database error during login: " + e.getMessage());
        }
    }

    private void handleAdminLogin() {
        printHeader("ADMIN LOGIN");
        System.out.print("Email Address: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = authService.login(email, password);
            if (!(user instanceof Admin)) {
                printError("This account does not have ADMIN privileges.");
                return;
            }
            Admin admin = (Admin) user;
            printSuccess("Admin authentication verified. Welcome, " + admin.getName() + ".");
            adminDashboard(admin);
        } catch (UnauthorizedAccessException | InvalidEmailException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Database error during login: " + e.getMessage());
        }
    }

    // ==================== CANDIDATE DASHBOARD ====================

    private void candidateDashboard(Candidate candidate) {
        boolean active = true;
        while (active) {
            printHeader("CANDIDATE DASHBOARD - " + candidate.getName());
            System.out.println("1. View My Profile");
            System.out.println("2. Update My Profile");
            System.out.println("3. View Available Interviewers");
            System.out.println("4. Search Interviewers by Specialization");
            System.out.println("5. Request an Interview");
            System.out.println("6. View My Interview Requests");
            System.out.println("7. Cancel Pending Interview Request");
            System.out.println("8. View Upcoming / Scheduled Interviews");
            System.out.println("9. View Interview History & Completed Evaluations");
            System.out.println("10. View My Comprehensive Performance Report");
            System.out.println("11. View Campus Placement Drives & Apply (Milestone 1)");
            System.out.println("12. View My Placement Drive Applications");
            System.out.println("13. Logout");
            System.out.print("\nEnter your choice (1-13): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewCandidateProfile(candidate);
                    break;
                case "2":
                    updateCandidateProfile(candidate);
                    break;
                case "3":
                    viewAvailableInterviewers();
                    break;
                case "4":
                    searchInterviewers();
                    break;
                case "5":
                    requestInterview(candidate);
                    break;
                case "6":
                    viewCandidateRequests(candidate);
                    break;
                case "7":
                    cancelCandidateRequest(candidate);
                    break;
                case "8":
                    viewCandidateUpcoming(candidate);
                    break;
                case "9":
                    viewCandidateHistory(candidate);
                    break;
                case "10":
                    viewCandidatePerformanceReport(candidate);
                    break;
                case "11":
                    viewAndApplyPlacementDrives(candidate);
                    break;
                case "12":
                    viewMyApplications(candidate);
                    break;
                case "13":
                    printSuccess("Logging out from Candidate account...");
                    active = false;
                    break;
                default:
                    printError("Invalid option.");
            }
            if (active) pauseForEnter();
        }
    }

    private void viewCandidateProfile(Candidate candidate) {
        try {
            Candidate c = candidateService.getProfile(candidate.getCandidateId());
            printHeader("CANDIDATE PROFILE");
            System.out.println("Candidate ID       : " + c.getCandidateId());
            System.out.println("Student ID         : " + c.getStudentId());
            System.out.println("Full Name          : " + c.getName());
            System.out.println("Email Address      : " + c.getEmail());
            System.out.println("Department         : " + c.getDepartment());
            System.out.printf("CGPA               : %.2f / 10.00%n", c.getCgpa());
            System.out.println("Graduation Year    : " + c.getGraduationYear());
            System.out.println("Specialization     : " + c.getSpecialization());
            System.out.println("Account Status     : " + c.getStatus());
        } catch (SQLException e) {
            printError("Failed to fetch profile: " + e.getMessage());
        }
    }

    private void updateCandidateProfile(Candidate candidate) {
        printHeader("UPDATE PROFILE");
        try {
            System.out.print("New Full Name (" + candidate.getName() + "): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) candidate.setName(name);

            System.out.print("New Department (" + candidate.getDepartment() + "): ");
            String dept = scanner.nextLine().trim();
            if (!dept.isEmpty()) candidate.setDepartment(dept);

            System.out.print("New CGPA (" + candidate.getCgpa() + "): ");
            String cgpaStr = scanner.nextLine().trim();
            if (!cgpaStr.isEmpty()) candidate.setCgpa(Double.parseDouble(cgpaStr));

            System.out.print("New Graduation Year (" + candidate.getGraduationYear() + "): ");
            String yrStr = scanner.nextLine().trim();
            if (!yrStr.isEmpty()) candidate.setGraduationYear(Integer.parseInt(yrStr));

            System.out.print("New Specialization (" + candidate.getSpecialization() + "): ");
            String spec = scanner.nextLine().trim();
            if (!spec.isEmpty()) candidate.setSpecialization(spec);

            candidateService.updateProfile(candidate);
            printSuccess("Profile updated successfully!");
        } catch (Exception e) {
            printError("Update failed: " + e.getMessage());
        }
    }

    private void viewAvailableInterviewers() {
        printHeader("AVAILABLE INTERVIEWERS");
        try {
            List<Interviewer> list = candidateService.getAvailableInterviewers();
            if (list.isEmpty()) {
                System.out.println("No interviewers currently available.");
                return;
            }
            for (Interviewer i : list) {
                System.out.printf("[ID: %d] %s (%s) | Org: %s | Exp: %d yrs%n    Specialization: %s | Expertise: %s%n%n",
                        i.getInterviewerId(), i.getName(), i.getEmail(), i.getOrganization(),
                        i.getExperienceYears(), i.getSpecialization(), i.getExpertise());
            }
        } catch (SQLException e) {
            printError("Error fetching interviewers: " + e.getMessage());
        }
    }

    private void searchInterviewers() {
        printHeader("SEARCH INTERVIEWERS");
        System.out.print("Enter domain or keyword (e.g. Java, HR, Backend): ");
        String kw = scanner.nextLine().trim();
        try {
            List<Interviewer> list = candidateService.searchInterviewers(kw);
            if (list.isEmpty()) {
                System.out.println("No interviewers matching '" + kw + "'.");
                return;
            }
            for (Interviewer i : list) {
                System.out.printf("[ID: %d] %s | Spec: %s | Org: %s | Expertise: %s%n",
                        i.getInterviewerId(), i.getName(), i.getSpecialization(), i.getOrganization(), i.getExpertise());
            }
        } catch (SQLException e) {
            printError("Search error: " + e.getMessage());
        }
    }

    private void requestInterview(Candidate candidate) {
        printHeader("REQUEST AN INTERVIEW");
        try {
            viewAvailableInterviewers();
            System.out.print("Enter Interviewer ID to request: ");
            int intId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Interview Type (1. TECHNICAL | 2. HR): ");
            String typeChoice = scanner.nextLine().trim();
            String type = "2".equals(typeChoice) ? "HR" : "TECHNICAL";

            System.out.print("Preferred Date (YYYY-MM-DD, e.g. 2025-06-15): ");
            LocalDate date = InputValidator.parseDate(scanner.nextLine());

            System.out.print("Preferred Time (HH:MM, e.g. 10:30 or 15:00): ");
            LocalTime time = InputValidator.parseTime(scanner.nextLine());

            InterviewRequest req = candidateService.requestInterview(candidate.getCandidateId(), intId, type, date, time);
            printSuccess("Interview request #" + req.getRequestId() + " submitted successfully with status PENDING!");
            System.out.println("The interviewer will review your request.");

        } catch (InterviewBookingException | IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Database error: " + e.getMessage());
        }
    }

    private void viewCandidateRequests(Candidate candidate) {
        printHeader("MY INTERVIEW REQUESTS");
        try {
            List<InterviewRequest> list = candidateService.getMyRequests(candidate.getCandidateId());
            if (list.isEmpty()) {
                System.out.println("No interview requests submitted yet.");
                return;
            }
            for (InterviewRequest r : list) {
                System.out.printf("Request #%-3d | Interviewer: %-20s | Type: %-9s | Date: %s at %s | Status: %s%n",
                        r.getRequestId(), r.getInterviewerName(), r.getInterviewType(),
                        r.getPreferredDate(), r.getPreferredTime(), r.getRequestStatus());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void cancelCandidateRequest(Candidate candidate) {
        printHeader("CANCEL INTERVIEW REQUEST");
        viewCandidateRequests(candidate);
        System.out.print("Enter Request ID to cancel: ");
        try {
            int reqId = Integer.parseInt(scanner.nextLine().trim());
            candidateService.cancelInterviewRequest(reqId, candidate.getCandidateId());
            printSuccess("Interview request #" + reqId + " was cancelled successfully.");
        } catch (Exception e) {
            printError("Cancel failed: " + e.getMessage());
        }
    }

    private void viewCandidateUpcoming(Candidate candidate) {
        printHeader("UPCOMING / SCHEDULED INTERVIEWS");
        try {
            List<Interview> list = candidateService.getUpcomingInterviews(candidate.getCandidateId());
            if (list.isEmpty()) {
                System.out.println("You have no upcoming scheduled interviews.");
                return;
            }
            for (Interview inv : list) {
                System.out.printf("Interview #%-3d | Interviewer: %-20s | Type: %-9s | Scheduled: %s at %s | Status: %s%n",
                        inv.getInterviewId(), inv.getInterviewerName(), inv.getInterviewType(),
                        inv.getInterviewDate(), inv.getInterviewTime(), inv.getInterviewStatus());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void viewCandidateHistory(Candidate candidate) {
        printHeader("INTERVIEW HISTORY");
        try {
            List<Interview> list = candidateService.getInterviewHistory(candidate.getCandidateId());
            if (list.isEmpty()) {
                System.out.println("No interviews recorded yet.");
                return;
            }
            for (Interview inv : list) {
                String scoreDisplay = inv.getOverallScore() != null ? String.format("%.2f / 100", inv.getOverallScore()) : "Pending";
                System.out.printf("Interview #%-3d | Date: %s %s | Interviewer: %-18s | Type: %-9s | Status: %-9s | Score: %s%n",
                        inv.getInterviewId(), inv.getInterviewDate(), inv.getInterviewTime(),
                        inv.getInterviewerName(), inv.getInterviewType(), inv.getInterviewStatus(), scoreDisplay);
            }

            System.out.print("\nDo you want to inspect detailed evaluation feedback for an interview? (y/n): ");
            String ans = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(ans)) {
                System.out.print("Enter Interview ID: ");
                int invId = Integer.parseInt(scanner.nextLine().trim());
                InterviewFeedback fb = candidateService.getInterviewFeedback(invId, candidate.getCandidateId());
                if (fb != null) {
                    System.out.println("\n" + fb.toFormattedReport());
                } else {
                    System.out.println("No evaluation submitted yet for this interview.");
                }
            }
        } catch (Exception e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void viewCandidatePerformanceReport(Candidate candidate) {
        try {
            CandidateStatistics stats = candidateService.getPerformanceStatistics(candidate.getCandidateId());
            System.out.println(stats.toFormattedReport());
        } catch (SQLException e) {
            printError("Failed to generate performance report: " + e.getMessage());
        }
    }

    private void viewAndApplyPlacementDrives(Candidate candidate) {
        printHeader("CAMPUS PLACEMENT DRIVES & ELIGIBILITY EVALUATION");
        try {
            List<PlacementDrive> drives = candidateService.getAvailablePlacementDrives();
            if (drives.isEmpty()) {
                System.out.println("No placement drives available right now.");
                return;
            }

            System.out.printf("Your Academic Profile: CGPA %.2f | Department: %s%n%n",
                    candidate.getCgpa(), candidate.getDepartment());

            for (PlacementDrive d : drives) {
                // Evaluated dynamically via Eligible interface
                boolean eligible = d.isEligible(candidate);
                String eligibleTag = eligible ? "[ELIGIBLE - YOU CAN APPLY]" : "[NOT ELIGIBLE]";
                System.out.printf("Drive #%-2d: %-35s | Company: %-18s | Min CGPA: %4.2f | Dept: %-15s | Status: %-6s | %s%n",
                        d.getDriveId(), d.getDriveName(), d.getCompanyName(), d.getEligibilityCgpa(),
                        d.getEligibleDepartment(), d.getStatus(), eligibleTag);
            }

            System.out.print("\nEnter Drive ID to submit application (or 0 to cancel): ");
            int driveId = Integer.parseInt(scanner.nextLine().trim());
            if (driveId > 0) {
                Application app = candidateService.applyToDrive(candidate.getCandidateId(), driveId);
                printSuccess("Application #" + app.getApplicationId() + " submitted successfully! Status: " + app.getApplicationStatus());
            }

        } catch (Exception e) {
            printError("Application error: " + e.getMessage());
        }
    }

    private void viewMyApplications(Candidate candidate) {
        printHeader("MY PLACEMENT DRIVE APPLICATIONS");
        try {
            List<Application> list = candidateService.getMyApplications(candidate.getCandidateId());
            if (list.isEmpty()) {
                System.out.println("You have not applied to any placement drives yet.");
                return;
            }
            for (Application a : list) {
                System.out.printf("App #%-3d | Drive: %-35s | Applied Date: %s | Status: %s%n",
                        a.getApplicationId(), a.getDriveName(), a.getApplicationDate(), a.getApplicationStatus());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    // ==================== INTERVIEWER DASHBOARD ====================

    private void interviewerDashboard(Interviewer interviewer) {
        boolean active = true;
        while (active) {
            printHeader("INTERVIEWER DASHBOARD - " + interviewer.getName());
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. View Pending Interview Requests");
            System.out.println("4. Accept Interview Request");
            System.out.println("5. Reject Interview Request");
            System.out.println("6. View Scheduled Interviews");
            System.out.println("7. Conduct Interview & Submit Rubric Evaluation");
            System.out.println("8. View Interview Question Bank");
            System.out.println("9. View My Interview History");
            System.out.println("10. View My Interviewing Statistics");
            System.out.println("11. Logout");
            System.out.print("\nEnter your choice (1-11): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewInterviewerProfile(interviewer);
                    break;
                case "2":
                    updateInterviewerProfile(interviewer);
                    break;
                case "3":
                    viewPendingRequests(interviewer);
                    break;
                case "4":
                    acceptInterviewRequest(interviewer);
                    break;
                case "5":
                    rejectInterviewRequest(interviewer);
                    break;
                case "6":
                    viewInterviewerScheduled(interviewer);
                    break;
                case "7":
                    conductAndEvaluateInterview(interviewer);
                    break;
                case "8":
                    viewQuestionBank();
                    break;
                case "9":
                    viewInterviewerHistory(interviewer);
                    break;
                case "10":
                    viewInterviewerStats(interviewer);
                    break;
                case "11":
                    printSuccess("Logging out from Interviewer account...");
                    active = false;
                    break;
                default:
                    printError("Invalid option.");
            }
            if (active) pauseForEnter();
        }
    }

    private void viewInterviewerProfile(Interviewer interviewer) {
        try {
            Interviewer i = interviewerService.getProfile(interviewer.getInterviewerId());
            printHeader("INTERVIEWER PROFILE");
            System.out.println("Interviewer ID     : " + i.getInterviewerId());
            System.out.println("Full Name          : " + i.getName());
            System.out.println("Email Address      : " + i.getEmail());
            System.out.println("Organization       : " + i.getOrganization());
            System.out.println("Specialization     : " + i.getSpecialization());
            System.out.println("Experience         : " + i.getExperienceYears() + " years");
            System.out.println("Expertise          : " + i.getExpertise());
            System.out.println("Account Status     : " + i.getStatus());
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void updateInterviewerProfile(Interviewer interviewer) {
        printHeader("UPDATE INTERVIEWER PROFILE");
        try {
            System.out.print("New Name (" + interviewer.getName() + "): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) interviewer.setName(name);

            System.out.print("New Organization (" + interviewer.getOrganization() + "): ");
            String org = scanner.nextLine().trim();
            if (!org.isEmpty()) interviewer.setOrganization(org);

            System.out.print("New Specialization (" + interviewer.getSpecialization() + "): ");
            String spec = scanner.nextLine().trim();
            if (!spec.isEmpty()) interviewer.setSpecialization(spec);

            System.out.print("New Experience Years (" + interviewer.getExperienceYears() + "): ");
            String expStr = scanner.nextLine().trim();
            if (!expStr.isEmpty()) interviewer.setExperienceYears(Integer.parseInt(expStr));

            System.out.print("New Expertise (" + interviewer.getExpertise() + "): ");
            String exp = scanner.nextLine().trim();
            if (!exp.isEmpty()) interviewer.setExpertise(exp);

            interviewerService.updateProfile(interviewer);
            printSuccess("Profile updated successfully!");
        } catch (Exception e) {
            printError("Update failed: " + e.getMessage());
        }
    }

    private void viewPendingRequests(Interviewer interviewer) {
        printHeader("PENDING INTERVIEW REQUESTS");
        try {
            List<InterviewRequest> list = interviewerService.getPendingRequests(interviewer.getInterviewerId());
            if (list.isEmpty()) {
                System.out.println("No pending requests waiting for your review.");
                return;
            }
            for (InterviewRequest r : list) {
                System.out.printf("Request #%-3d | Candidate: %-20s (ID: %d) | Type: %-9s | Date: %s at %s%n",
                        r.getRequestId(), r.getCandidateName(), r.getCandidateId(),
                        r.getInterviewType(), r.getPreferredDate(), r.getPreferredTime());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void acceptInterviewRequest(Interviewer interviewer) {
        printHeader("ACCEPT INTERVIEW REQUEST");
        viewPendingRequests(interviewer);
        System.out.print("\nEnter Request ID to accept: ");
        try {
            int reqId = Integer.parseInt(scanner.nextLine().trim());
            Interview inv = interviewerService.acceptRequest(reqId, interviewer.getInterviewerId());
            printSuccess("Request accepted! Confirmed interview scheduled as Interview #" + inv.getInterviewId() + ".");
        } catch (Exception e) {
            printError("Acceptance failed: " + e.getMessage());
        }
    }

    private void rejectInterviewRequest(Interviewer interviewer) {
        printHeader("REJECT INTERVIEW REQUEST");
        viewPendingRequests(interviewer);
        System.out.print("\nEnter Request ID to reject: ");
        try {
            int reqId = Integer.parseInt(scanner.nextLine().trim());
            interviewerService.rejectRequest(reqId, interviewer.getInterviewerId());
            printSuccess("Request #" + reqId + " marked as REJECTED.");
        } catch (Exception e) {
            printError("Rejection failed: " + e.getMessage());
        }
    }

    private void viewInterviewerScheduled(Interviewer interviewer) {
        printHeader("SCHEDULED INTERVIEWS");
        try {
            List<Interview> list = interviewerService.getScheduledInterviews(interviewer.getInterviewerId());
            if (list.isEmpty()) {
                System.out.println("No interviews currently scheduled.");
                return;
            }
            for (Interview inv : list) {
                System.out.printf("Interview #%-3d | Candidate: %-20s (ID: %d) | Type: %-9s | Date: %s at %s | Status: %s%n",
                        inv.getInterviewId(), inv.getCandidateName(), inv.getCandidateId(),
                        inv.getInterviewType(), inv.getInterviewDate(), inv.getInterviewTime(), inv.getInterviewStatus());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void conductAndEvaluateInterview(Interviewer interviewer) {
        printHeader("CONDUCT INTERVIEW & SUBMIT EVALUATION");
        viewInterviewerScheduled(interviewer);
        try {
            System.out.print("\nEnter Interview ID to conduct and evaluate: ");
            int interviewId = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("\nWould you like to review sample questions from Question Bank? (1. Technical | 2. HR | 3. Skip): ");
            String qChoice = scanner.nextLine().trim();
            if ("1".equals(qChoice)) {
                printQuestions(interviewerService.getQuestionsByCategory("TECHNICAL"));
            } else if ("2".equals(qChoice)) {
                printQuestions(interviewerService.getQuestionsByCategory("HR"));
            }

            System.out.println("\n--- EVALUATION RUBRIC (Scores: 0.00 to 100.00) ---");
            String[] rubricCategories = {
                    "Technical Knowledge",
                    "Problem Solving",
                    "Communication Skills",
                    "Confidence",
                    "Logical Thinking",
                    "Subject Knowledge"
            };

            List<SkillScore> skillScores = new ArrayList<>();
            for (String cat : rubricCategories) {
                System.out.print("Score for " + cat + " (0-100): ");
                double sc = Double.parseDouble(scanner.nextLine().trim());
                skillScores.add(new SkillScore(cat, sc));
            }

            System.out.print("\nEnter Overall Interview Score (0-100): ");
            double overallScore = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Written Qualitative Feedback Remarks: ");
            String feedback = scanner.nextLine().trim();

            boolean ok = interviewerService.submitEvaluation(interviewId, interviewer.getInterviewerId(), feedback, overallScore, skillScores);
            if (ok) {
                printSuccess("Evaluation successfully saved in database! Interview #" + interviewId + " marked as COMPLETED.");
            }
        } catch (Exception e) {
            printError("Evaluation failed: " + e.getMessage());
        }
    }

    private void printQuestions(List<Question> questions) {
        System.out.println("\nAvailable Questions:");
        for (Question q : questions) {
            System.out.printf("  - [#%d | %s | %s] %s%n", q.getQuestionId(), q.getDifficulty(), q.getSpecialization(), q.getQuestionText());
        }
    }

    private void viewQuestionBank() {
        printHeader("QUESTION BANK");
        try {
            List<Question> list = interviewerService.getAllQuestions();
            for (Question q : list) {
                System.out.printf("[#%-2d | %-9s | %-6s | %-20s] %s%n",
                        q.getQuestionId(), q.getCategory(), q.getDifficulty(), q.getSpecialization(), q.getQuestionText());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void viewInterviewerHistory(Interviewer interviewer) {
        printHeader("MY INTERVIEW CONDUCTING HISTORY");
        try {
            List<Interview> list = interviewerService.getInterviewHistory(interviewer.getInterviewerId());
            if (list.isEmpty()) {
                System.out.println("No interview records found.");
                return;
            }
            for (Interview inv : list) {
                String scoreDisplay = inv.getOverallScore() != null ? String.format("%.2f / 100", inv.getOverallScore()) : "N/A";
                System.out.printf("Interview #%-3d | Candidate: %-20s | Date: %s %s | Status: %-9s | Score Awarded: %s%n",
                        inv.getInterviewId(), inv.getCandidateName(), inv.getInterviewDate(),
                        inv.getInterviewTime(), inv.getInterviewStatus(), scoreDisplay);
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void viewInterviewerStats(Interviewer interviewer) {
        try {
            InterviewerStatistics stats = interviewerService.getStatistics(interviewer.getInterviewerId());
            System.out.println(stats.toFormattedReport());
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    // ==================== ADMIN DASHBOARD ====================

    private void adminDashboard(Admin admin) {
        boolean active = true;
        while (active) {
            printHeader("ADMINISTRATOR CONTROL PANEL");
            System.out.println("1. View All Registered Users & Roles");
            System.out.println("2. Activate / Deactivate User Account");
            System.out.println("3. View All Candidates");
            System.out.println("4. View All Interviewers");
            System.out.println("5. Add New Interviewer Account");
            System.out.println("6. Manage Interview Question Bank");
            System.out.println("7. View All Interviews & Requests");
            System.out.println("8. Manage Placement Drives & Companies");
            System.out.println("9. View Student Placement Applications");
            System.out.println("10. View Global System Analytics");
            System.out.println("11. Logout");
            System.out.print("\nEnter your choice (1-11): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    toggleUserStatus();
                    break;
                case "3":
                    viewAllCandidates();
                    break;
                case "4":
                    viewAllInterviewersAdmin();
                    break;
                case "5":
                    addNewInterviewer();
                    break;
                case "6":
                    manageQuestionBank(admin);
                    break;
                case "7":
                    viewAllInterviewsAdmin();
                    break;
                case "8":
                    managePlacementDrives();
                    break;
                case "9":
                    viewAllApplicationsAdmin();
                    break;
                case "10":
                    viewSystemStatistics();
                    break;
                case "11":
                    printSuccess("Logging out from Admin panel...");
                    active = false;
                    break;
                default:
                    printError("Invalid option.");
            }
            if (active) pauseForEnter();
        }
    }

    private void viewAllUsers() {
        printHeader("ALL REGISTERED USERS");
        try {
            List<User> list = adminService.getAllUsers();
            for (User u : list) {
                System.out.printf("User #%-3d | Name: %-22s | Email: %-28s | Role: %-11s | Status: %s%n",
                        u.getUserId(), u.getName(), u.getEmail(), u.getRole(), u.getStatus());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void toggleUserStatus() {
        printHeader("ACTIVATE / DEACTIVATE USER");
        viewAllUsers();
        System.out.print("\nEnter User ID to update: ");
        try {
            int uid = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter New Status (1. ACTIVE | 2. INACTIVE): ");
            String stChoice = scanner.nextLine().trim();
            String status = "2".equals(stChoice) ? "INACTIVE" : "ACTIVE";

            adminService.toggleUserStatus(uid, status);
            printSuccess("User #" + uid + " status updated to " + status + ".");
        } catch (Exception e) {
            printError("Operation failed: " + e.getMessage());
        }
    }

    private void viewAllCandidates() {
        printHeader("ALL CANDIDATES");
        try {
            List<Candidate> list = adminService.getAllCandidates();
            for (Candidate c : list) {
                System.out.printf("Candidate #%-3d | %-20s | Student ID: %-10s | Dept: %-18s | CGPA: %4.2f | Spec: %s%n",
                        c.getCandidateId(), c.getName(), c.getStudentId(), c.getDepartment(), c.getCgpa(), c.getSpecialization());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void viewAllInterviewersAdmin() {
        printHeader("ALL INTERVIEWERS");
        try {
            List<Interviewer> list = adminService.getAllInterviewers();
            for (Interviewer i : list) {
                System.out.printf("Interviewer #%-3d | %-20s | Org: %-20s | Spec: %-18s | Exp: %d yrs%n",
                        i.getInterviewerId(), i.getName(), i.getOrganization(), i.getSpecialization(), i.getExperienceYears());
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void addNewInterviewer() {
        printHeader("ADD NEW INTERVIEWER ACCOUNT");
        try {
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Email Address: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();
            System.out.print("Organization / Company: ");
            String org = scanner.nextLine();
            System.out.print("Specialization (e.g. Software Engineering): ");
            String spec = scanner.nextLine();
            System.out.print("Years of Experience: ");
            int exp = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Expertise Description: ");
            String expertise = scanner.nextLine();

            Interviewer inv = adminService.addInterviewer(name, email, pass, expertise, exp, spec, org);
            printSuccess("Interviewer account created successfully! Interviewer ID: " + inv.getInterviewerId());
        } catch (Exception e) {
            printError("Failed to add interviewer: " + e.getMessage());
        }
    }

    private void manageQuestionBank(Admin admin) {
        printHeader("MANAGE QUESTION BANK");
        System.out.println("1. View All Questions");
        System.out.println("2. Add New Question");
        System.out.println("3. Delete Question");
        System.out.print("Enter choice (1-3): ");
        String ch = scanner.nextLine().trim();

        try {
            if ("1".equals(ch)) {
                List<Question> list = adminService.getAllQuestions();
                for (Question q : list) {
                    System.out.printf("[#%-2d | %-9s | %-6s | %-15s] %s%n",
                            q.getQuestionId(), q.getCategory(), q.getDifficulty(), q.getSpecialization(), q.getQuestionText());
                }
            } else if ("2".equals(ch)) {
                System.out.print("Category (1. TECHNICAL | 2. HR): ");
                String catChoice = scanner.nextLine().trim();
                String category = "2".equals(catChoice) ? "HR" : "TECHNICAL";

                System.out.print("Difficulty (EASY | MEDIUM | HARD): ");
                String diff = scanner.nextLine().trim();

                System.out.print("Specialization / Topic: ");
                String spec = scanner.nextLine().trim();

                System.out.print("Question Text: ");
                String text = scanner.nextLine().trim();

                Question q = adminService.addQuestion(text, category, diff, spec, admin.getUserId());
                printSuccess("Question #" + q.getQuestionId() + " added successfully!");
            } else if ("3".equals(ch)) {
                System.out.print("Enter Question ID to delete: ");
                int qid = Integer.parseInt(scanner.nextLine().trim());
                adminService.deleteQuestion(qid);
                printSuccess("Question #" + qid + " deleted.");
            }
        } catch (Exception e) {
            printError("Operation failed: " + e.getMessage());
        }
    }

    private void viewAllInterviewsAdmin() {
        printHeader("SYSTEM INTERVIEWS & REQUESTS");
        try {
            List<Interview> list = adminService.getAllInterviews();
            System.out.println("Total Interviews Recorded: " + list.size());
            for (Interview inv : list) {
                String scoreDisplay = inv.getOverallScore() != null ? String.format("%.2f", inv.getOverallScore()) : "Pending";
                System.out.printf("Interview #%-3d | Date: %s %s | Candidate: %-18s | Interviewer: %-18s | Type: %-9s | Status: %-9s | Score: %s%n",
                        inv.getInterviewId(), inv.getInterviewDate(), inv.getInterviewTime(),
                        inv.getCandidateName(), inv.getInterviewerName(), inv.getInterviewType(),
                        inv.getInterviewStatus(), scoreDisplay);
            }
        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void managePlacementDrives() {
        printHeader("MANAGE PLACEMENT DRIVES & COMPANIES");
        System.out.println("1. View All Companies");
        System.out.println("2. Add New Company");
        System.out.println("3. View All Placement Drives");
        System.out.println("4. Create New Placement Drive");
        System.out.print("Enter choice (1-4): ");
        String ch = scanner.nextLine().trim();

        try {
            if ("1".equals(ch)) {
                List<Company> list = adminService.getAllCompanies();
                for (Company c : list) {
                    System.out.printf("Company #%-2d | %-25s | Industry: %-20s | Location: %s%n",
                            c.getCompanyId(), c.getCompanyName(), c.getIndustry(), c.getLocation());
                }
            } else if ("2".equals(ch)) {
                System.out.print("Company Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Industry: ");
                String ind = scanner.nextLine().trim();
                System.out.print("Location: ");
                String loc = scanner.nextLine().trim();
                Company c = adminService.addCompany(name, ind, loc);
                printSuccess("Company '" + c.getCompanyName() + "' registered (ID: " + c.getCompanyId() + ").");
            } else if ("3".equals(ch)) {
                List<PlacementDrive> list = adminService.getAllPlacementDrives();
                for (PlacementDrive d : list) {
                    System.out.printf("Drive #%-2d | %-35s | Company: %-20s | Min CGPA: %4.2f | Dept: %-15s | Status: %s%n",
                            d.getDriveId(), d.getDriveName(), d.getCompanyName(),
                            d.getEligibilityCgpa(), d.getEligibleDepartment(), d.getStatus());
                }
            } else if ("4".equals(ch)) {
                System.out.print("Company ID: ");
                int cid = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Drive Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Minimum Eligibility CGPA (0.00-10.00): ");
                double cgpa = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Eligible Department (e.g. Computer Science or 'All Engineering'): ");
                String dept = scanner.nextLine().trim();
                System.out.print("Application Deadline (YYYY-MM-DD): ");
                LocalDate dl = InputValidator.parseDate(scanner.nextLine().trim());

                PlacementDrive drive = adminService.createPlacementDrive(cid, name, cgpa, dept, dl);
                printSuccess("Placement Drive #" + drive.getDriveId() + " created successfully!");
            }
        } catch (Exception e) {
            printError("Operation failed: " + e.getMessage());
        }
    }

    private void viewAllApplicationsAdmin() {
        printHeader("STUDENT PLACEMENT APPLICATIONS");
        try {
            List<Application> list = adminService.getAllApplications();
            if (list.isEmpty()) {
                System.out.println("No student placement applications recorded.");
                return;
            }
            for (Application a : list) {
                System.out.printf("App #%-3d | Candidate: %-20s | Drive: %-35s | Date: %s | Status: %s%n",
                        a.getApplicationId(), a.getCandidateName(), a.getDriveName(), a.getApplicationDate(), a.getApplicationStatus());
            }

            System.out.print("\nDo you want to update an application status? (y/n): ");
            String ans = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(ans)) {
                System.out.print("Enter Application ID: ");
                int aid = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("New Status (SHORTLISTED | SELECTED | REJECTED): ");
                String st = scanner.nextLine().trim();
                adminService.updateApplicationStatus(aid, st);
                printSuccess("Application #" + aid + " status updated to " + st + ".");
            }
        } catch (Exception e) {
            printError("Error: " + e.getMessage());
        }
    }

    private void viewSystemStatistics() {
        try {
            SystemStatistics stats = adminService.getSystemStatistics();
            System.out.println(stats.toFormattedReport());
        } catch (SQLException e) {
            printError("Error generating system analytics: " + e.getMessage());
        }
    }

    // ==================== MILESTONE 2: COLLECTIONS DEMO ====================

    private void runCollectionsDemo() {
        printHeader("MILESTONE 2: JAVA COLLECTIONS IN-MEMORY ANALYTICS DEMO");
        try {
            List<Candidate> candidates = candidateService.getProfile(1) != null ?
                    adminService.getAllCandidates() : new ArrayList<>();
            List<Interviewer> interviewers = adminService.getAllInterviewers();
            List<Interview> interviews = adminService.getAllInterviews();

            collectionManager.syncData(candidates, interviewers, interviews);

            System.out.println("[COLLECTIONS SYNCED] Loaded from MySQL into in-memory Collections:");
            System.out.println("  - ArrayList<Candidate>    : " + collectionManager.getCandidateList().size() + " items");
            System.out.println("  - ArrayList<Interviewer>  : " + collectionManager.getInterviewerList().size() + " items");
            System.out.println("  - ArrayList<Interview>    : " + collectionManager.getInterviewList().size() + " items");

            System.out.println("\n1. Comparator Sorting: Candidates by CGPA (Descending):");
            List<Candidate> byCgpa = collectionManager.sortCandidatesByCgpaDescending();
            for (Candidate c : byCgpa) {
                System.out.printf("   * CGPA %4.2f | %-20s | %s%n", c.getCgpa(), c.getName(), c.getDepartment());
            }

            System.out.println("\n2. Comparator Sorting: Candidates by Name (Alphabetical):");
            List<Candidate> byName = collectionManager.sortCandidatesByName();
            for (Candidate c : byName) {
                System.out.printf("   * %-20s | Student ID: %s | CGPA: %4.2f%n", c.getName(), c.getStudentId(), c.getCgpa());
            }

            System.out.println("\n3. Filtering by Department ('Computer Science') using Enhanced For-Loop:");
            List<Candidate> csCandidates = collectionManager.filterCandidatesByDepartment("Computer Science");
            for (Candidate c : csCandidates) {
                System.out.printf("   * %-20s | Spec: %s%n", c.getName(), c.getSpecialization());
            }

            System.out.println("\n4. Top Candidates by Average Interview Score (Collection Analytics):");
            List<Map.Entry<Candidate, Double>> top = collectionManager.getTopFiveCandidatesByScore();
            if (top.isEmpty()) {
                System.out.println("   No completed interview scores found in collection.");
            } else {
                int rank = 1;
                for (Map.Entry<Candidate, Double> entry : top) {
                    System.out.printf("   Rank #%d: %-20s | Average Score: %5.2f / 100.00 | CGPA: %4.2f%n",
                            rank++, entry.getKey().getName(), entry.getValue(), entry.getKey().getCgpa());
                }
            }

            System.out.println("\n5. Fast O(1) HashMap Lookup Demonstration:");
            if (!candidates.isEmpty()) {
                int testId = candidates.get(0).getCandidateId();
                Candidate cached = collectionManager.findCandidateById(testId);
                System.out.println("   Looked up Candidate ID " + testId + " from HashMap -> " + cached);
            }

            System.out.println("\n6. HashSet Duplicate Prevention Demonstration:");
            String testEmail = "rahul.sharma@college.edu";
            System.out.println("   Checking if '" + testEmail + "' exists in HashSet: " +
                    collectionManager.isEmailRegistered(testEmail));

        } catch (SQLException e) {
            printError("Collections demo database fetch failed: " + e.getMessage());
        }
    }

    private void testDb() {
        printHeader("DATABASE CONNECTIVITY TEST");
        System.out.println("Testing MySQL connection to: " + DBConnection.getDbUrl());
        System.out.println("User: " + DBConnection.getDbUser());
        boolean ok = DBConnection.testConnection();
        if (ok) {
            printSuccess("Connection established successfully! MySQL database is reachable.");
        } else {
            printError("Connection FAILED. Check src/main/resources/database.properties credentials or MySQL service status.");
        }
    }

    // ==================== UI HELPERS ====================

    private void printHeader(String title) {
        System.out.println("\n==============================================================");
        System.out.println("  " + title);
        System.out.println("==============================================================");
    }

    private void printSuccess(String msg) {
        System.out.println("[SUCCESS] " + msg);
    }

    private void printError(String msg) {
        System.out.println("[ERROR] " + msg);
    }

    private void pauseForEnter() {
        System.out.print("\nPress [ENTER] to continue...");
        scanner.nextLine();
    }
}
