package ta.model;

public class TAProfileDraft {
    private String email;
    private String major;
    private double gpa;
    private String teachingExperience;
    private String resumePath;
    private long draftSaveTime;

    public TAProfileDraft() {
    }

    public TAProfileDraft(String email, String major, double gpa,
                          String teachingExperience, String resumePath, long draftSaveTime) {
        this.email = email;
        this.major = major;
        this.gpa = gpa;
        this.teachingExperience = teachingExperience;
        this.resumePath = resumePath;
        this.draftSaveTime = draftSaveTime;
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

    public long getDraftSaveTime() {
        return draftSaveTime;
    }

    public void setDraftSaveTime(long draftSaveTime) {
        this.draftSaveTime = draftSaveTime;
    }

    public String toCsvLine() {
        return email + "," +
                safe(major) + "," +
                gpa + "," +
                safe(teachingExperience) + "," +
                safe(resumePath) + "," +
                draftSaveTime;
    }

    public static TAProfileDraft fromCsvLine(String line) {
        String[] arr = line.split(",", -1);
        if (arr.length < 6) {
            return null;
        }

        TAProfileDraft draft = new TAProfileDraft();
        draft.setEmail(arr[0]);
        draft.setMajor(arr[1]);
        draft.setGpa(parseDoubleSafe(arr[2]));
        draft.setTeachingExperience(arr[3]);
        draft.setResumePath(arr[4]);
        draft.setDraftSaveTime(parseLongSafe(arr[5]));
        return draft;
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