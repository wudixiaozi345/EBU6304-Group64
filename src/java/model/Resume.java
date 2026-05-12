package model;
public class Resume {
    private String studentId;
    private String name;
    private String email;
    private String major;
    private String grade;
    private String gpa;
    private String englishScore;
    private String skills;
    private String relatedCourses;
    private String awards;
    private String projects;
    private String experience;
    private String competency;
    private String workHours;

    // Constructors, Getters, and Setters
    public Resume() {}
    public Resume(String studentId, String name, String email, String major, String grade, String gpa, String englishScore, String skills, String relatedCourses, String awards, String projects, String experience, String competency, String workHours) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.major = major;
        this.grade = grade;
        this.gpa = gpa;
        this.englishScore = englishScore;
        this.skills = skills;
        this.relatedCourses = relatedCourses;
        this.awards = awards;
        this.projects = projects;
        this.experience = experience;
        this.competency = competency;
        this.workHours = workHours;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
    public String getEnglishScore() { return englishScore; }
    public void setEnglishScore(String englishScore) { this.englishScore = englishScore; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getRelatedCourses() { return relatedCourses; }
    public void setRelatedCourses(String relatedCourses) { this.relatedCourses = relatedCourses; }
    public String getAwards() { return awards; }
    public void setAwards(String awards) { this.awards = awards; }
    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getCompetency() { return competency; }
    public void setCompetency(String competency) { this.competency = competency; }
    public String getWorkHours() { return workHours; }
    public void setWorkHours(String workHours) { this.workHours = workHours; }
}
