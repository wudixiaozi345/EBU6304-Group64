package ta.model;

public class TAProfile {
    private String email;
    private String major;
    private double gpa;
    private String teachingExperience;
    private String resumePath;
    private long lastUpdated;

    public TAProfile() {
    }

    public TAProfile(String email, String major, double gpa,
                     String teachingExperience, String resumePath, long lastUpdated) {
        this.email = email;
        this.major = major;
        this.gpa = gpa;
        this.teachingExperience = teachingExperience;
        this.resumePath = resumePath;
        this.lastUpdated = lastUpdated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getTeachingExperience() {
        return teachingExperience;
    }

    public void setTeachingExperience(String teachingExperience) {
        this.teachingExperience = teachingExperience;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String toCsvLine() {
        return email + "," +
                safe(major) + "," +
                gpa + "," +
                safe(teachingExperience) + "," +
                safe(resumePath) + "," +
                lastUpdated;
    }

    public static TAProfile fromCsvLine(String line) {
        String[] arr = line.split(",", -1);
        if (arr.length < 6) {
            return null;
        }

        TAProfile profile = new TAProfile();
        profile.setEmail(arr[0]);
        profile.setMajor(arr[1]);
        profile.setGpa(parseDoubleSafe(arr[2]));
        profile.setTeachingExperience(arr[3]);
        profile.setResumePath(arr[4]);
        profile.setLastUpdated(parseLongSafe(arr[5]));
        return profile;
    }

    private static String safe(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(",", " ");
    }

    private static double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static long parseLongSafe(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return 0L;
        }
    }
}