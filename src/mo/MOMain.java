package mo;

import mo.model.*;
import mo.service.DataHandler;

import java.io.File;
import java.util.*;

public class MOMain {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- MO Management System ---");
            System.out.println("1. Post a New Job");
            System.out.println("2. Review Applications (Resume Screening)");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1)
                postJob();
            else if (choice == 2)
                reviewApps();
            else
                break;
        }
    }

    private static void postJob() {
        System.out.print("Course Name: ");
        String name = scanner.nextLine();
        System.out.print("Vacancies: ");
        int num = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Duties: ");
        String duties = scanner.nextLine();
        System.out.print("Deadline: ");
        String date = scanner.nextLine();
        System.out.print("Priority Requirements: ");
        String prio = scanner.nextLine();

        Job newJob = new Job(name, num, duties, date, prio);
        // 预览
        System.out.println("\nPreview: " + newJob.toString());
        System.out.print("Confirm to submit? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            DataHandler.saveJob(newJob.toString());
            System.out.println("Job posted successfully!");
        }
    }

    private static void reviewApps() {
        List<String[]> apps = DataHandler.readApplications();
        if (apps.isEmpty())
            return;

        // 排序逻辑 (按 GPA 降序)
        apps.sort((a, b) -> Double.compare(Double.parseDouble(b[1]), Double.parseDouble(a[1])));

        System.out.println("\n--- Applications (Sorted by GPA) ---");
        for (int i = 0; i < apps.size(); i++) {
            String[] a = apps.get(i);
            System.out.println(i + ". Name: " + a[0] + " | GPA: " + a[1] + " | Major: " + a[2] + " | Status: " + a[4]);
        }

        System.out.print("\nEnter the index to screen (or -1 to go back): ");
        int idx = scanner.nextInt();
        scanner.nextLine();
        if (idx >= 0 && idx < apps.size()) {
            // 在 idx = scanner.nextInt(); 之后
            if (idx >= 0 && idx < apps.size()) {
                String pdfFileName = apps.get(idx)[3]; // 获取简历路径
                System.out.println("Opening resume: " + pdfFileName + "...");

                // 模拟打开 PDF 的代码
                try {
                    File file = new File(pdfFileName);
                    if (file.exists()) {
                        java.awt.Desktop.getDesktop().open(file); // 这行代码会调用系统默认软件打开PDF
                    } else {
                        System.out.println("[Warning] PDF file not found at " + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.out.println("Could not open file: " + e.getMessage());
                }

                // 然后再进行后续的 Pass/Reject 操作...
            }
            System.out.print("Pass or Reject? (p/r): ");
            String res = scanner.nextLine().equalsIgnoreCase("p") ? "Accepted" : "Rejected";
            System.out.print("Reason: ");
            String reason = scanner.nextLine();

            apps.get(idx)[4] = res; // 更新状态
            apps.get(idx)[5] = reason; // 更新理由
            DataHandler.updateApplications(apps);
            System.out.println("Screening updated!");
        }
    }
}