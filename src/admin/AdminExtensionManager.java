package admin;

import common.Constants;
import common.CsvUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 对应任务：
 * 1. 课程库管理（导入、查重、编辑）
 * 2. 招聘进度监控（筛选、统计、导出）
 * [cite: 25, 26, 33, 98]
 */
public class AdminExtensionManager {

    /**
     * 功能 1.1：手动添加/编辑课程（含重复提示）
     * [cite: 25, 92]
     */
    public boolean saveCourse(Course course) {
        List<String[]> allCourses = CsvUtil.readCsv(Constants.COURSES_CSV);
        boolean exists = false;

        if (allCourses == null) allCourses = new ArrayList<>();

        // 检查是否存在（手动编辑逻辑：若存在则更新，若不存在则添加）
        for (int i = 0; i < allCourses.size(); i++) {
            if (allCourses.get(i).length > 0 && allCourses.get(i)[0].equals(course.getCourseCode())) {
                allCourses.set(i, course.toCsvRow()); // 覆盖旧信息（即编辑）
                exists = true;
                System.out.println("提示：课程 " + course.getCourseCode() + " 已更新。 [cite: 92]");
                break;
            }
        }

        if (!exists) {
            List<String[]> toAppend = new ArrayList<>();
            toAppend.add(course.toCsvRow());
            CsvUtil.writeCsv(Constants.COURSES_CSV, toAppend, true); // 追加新课程
            System.out.println("提示：新课程添加成功。");
        } else {
            // 如果是更新，需要重写整个文件（false）
            CsvUtil.writeCsv(Constants.COURSES_CSV, allCourses, false);
        }
        return true;
    }

    /**
     * 功能 1.2：Excel批量导入课程
     * [cite: 22, 25]
     */
    public void importCoursesFromCsv(String externalPath) {
        List<String[]> newData = CsvUtil.readCsv(externalPath);
        if (newData == null) {
            System.out.println("错误：无法读取外部文件。");
            return;
        }

        int count = 0;
        // 从第1行开始，跳过表头
        for (int i = 1; i < newData.size(); i++) {
            String[] row = newData.get(i);
            if (row.length >= 5) {
                try {
                    Course c = new Course(row[0], row[1], row[2],
                            Integer.parseInt(row[3]), Integer.parseInt(row[4]));
                    saveCourse(c); // 复用保存逻辑
                    count++;
                } catch (Exception e) {
                    System.out.println("第 " + i + " 行数据错误，已跳过。");
                }
            }
        }
        System.out.println("批量导入完成，共处理 " + count + " 条数据。");
    }

    /**
     * 功能 2：招聘进度监控与导出进度表
     * [cite: 33, 36, 131]
     */
    public void monitorAndExport(String keyword) {
        List<String[]> allCourses = CsvUtil.readCsv(Constants.COURSES_CSV);
        List<String[]> report = new ArrayList<>();
        report.add(new String[]{"课程代码", "名称", "MO ID", "目标人数", "已录用", "缺口"});

        System.out.println("--- 招聘监控报告 ---");
        if (allCourses != null) {
            for (String[] row : allCourses) {
                // 筛选逻辑：匹配课程名或MO ID [cite: 28]
                if (row.length >= 5 && (row[1].contains(keyword) || row[2].equals(keyword))) {
                    int target = Integer.parseInt(row[3]);
                    int current = Integer.parseInt(row[4]);
                    int gap = target - current;

                    String[] reportRow = {row[0], row[1], row[2], row[3], row[4], String.valueOf(gap)};
                    report.add(reportRow);

                    System.out.println("课程: " + row[1] + " | 进度: " + current + "/" + target);
                }
            }
        }
        // 导出Excel进度表（CSV格式） [cite: 43, 44]
        CsvUtil.writeCsv("data/recruitment_progress.csv", report, false);
        System.out.println("进度表已导出至 data/recruitment_progress.csv");
    }
}