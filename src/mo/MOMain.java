package mo;

import mo.service.DataHandler;
import mo.service.MOExtensionService;
import java.util.*;

public class MOMain {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- MO Management (Member 4) ---");
            System.out.println("1. Post Job");
            System.out.println("2. Review/Screen Apps");
            System.out.println("3. [Task 4.1] Send Bulk Notices");
            System.out.println("4. [Task 4.2] Export Hired List");
            System.out.println("5. Exit");
            System.out.print("Select: ");

            String choice = scanner.nextLine();
            if (!MOExtensionService.validate(choice)) continue;

            if (choice.equals("3")) {
                System.out.print("Time: "); String t = scanner.nextLine();
                System.out.print("Template (use {Name}): "); String tmp = scanner.nextLine();
                MOExtensionService.sendBulkNotices(DataHandler.readApplications(), tmp, t);
            } else if (choice.equals("4")) {
                MOExtensionService.exportHiringList(DataHandler.readApplications());
            } else if (choice.equals("5")) break;
            else if (choice.equals("2")) review();
            // 选1和选2的其他代码可根据需要补充
        }
    }

    private static void review() {
        List<String[]> apps = DataHandler.readApplications();
        for(int i=0; i<apps.size(); i++) System.out.println(i + ". " + apps.get(i)[0]);
        System.out.print("Select index to Hire: ");
        int id = Integer.parseInt(scanner.nextLine());
        apps.get(id)[4] = "Hired";
        DataHandler.updateApplications(apps);
        System.out.println("Status updated!");
    }
}