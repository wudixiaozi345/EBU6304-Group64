package com.bupt.recruit.model;

import com.bupt.recruit.model.Resume;

public class Application {
    private String id;
    private String studentId;
    private String positionId;
    private String status;
    private String reason;
    private String createdAt;
    private String resumePdfPath;
    private String interviewConfirmStatus;

    // 用于页面展示的扩展字段
    private String positionTitle;
    private String courseName;
    private String courseId;

    private Resume resume; // 关联 TA 简历

    // 构造方法（对应 CSV 里的 6 个字段）
    public Application(String id, String studentId, String positionId, String status, String reason, String createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.positionId = positionId;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.resumePdfPath = "";
        this.interviewConfirmStatus = "not_sent";
    }

    // Getter & Setter 完整齐全
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public String getResumePdfPath() {
        return resumePdfPath;
    }

    public void setResumePdfPath(String resumePdfPath) {
        this.resumePdfPath = resumePdfPath;
    }

    public String getInterviewConfirmStatus() {
        return interviewConfirmStatus;
    }

    public void setInterviewConfirmStatus(String interviewConfirmStatus) {
        this.interviewConfirmStatus = interviewConfirmStatus;
    }
}