package ta.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TAPositionService {

    private static final String JOB_FILE = "data/jobs.txt";

    public void browsePositions() {
        List<String[]> positions = readAllPositions();

        if (positions.isEmpty()) {
            System.out.println("No available positions at the moment.");
            System.out.println("Note: jobs.txt has not been generated yet by the MO module.");
            return;
        }

        System.out.println("----- Available Positions -----");
        for (int i = 0; i < positions.size(); i++) {
            String[] row = positions.get(i);

            String courseName = getValue(row, 0);
            String vacancies = getValue(row, 1);
            String duties = getValue(row, 2);
            String deadline = getValue(row, 3);
            String priorities = getValue(row, 4);

            System.out.println("Position #" + (i + 1));
            System.out.println("Course Name: " + courseName);
            System.out.println("Vacancies: " + vacancies);
            System.out.println("Duties: " + duties);
            System.out.println("Deadline: " + deadline);
            System.out.println("Priority Requirements: " + priorities);
            System.out.println("------------------------------");
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

    private String getValue(String[] row, int index) {
        if (row == null || index >= row.length) {
            return "";
        }
        return row[index];
    }
}