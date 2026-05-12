package model;

/**
 * Course model class representing course information.
 * Contains course ID, name, credits, MO ID, semester, status, etc.
 */
public class Course {
    private String id;
    private String name;
    private String credits;
    private String moId;
    private String semester;
    private String status;

    /**
     * Constructor for creating a Course object with default status 'active'.
     * @param id course ID
     * @param name course name
     * @param credits credits
     * @param moId MO ID
     * @param semester semester
     */
    // Backward-compatible constructor defaults status to active.
    public Course(String id, String name, String credits, String moId, String semester) {
        this(id, name, credits, moId, semester, "active");
    }

    /**
     * Constructor for creating a Course object.
     * @param id course ID
     * @param name course name
     * @param credits credits
     * @param moId MO ID
     * @param semester semester
     * @param status status
     */
    public Course(String id, String name, String credits, String moId, String semester, String status) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.moId = moId;
        this.semester = semester;
        this.status = (status == null || status.trim().isEmpty()) ? "active" : status;
    }

    
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

    // Optional: setter methods
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCredits(String credits) { this.credits = credits; }
    public void setMoId(String moId) { this.moId = moId; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setStatus(String status) { this.status = (status == null || status.trim().isEmpty()) ? "active" : status; }
}
