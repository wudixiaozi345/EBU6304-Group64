package mo.model;

public class Application {
    public String studentName;
    public double gpa;
    public String major;
    public String resumePath;
    public String status; // Pending, Invited, Hired, Waitlisted, Rejected
    public String reason;
    public String interviewTime; // 【成员4新增】面试时间
    public String taConfirmation; // 【成员4新增】TA确认状态

    public Application(String studentName, double gpa, String major, String resumePath, String status, String reason, String interviewTime, String taConfirmation) {
        this.studentName = studentName;
        this.gpa = gpa;
        this.major = major;
        this.resumePath = resumePath;
        this.status = status;
        this.reason = reason;
        this.interviewTime = interviewTime;
        this.taConfirmation = taConfirmation;
    }

    @Override
    public String toString() {
        // 以CSV格式返回字符串，方便存储
        return studentName + "," + gpa + "," + major + "," + resumePath + "," + status + "," + reason + "," + interviewTime + "," + taConfirmation;
    }
}