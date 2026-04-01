package mo.service;

import java.io.*;
import java.util.*;

public class MOExtensionService {
    // 任务4.1: 批量通知
    public static void sendBulkNotices(List<String[]> apps, String template, String time) {
        System.out.println("\n--- [Simulating Emails] ---");
        for (int i = 0; i < apps.size(); i++) {
            String[] app = apps.get(i);

            // 【核心修复】：如果数组长度不够8，手动扩充它，防止崩溃
            if (app.length < 8) {
                app = Arrays.copyOf(app, 8);
                Arrays.fill(app, 5, 8, "None"); // 填充空白位
            }

            if (app[4].equals("Pending") || app[4].equals("Invited")) {
                app[4] = "Invited";
                app[6] = time; // 现在索引6肯定存在了
                String content = template.replace("{Name}", app[0]).replace("{Time}", time);
                System.out.println("To: " + app[0] + " | Msg: " + content);
            }
            apps.set(i, app); // 写回列表
        }
        DataHandler.updateApplications(apps);
    }

    // 任务4.2: 导出录用名单
    public static void exportHiringList(List<String[]> apps) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/Hired_List.csv"))) {
            pw.println("Name,GPA,Major,Status");
            int count = 0;
            for (String[] app : apps) {
                if (app.length > 4 && app[4].equalsIgnoreCase("Hired")) {
                    pw.println(app[0] + "," + app[1] + "," + app[2] + "," + app[4]);
                    count++;
                }
            }
            System.out.println("\n[Success] Exported " + count + " records to data/Hired_List.csv");
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    // 任务4.3: 基础错误处理
    public static boolean validate(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println(">> [Error] Input cannot be empty!");
            return false;
        }
        return true;
    }
}