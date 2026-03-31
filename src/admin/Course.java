package admin;

/**
 * 课程实体类 - 定义课程的数据结构 [cite: 16]
 */
public class Course {
    private String courseCode; // 课程代码
    private String courseName; // 课程名称
    private String moID;       // 模块负责人ID
    private int targetTA;      // 目标TA人数
    private int currentTA;     // 当前已录用人数

    // 构造函数：用于创建课程对象
    public Course(String courseCode, String courseName, String moID, int targetTA, int currentTA) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.moID = moID;
        this.targetTA = targetTA;
        this.currentTA = currentTA;
    }

    // Getter方法：供AdminExtensionManager调用进行查重
    public String getCourseCode() {
        return courseCode;
    }

    // 将对象转换为CSV行数组：供CsvUtil调用进行存储 [cite: 43, 44]
    public String[] toCsvRow() {
        return new String[]{
                courseCode,
                courseName,
                moID,
                String.valueOf(targetTA),
                String.valueOf(currentTA)
        };
    }
}