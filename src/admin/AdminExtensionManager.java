package admin;

import common.Constants;
import common.CsvUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 修复版 - 解决了 CsvUtil 类型不匹配的问题
 */
public class AdminExtensionManager {

    /**
     * 辅助方法：将 CsvUtil 读取的原始字符串列表转换为数组列表
     */
    private List<String[]> loadData() {
        List<String> rawLines = CsvUtil.readCsv(Constants.COURSES_CSV);
        List<String[]> data = new ArrayList<>();
        for (String line : rawLines) {
            data.add(line.split(","));
        }
        return data;
    }

    /**
     * 功能 1.1：手动添加/编辑课程
     */
    public boolean saveCourse(Course course) {
        List<String[]> allCourses = loadData(); // 使用辅助方法转换类型
        boolean exists = false;

        for (int i = 0; i < allCourses.size(); i++) {
            if (allCourses.get(i).length > 0 && allCourses.get(i)[0].equals(course.getCourseCode())) {
                allCourses.set(i, course.toCsvRow());
                exists = true;
                break;
            }
        }

        if (!exists) {
            List<String> toAppend = new ArrayList<>();
            toAppend.add(String.join(",", course.toCsvRow()));
            CsvUtil.writeCsv(Constants.COURSES_CSV, toAppend, true);
        } else {
            List<String> toWrite = new ArrayList<>();
            for (String[] row : allCourses) {
                toWrite.add(String.join(",", row));
            }
            CsvUtil.writeCsv(Constants.COURSES_CSV, toWrite, false);
        }
        return true;
    }

    /**
     * 功能 1.2：批量导入课程
     */
    public void importCoursesFromCsv(String externalPath) {
        List<String> rawLines = CsvUtil.readCsv(externalPath);
        if (rawLines == null) return;

        int count = 0;
        for (int i = 1; i < rawLines.size(); i++) { // 跳过表头
            String[] row = rawLines.get(i).split(",");
            if (row.length >= 5) {
                try {
                    Course c = new Course(row[0], row[1], row[2],
                            Integer.parseInt(row[3]), Integer.parseInt(row[4]));
                    saveCourse(c);
                    count++;
                } catch (Exception e) {
                    System.out.println("Line " + i + " error, skipped.");
                }
            }
        }
        System.out.println("Imported " + count + " courses.");
    }

    /**
     * 功能 2：招聘进度监控
     */
    public void monitorAndExport(String keyword) {
        List<String[]> allCourses = loadData();
        List<String> report = new ArrayList<>();
        report.add("Code,Name,MO_ID,Target,Current,Gap");

        for (String[] row : allCourses) {
            if (row.length >= 5 && (row[1].contains(keyword) || row[2].equals(keyword))) {
                int target = Integer.parseInt(row[3]);
                int current = Integer.parseInt(row[4]);
                int gap = target - current;
                report.add(row[0] + "," + row[1] + "," + row[2] + "," + row[3] + "," + row[4] + "," + gap);
            }
        }
        CsvUtil.writeCsv("data/recruitment_progress.csv", report, false);
        System.out.println("Progress report exported.");
    }
}