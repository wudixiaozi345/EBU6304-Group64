package model;
public class Course {
    private String id;
    private String name;
    private String credits;
    private String moId;
    private String semester;
    private String status;

    // Backward-compatible constructor defaults status to active.
    public Course(String id, String name, String credits, String moId, String semester) {
        this(id, name, credits, moId, semester, "active");
    }

    public Course(String id, String name, String credits, String moId, String semester, String status) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.moId = moId;
        this.semester = semester;
        this.status = (status == null || status.trim().isEmpty()) ? "active" : status;
    }

    // 必须有这些 getter 方法！否则部分 JSP 会报错
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCredits() {
        return credits;
    }

    public String getMoId() {
        return moId;
    }

    public String getSemester() {
        return semester;
    }

    public String getStatus() {
        return status;
    }

    // 可选：setter 方法
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCredits(String credits) { this.credits = credits; }
    public void setMoId(String moId) { this.moId = moId; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setStatus(String status) { this.status = (status == null || status.trim().isEmpty()) ? "active" : status; }
}
