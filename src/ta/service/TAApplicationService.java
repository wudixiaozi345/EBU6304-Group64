package ta.service;

import ta.model.TAProfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TAApplicationService {

    private static final String JOB_FILE = "data/jobs.txt";
    private static final String APPLICATION_FILE = "applications.txt";

    private final Scanner scanner = new Scanner(System.in);
    private final TAProfileService profileService = new TAProfileService();

    public void applyForPosition(String email) {
        TAProfile profile = profileService.getProfileByEmail(email);
        if (profile == null) {
            System.out.println("Please create your profile before applying for a position.");
            return;
        }

        List<String[]> positions = readAllPositions();
        if (positions.isEmpty()) {
            System.out.println("No available positions at the moment.");
            System.out.println("Note: jobs.txt has not been generated yet by the MO module.");
            return;
        }

        System.out.println("----- Available Positions -----");
        for (int i = 0; i < positions.size(); i++) {
            String[] row = positions.get(i);
            System.out.println((i + 1) + ". " + getValue(row, 0) + " | Vacancies: " + getValue(row, 1));
        }

        System.out.print("Select a position number to apply: ");
        int index = readInt();

        if (index < 1 || index > positions.size()) {
            System.out.println("Invalid position number.");
            return;
        }

        String[] selected = positions.get(index - 1);
        String selectedCourse = getValue(selected, 0);

        if (hasAlreadyApplied(email, selectedCourse)) {
            System.out.println("You have already applied for this position.");
            return;
        }

        System.out.print("Enter your available time slot (e.g. Monday 10:00-12:00): ");
        String timeSlot = scanner.nextLine();

        String applicationLine = buildApplicationLine(
                email,
                profile.getGpa(),
                profile.getMajor(),
                profile.getResumePath(),
                "Pending",
                "Preferred slot: " + timeSlot,
                "",
                ""
        );

        boolean ok = appendLine(APPLICATION_FILE, applicationLine);

        if (ok) {
            System.out.println("Application submitted successfully.");
            System.out.println("Applied Course: " + selectedCourse);
            System.out.println("Current Status: Pending");
        } else {
            System.out.println("Failed to submit application.");
        }
    }

    public void viewMyApplicationStatus(String email) {
        File file = new File(APPLICATION_FILE);
        if (!file.exists()) {
            System.out.println("No application records found.");
            return;
        }

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("----- My Application Status -----");

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] row = normalizeApplicationRow(line.split(","));

                if (email.equals(getValue(row, 0))) {
                    found = true;

                    System.out.println("Applicant: " + getValue(row, 0));
                    System.out.println("GPA: " + getValue(row, 1));
                    System.out.println("Major: " + getValue(row, 2));
                    System.out.println("Resume Path: " + getValue(row, 3));
                    System.out.println("Status: " + getValue(row, 4));
                    System.out.println("Reason / Note: " + getValue(row, 5));
                    System.out.println("Interview Time: " + getValue(row, 6));
                    System.out.println("TA Confirmation: " + getValue(row, 7));
                    System.out.println("------------------------------");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to read application status: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("You have not submitted any applications yet.");
        }
    }

    private List<String[]> readAllPositions() {
        List<String[]> positions = new ArrayList<>();
        File file = new File(JOB_FILE);

        if (!file.exists()) {
            return positions;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    positions.add(line.split(","));
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to read positions: " + e.getMessage());
        }

        return positions;
    }

    private boolean hasAlreadyApplied(String email, String courseName) {
        File file = new File(APPLICATION_FILE);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] row = normalizeApplicationRow(line.split(","));

                String applicant = getValue(row, 0);
                String note = getValue(row, 5);

                if (email.equals(applicant) && note.contains(courseName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to check duplicate application: " + e.getMessage());
        }

        return false;
    }

    private String buildApplicationLine(String applicant, double gpa, String major,
                                        String resumePath, String status, String reason,
                                        String interviewTime, String confirmation) {
        return applicant + "," +
                gpa + "," +
                safe(major) + "," +
                safe(resumePath) + "," +
                safe(status) + "," +
                safe(reason) + "," +
                safe(interviewTime) + "," +
                safe(confirmation);
    }

    private boolean appendLine(String fileName, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(line);
            bw.newLine();
            return true;
        } catch (Exception e) {
            System.out.println("File write failed: " + e.getMessage());
            return false;
        }
    }

    private String[] normalizeApplicationRow(String[] row) {
        String[] fixed = new String[8];
        for (int i = 0; i < fixed.length; i++) {
            if (row != null && i < row.length) {
                fixed[i] = row[i];
            } else {
                fixed[i] = "";
            }
        }
        return fixed;
    }

    private String getValue(String[] row, int index) {
        if (row == null || index >= row.length) {
            return "";
        }
        return row[index];
    }

    private String safe(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(",", " ");
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.print("Invalid number, please input again: ");
            }
        }
    }
}
