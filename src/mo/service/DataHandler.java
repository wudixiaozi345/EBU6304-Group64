package mo.service;

import java.io.*;
import java.util.*;

public class DataHandler {
    private static final String JOB_FILE = "jobs.txt";
    private static final String APP_FILE = "applications.txt";

    // 保存岗位到文件
    public static void saveJob(String jobData) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(JOB_FILE, true)))) {
            out.println(jobData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从文件读取所有申请
    public static List<String[]> readApplications() {
        List<String[]> apps = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(APP_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                apps.add(line.split(","));
            }
        } catch (IOException e) {
            System.out.println("No applications found yet.");
        }
        return apps;
    }

    // 更新申请状态（筛选简历后写回文件）
    public static void updateApplications(List<String[]> allApps) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(APP_FILE, false)))) {
            for (String[] app : allApps) {
                out.println(String.join(",", app));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}