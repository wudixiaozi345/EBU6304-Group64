package com.bupt.recruit.model;

public class Position {
    private String id;
    private String title;
    private String courseId;
    private String requirements;
    private String status;
    private String deadline;
    private String vacancies;
    private String moId;
    private boolean applied;
    private String courseName; // 新增：课程名称
    private String applyDisabledReason;

    // 7 参数构造（对应 CSV）：含 status, deadline, vacancies, moId
    public Position(String id, String title, String courseId, String requirements, String status, String deadline, String vacancies, String moId) {
        this.id = id;
        this.title = title;
        this.courseId = courseId;
        this.requirements = requirements;
        this.status = status == null || status.trim().isEmpty() ? "open" : status;
        this.deadline = deadline == null ? "" : deadline;
        this.vacancies = vacancies == null ? "" : vacancies;
        this.moId = moId == null ? "" : moId;
        this.applied = false;
        this.applyDisabledReason = "";
    }

    public Position(String id, String title, String courseId, String requirements, String deadline, String vacancies, String moId) {
        this(id, title, courseId, requirements, "open", deadline, vacancies, moId);
    }

    public Position(String id, String title, String courseId, String requirements) {
        this(id, title, courseId, requirements, "open", "", "", "");
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getVacancies() {
        return vacancies;
    }

    public void setVacancies(String vacancies) {
        this.vacancies = vacancies;
    }

    public String getMoId() {
        return moId;
    }

    public void setMoId(String moId) {
        this.moId = moId;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getApplyDisabledReason() {
        return applyDisabledReason;
    }

    public void setApplyDisabledReason(String applyDisabledReason) {
        this.applyDisabledReason = applyDisabledReason == null ? "" : applyDisabledReason;
    }
}