package model;

/**
 * Application draft model class representing TA position application draft information.
 * Contains draft ID, student ID, position ID, mode, resume PDF path, update time, expiration time, etc.
 */
public class ApplicationDraft {
    private String id;
    private String studentId;
    private String jobId;
    private String mode;
    private String resumePdfPath;
    private String updatedAt;
    private String expiresAt;
    private Resume resume;

    public ApplicationDraft() {}

    /**
     * Constructor for creating an ApplicationDraft object.
     * @param id draft ID
     * @param studentId student ID
     * @param jobId position ID
     * @param mode application mode
     * @param resumePdfPath resume PDF path
     * @param updatedAt update time
     * @param expiresAt expiration time
     * @param resume resume object
     */
    public ApplicationDraft(String id, String studentId, String jobId, String mode, String resumePdfPath, String updatedAt, String expiresAt, Resume resume) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.mode = mode;
        this.resumePdfPath = resumePdfPath;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.resume = resume;
    }

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

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getResumePdfPath() {
        return resumePdfPath;
    }

    public void setResumePdfPath(String resumePdfPath) {
        this.resumePdfPath = resumePdfPath;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}
