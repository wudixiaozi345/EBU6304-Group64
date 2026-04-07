package ta.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TAInterviewService {

    private static final String APPLICATION_FILE = "applications.txt";
    private final Scanner scanner = new Scanner(System.in);

    public void viewInterviewNotice(String email) {
        File file = new File(APPLICATION_FILE);
        if (!file.exists()) {
            System.out.println("No interview notice found.");
            return;
        }

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("----- Interview Notice -----");

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] row = normalizeApplicationRow(line.split(","));

                if (email.equals(getValue(row, 0)) && !getValue(row, 6).isEmpty()) {
                    found = true;
                    System.out.println("Applicant: " + getValue(row, 0));
                    System.out.println("Status: " + getValue(row, 4));
                    System.out.println("Interview Time: " + getValue(row, 6));
                    System.out.println("TA Confirmation: " + getValue(row, 7));
                    System.out.println("------------------------------");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to read interview notice: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("No interview notice available for you now.");
        }
    }

    public void confirmOrRescheduleInterview(String email) {
        File file = new File(APPLICATION_FILE);
        if (!file.exists()) {
            System.out.println("No interview data found.");
            return;
        }

        List<String> updatedLines = new ArrayList<>();
        List<Integer> candidateIndexes = new ArrayList<>();
        List<String[]> candidateRows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int rowIndex = 0;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    updatedLines.add(line);
                    rowIndex++;
                    continue;
                }

                String[] row = normalizeApplicationRow(line.split(","));

                if (email.equals(getValue(row, 0)) && !getValue(row, 6).isEmpty()) {
                    candidateIndexes.add(rowIndex);
                    candidateRows.add(row);
                }

                updatedLines.add(joinRow(row));
                rowIndex++;
            }
        } catch (Exception e) {
            System.out.println("Failed to load interview data: " + e.getMessage());
            return;
        }

        if (candidateRows.isEmpty()) {
            System.out.println("You do not have any interview to confirm or reschedule.");
            return;
        }

        System.out.println("----- Interview List -----");
        for (int i = 0; i < candidateRows.size(); i++) {
            String[] row = candidateRows.get(i);
            System.out.println((i + 1) + ". Interview Time: " + getValue(row, 6)
                    + " | Current Confirmation: " + getValue(row, 7));
        }

        System.out.print("Select interview number: ");
        int index = readInt();

        if (index < 1 || index > candidateRows.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String[] targetRow = candidateRows.get(index - 1);

        System.out.println("1. Confirm Interview");
        System.out.println("2. Request Reschedule");
        System.out.print("Select: ");
        int action = readInt();

        if (action == 1) {
            targetRow[7] = "Confirmed";
        } else if (action == 2) {
            targetRow[7] = "Reschedule Requested";
        } else {
            System.out.println("Invalid action.");
            return;
        }

        int targetFileIndex = candidateIndexes.get(index - 1);
        updatedLines.set(targetFileIndex, joinRow(targetRow));

        boolean ok = writeAllLines(APPLICATION_FILE, updatedLines);
        if (ok) {
            System.out.println("Interview response updated successfully.");
        } else {
            System.out.println("Failed to update interview response.");
        }
    }

    private boolean writeAllLines(String fileName, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Failed to write file: " + e.getMessage());
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

    private String joinRow(String[] row) {
        return safe(getValue(row, 0)) + "," +
                safe(getValue(row, 1)) + "," +
                safe(getValue(row, 2)) + "," +
                safe(getValue(row, 3)) + "," +
                safe(getValue(row, 4)) + "," +
                safe(getValue(row, 5)) + "," +
                safe(getValue(row, 6)) + "," +
                safe(getValue(row, 7));
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