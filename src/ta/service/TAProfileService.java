package ta.service;

import common.CsvUtil;
import ta.model.TAProfile;
import ta.model.TAProfileDraft;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TAProfileService {

    private static final String PROFILE_FILE = "ta_profiles.csv";
    private static final String DRAFT_FILE = "ta_profile_drafts.csv";

    private static final long DRAFT_VALID_MILLIS = 7L * 24 * 60 * 60 * 1000;
    private static final long MAX_PDF_SIZE = 10L * 1024 * 1024;

    public boolean createOrUpdateProfile(String email, String major, double gpa,
                                         String teachingExperience, String resumePath) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Profile save failed: email cannot be empty.");
            return false;
        }

        if (major == null || major.trim().isEmpty()) {
            System.out.println("Profile save failed: major cannot be empty.");
            return false;
        }

        if (gpa < 0.0 || gpa > 4.0) {
            System.out.println("Profile save failed: GPA must be between 0.0 and 4.0.");
            return false;
        }

        if (teachingExperience == null || teachingExperience.trim().isEmpty()) {
            System.out.println("Profile save failed: teaching experience cannot be empty.");
            return false;
        }

        if (!isValidPdf(resumePath)) {
            return false;
        }

        List<TAProfile> profiles = getAllProfiles();
        boolean updated = false;

        for (TAProfile profile : profiles) {
            if (profile.getEmail().equalsIgnoreCase(email)) {
                profile.setMajor(major);
                profile.setGpa(gpa);
                profile.setTeachingExperience(teachingExperience);
                profile.setResumePath(resumePath);
                profile.setLastUpdated(System.currentTimeMillis());
                updated = true;
                break;
            }
        }

        if (!updated) {
            profiles.add(new TAProfile(
                    email,
                    major,
                    gpa,
                    teachingExperience,
                    resumePath,
                    System.currentTimeMillis()
            ));
        }

        rewriteAllProfiles(profiles);
        System.out.println(updated ? "Profile updated successfully." : "Profile created successfully.");
        return true;
    }

    public boolean saveDraft(String email, String major, double gpa,
                             String teachingExperience, String resumePath) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Draft save failed: email cannot be empty.");
            return false;
        }

        List<TAProfileDraft> drafts = getAllDrafts();
        boolean updated = false;

        for (TAProfileDraft draft : drafts) {
            if (draft.getEmail().equalsIgnoreCase(email)) {
                draft.setMajor(major);
                draft.setGpa(gpa);
                draft.setTeachingExperience(teachingExperience);
                draft.setResumePath(resumePath);
                draft.setDraftSaveTime(System.currentTimeMillis());
                updated = true;
                break;
            }
        }

        if (!updated) {
            drafts.add(new TAProfileDraft(
                    email,
                    major,
                    gpa,
                    teachingExperience,
                    resumePath,
                    System.currentTimeMillis()
            ));
        }

        rewriteAllDrafts(drafts);
        System.out.println("Draft saved successfully.");
        return true;
    }

    public TAProfileDraft loadValidDraft(String email) {
        List<TAProfileDraft> drafts = getAllDrafts();

        for (TAProfileDraft draft : drafts) {
            if (draft.getEmail().equalsIgnoreCase(email)) {
                long now = System.currentTimeMillis();
                if (now - draft.getDraftSaveTime() > DRAFT_VALID_MILLIS) {
                    System.out.println("Draft expired (over 7 days).");
                    return null;
                }

                System.out.println("Draft loaded successfully.");
                return draft;
            }
        }

        System.out.println("No draft found.");
        return null;
    }

    public TAProfile getProfileByEmail(String email) {
        List<TAProfile> profiles = getAllProfiles();
        for (TAProfile profile : profiles) {
            if (profile.getEmail().equalsIgnoreCase(email)) {
                return profile;
            }
        }
        return null;
    }

    public boolean previewResume(String email) {
        TAProfile profile = getProfileByEmail(email);
        if (profile == null) {
            System.out.println("Preview failed: profile not found.");
            return false;
        }

        String path = profile.getResumePath();
        if (path == null || path.trim().isEmpty()) {
            System.out.println("Preview failed: no resume path.");
            return false;
        }

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Preview failed: PDF file does not exist.");
            return false;
        }

        System.out.println("Resume path: " + file.getAbsolutePath());

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                System.out.println("Preview opened by system default application.");
            } else {
                System.out.println("Desktop preview is not supported on this device.");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Preview failed: " + e.getMessage());
            return false;
        }
    }

    public List<TAProfile> getAllProfiles() {
        List<String> lines = CsvUtil.readCsv(PROFILE_FILE);
        List<TAProfile> profiles = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && line.contains("email,major,gpa")) {
                continue;
            }
            TAProfile profile = TAProfile.fromCsvLine(line);
            if (profile != null) {
                profiles.add(profile);
            }
        }
        return profiles;
    }

    public List<TAProfileDraft> getAllDrafts() {
        List<String> lines = CsvUtil.readCsv(DRAFT_FILE);
        List<TAProfileDraft> drafts = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && line.contains("email,major,gpa")) {
                continue;
            }
            TAProfileDraft draft = TAProfileDraft.fromCsvLine(line);
            if (draft != null) {
                drafts.add(draft);
            }
        }
        return drafts;
    }

    private void rewriteAllProfiles(List<TAProfile> profiles) {
        List<String> lines = new ArrayList<>();
        lines.add("email,major,gpa,teachingExperience,resumePath,lastUpdated");
        for (TAProfile profile : profiles) {
            lines.add(profile.toCsvLine());
        }
        CsvUtil.writeCsv(PROFILE_FILE, lines, false);
    }

    private void rewriteAllDrafts(List<TAProfileDraft> drafts) {
        List<String> lines = new ArrayList<>();
        lines.add("email,major,gpa,teachingExperience,resumePath,draftSaveTime");
        for (TAProfileDraft draft : drafts) {
            lines.add(draft.toCsvLine());
        }
        CsvUtil.writeCsv(DRAFT_FILE, lines, false);
    }

    private boolean isValidPdf(String resumePath) {
        if (resumePath == null || resumePath.trim().isEmpty()) {
            System.out.println("Resume upload failed: path cannot be empty.");
            return false;
        }

        File file = new File(resumePath);
        if (!file.exists()) {
            System.out.println("Resume upload failed: file does not exist.");
            return false;
        }

        if (!file.isFile()) {
            System.out.println("Resume upload failed: path is not a file.");
            return false;
        }

        if (!resumePath.toLowerCase().endsWith(".pdf")) {
            System.out.println("Resume upload failed: only PDF is allowed.");
            return false;
        }

        if (file.length() > MAX_PDF_SIZE) {
            System.out.println("Resume upload failed: PDF must be <= 10MB.");
            return false;
        }

        return true;
    }
}