package mo.model;

public class Application {
    public String studentName;
    public double gpa;
    public String major;
    public String resumePath; // PDF路径
    public String status; // Pending, Accepted, Rejected
    public String reason; // 拒绝或通过的理由

    public Application(String studentName, double gpa, String major, String resumePath, String status, String reason) {
        this.studentName = studentName;
        this.gpa = gpa;
        this.major = major;
        this.resumePath = resumePath;
        this.status = status;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return studentName + "," + gpa + "," + major + "," + resumePath + "," + status + "," + reason;
    }
}