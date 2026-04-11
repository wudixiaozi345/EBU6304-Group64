package service;

import model.User;
import model.Resume;
import model.Course;
import model.Position;
import model.Application;
import model.ApplicationDraft;
import model.Feedback;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

public class CSVService {
    private static final File DATA_DIRECTORY = resolveDataDirectory();
    private static final String DATA_DIR = DATA_DIRECTORY.getAbsolutePath() + File.separator;
    private static final String RESOURCE_DATA_DIR = "data/";

    public static File getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public static File getDataFile(String filename) {
        return new File(DATA_DIRECTORY, filename);
    }

    private static File resolveDataDirectory() {
        String explicit = System.getProperty("recruit.data.dir");
        if (explicit != null && !explicit.trim().isEmpty()) {
            File configured = new File(explicit.trim());
            if (configured.exists() && configured.isDirectory()) {
                return configured.getAbsoluteFile();
            }
        }

        String userDir = System.getProperty("user.dir", ".");
        File byUserDir = findProjectDataDirectory(new File(userDir));
        if (byUserDir != null) {
            return byUserDir;
        }

        List<File> candidates = new ArrayList<>();
        candidates.add(new File(userDir, "data"));
        candidates.add(new File(userDir, "java-version/data"));
        candidates.add(new File("data"));
        candidates.add(new File("java-version/data"));

        try {
            File codeSource = new File(CSVService.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File classesDir = codeSource.isFile() ? codeSource.getParentFile() : codeSource;
            if (classesDir != null) {
                File byCodeSource = findProjectDataDirectory(classesDir);
                if (byCodeSource != null) {
                    return byCodeSource;
                }

                File targetDir = classesDir.getParentFile();
                if (targetDir != null) {
                    candidates.add(new File(targetDir, "data"));

                    File projectDir = targetDir.getParentFile();
                    if (projectDir != null) {
                        candidates.add(new File(projectDir, "data"));
                    }
                }
            }
        } catch (Exception ignored) {
            // Fall back to the candidate list built from user.dir.
        }

        for (File candidate : candidates) {
            if (candidate != null && candidate.exists() && candidate.isDirectory() && !looksLikePackagedClassesData(candidate)) {
                return candidate.getAbsoluteFile();
            }
        }

        File fallback = new File(userDir, "java-version/data");
        if (!fallback.exists()) {
            fallback.mkdirs();
        }
        return fallback.getAbsoluteFile();
    }

    private static File findProjectDataDirectory(File start) {
        File current = start;
        while (current != null) {
            File javaVersionData = new File(current, "java-version/data");
            if (looksLikeValidDataDirectory(javaVersionData)) {
                return javaVersionData.getAbsoluteFile();
            }

            File data = new File(current, "data");
            if (looksLikeValidDataDirectory(data) && !looksLikePackagedClassesData(data)) {
                return data.getAbsoluteFile();
            }

            current = current.getParentFile();
        }
        return null;
    }

    private static boolean looksLikeValidDataDirectory(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return false;
        }
        return new File(dir, "ta_account.csv").exists() && new File(dir, "application.csv").exists();
    }

    private static boolean looksLikePackagedClassesData(File dir) {
        String path = dir.getAbsolutePath().replace('\\', '/').toLowerCase(Locale.ROOT);
        return path.contains("/web-inf/classes/data") || path.contains("/target/classes/data");
    }

    private static InputStream getInputStream(String filename) throws FileNotFoundException {
        File file = getDataFile(filename);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        InputStream is = CSVService.class.getClassLoader().getResourceAsStream(RESOURCE_DATA_DIR + filename);
        if (is == null) {
            throw new FileNotFoundException("data file not found: " + filename);
        }
        return is;
    }

    // ====================== 登录相关：读取三个角色的账号 ======================
    public static List<User> readAdminUsers() {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("admin_account.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String id = nextLine.length > 0 ? nextLine[0] : "";
                String password = nextLine.length > 1 ? nextLine[1] : "";
                String name = nextLine.length > 2 ? nextLine[2] : "";
                String email = nextLine.length > 3 ? nextLine[3] : "";
                String status = nextLine.length > 4 ? nextLine[4] : "active";
                users.add(new User(id, name, email, password, "admin", status == null || status.isEmpty() ? "active" : status));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return users;
    }

    public static void writeAdminUsers(List<User> users) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIR + "admin_account.csv"))) {
            writer.writeNext(new String[]{"id", "password", "name", "email", "status"});
            for (User u : users) {
                writer.writeNext(new String[]{u.getId(), u.getPassword(), u.getName(), u.getEmail(), u.getStatus() == null ? "active" : u.getStatus()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static List<User> readMoUsers() {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("mo_account.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String status = nextLine.length > 4 ? nextLine[4] : "enabled";
                users.add(new User(
                        nextLine[0],
                        nextLine[2],
                        nextLine[3],
                        nextLine[1],
                        "mo",
                        "enabled".equalsIgnoreCase(status) ? "active" : "disabled"
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return users;
    }

    public static void writeMoUsers(List<User> users) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIR + "mo_account.csv"))) {
            writer.writeNext(new String[]{"staff_id", "password", "name", "email", "status"});
            for (User u : users) {
                String status = "active".equals(u.getStatus()) ? "enabled" : "disabled";
                writer.writeNext(new String[]{u.getId(), u.getPassword(), u.getName(), u.getEmail(), status});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static List<User> readTaUsers() {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("ta_account.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String status = nextLine.length > 4 ? nextLine[4] : "active";
                users.add(new User(
                        nextLine[0],
                        nextLine[2],
                        nextLine[3],
                        nextLine[1],
                        "ta",
                        status
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return users;
    }

    public static void writeTaUsers(List<User> users) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIR + "ta_account.csv"))) {
            writer.writeNext(new String[]{"student_id", "password", "name", "email", "status"});
            for (User u : users) {
                writer.writeNext(new String[]{u.getId(), u.getPassword(), u.getName(), u.getEmail(), u.getStatus() == null ? "active" : u.getStatus()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ====================== Existing behavior: skip CSV header (fixed) ======================
    public static List<User> readUsers() {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("users.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 6) continue;
                users.add(new User(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5]));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return users;
    }

    public static Resume getResume(String studentId) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("ta_resume.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length == 0 || nextLine[0] == null) continue;
                if (nextLine[0].equals(studentId)) {
                    if (nextLine.length >= 14) {
                        return new Resume(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5], nextLine[6], nextLine[7], nextLine[8], nextLine[9], nextLine[10], nextLine[11], nextLine[12], nextLine[13]);
                    } else {
                        // Backwards compatibility for old resume CSV format (5 columns)
                        String name = nextLine.length > 1 ? nextLine[1] : "";
                        String email = nextLine.length > 2 ? nextLine[2] : "";
                        String major = nextLine.length > 3 ? nextLine[3] : "";
                        String grade = "";
                        String gpa = "";
                        String englishScore = "";
                        String skills = nextLine.length > 4 ? nextLine[4] : "";
                        String relatedCourses = "";
                        String awards = "";
                        String projects = "";
                        String experience = "";
                        String competency = "";
                        String workHours = nextLine.length > 4 ? nextLine[4] : "";
                        return new Resume(nextLine[0], name, email, major, grade, gpa, englishScore, skills, relatedCourses, awards, projects, experience, competency, workHours);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isResumeComplete(String studentId) {
        Resume resume = getResume(studentId);
        if (resume == null) return false;

        return isFilled(resume.getName())
            && isFilled(resume.getEmail())
            && isFilled(resume.getMajor())
            && isFilled(resume.getGrade())
            && isFilled(resume.getGpa())
            && isFilled(resume.getSkills())
            && isFilled(resume.getWorkHours())
            && isFilled(resume.getCompetency());
    }

    private static boolean isFilled(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static List<Course> readCourses() {
        List<Course> courses = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("courses.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // CSV schema: course_id,name,credits,mo_staff_id,semester[,status]
                String id = nextLine.length > 0 ? nextLine[0] : "";
                String name = nextLine.length > 1 ? nextLine[1] : "";
                String code = nextLine.length > 2 ? nextLine[2] : "";
                String moId = nextLine.length > 3 ? nextLine[3] : "";
                String semester = nextLine.length > 4 ? nextLine[4] : "";
                String status = nextLine.length > 5 ? nextLine[5] : "active";
                courses.add(new Course(id, name, code, moId, semester, status));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return courses;
    }

    public static void writeCourses(List<Course> courses) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIR + "courses.csv"))) {
            writer.writeNext(new String[]{"course_id", "name", "credits", "mo_staff_id", "semester", "status"});
            for (Course c : courses) {
                writer.writeNext(new String[]{
                        safe(c.getId()),
                        safe(c.getName()),
                        safe(c.getCredits()),
                        safe(c.getMoId()),
                        safe(c.getSemester()),
                        safe(c.getStatus())
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean courseExists(String courseId) {
        return readCourses().stream().anyMatch(c -> courseId != null && courseId.equals(c.getId()));
    }

    public static void addCourse(Course course) {
        List<Course> all = readCourses();
        all.add(course);
        writeCourses(all);
    }

    public static List<Position> readPositions() {
        List<Position> positions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("position.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String id = nextLine.length > 0 ? nextLine[0] : "";
                String courseId = nextLine.length > 1 ? nextLine[1] : "";
                String title = nextLine.length > 2 ? nextLine[2] : "";
                String requirements = nextLine.length > 3 ? nextLine[3] : "";
                String status = nextLine.length > 4 ? nextLine[4] : "open";
                String deadline = nextLine.length > 5 ? nextLine[5] : "";
                String vacancies = nextLine.length > 6 ? nextLine[6] : "";
                String moId = nextLine.length > 7 ? nextLine[7] : "";
                String preferredCondition = nextLine.length > 8 ? nextLine[8] : "";
                String minGpa = nextLine.length > 9 ? nextLine[9] : "";
                String minEnglishScore = nextLine.length > 10 ? nextLine[10] : "";
                Position pos = new Position(id, title, courseId, requirements, status, deadline, vacancies, moId, preferredCondition, minGpa, minEnglishScore);
                positions.add(pos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }

    public static void writePositions(List<Position> positions) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIR + "position.csv"))) {
            writer.writeNext(new String[]{"id", "course_id", "title", "requirements", "status", "deadline", "vacancies", "mo_id", "preferred_condition", "min_gpa", "min_english_score"});
            for (Position p : positions) {
                writer.writeNext(new String[]{
                        p.getId(),
                        p.getCourseId(),
                        p.getTitle(),
                        p.getRequirements(),
                        p.getStatus(),
                        p.getDeadline(),
                        p.getVacancies(),
                        p.getMoId(),
                        safe(p.getPreferredCondition()),
                        safe(p.getMinGpa()),
                        safe(p.getMinEnglishScore())
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPosition(Position position) {
        List<Position> all = readPositions();
        all.add(position);
        writePositions(all);
    }

    public static void updatePosition(Position position) {
        List<Position> all = readPositions();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(position.getId())) {
                all.set(i, position);
                break;
            }
        }
        writePositions(all);
    }

    public static void deletePosition(String positionId) {
        List<Position> all = readPositions();
        all.removeIf(p -> p.getId().equals(positionId));
        writePositions(all);
    }

    public static void deletePositionsByCourseId(String courseId) {
        List<Position> all = readPositions();
        all.removeIf(p -> courseId != null && courseId.equals(p.getCourseId()));
        writePositions(all);
    }

    public static List<Application> readApplications() {
        List<Application> apps = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(getInputStream("application.csv"), java.nio.charset.StandardCharsets.UTF_8))) {
            reader.readNext(); // 跳过表头
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 5) continue;
                String id = nextLine[0];
                String positionId = nextLine.length > 1 ? nextLine[1] : "";
                String studentId = nextLine.length > 2 ? nextLine[2] : "";
                String status = nextLine.length > 3 ? nextLine[3] : "";
                String reason = nextLine.length > 4 ? nextLine[4] : "";
                String createdAt = nextLine.length > 5 ? nextLine[5] : "";
                String resumePdfPath = nextLine.length > 6 ? nextLine[6] : "";
                String interviewConfirmStatus = nextLine.length > 7 ? nextLine[7] : "not_sent";

                // Backward compatibility: swap if positionId seems not a position and studentId looks like a position
                if (!positionId.startsWith("P") && studentId.startsWith("P")) {
                    String tmp = positionId;
                    positionId = studentId;
                    studentId = tmp;
                }

                Application app = new Application(id, studentId, positionId, status, reason, createdAt);
                app.setResumePdfPath(resumePdfPath);
                app.setInterviewConfirmStatus(interviewConfirmStatus == null || interviewConfirmStatus.trim().isEmpty() ? "not_sent" : interviewConfirmStatus);
                apps.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apps;
    }

    // ====================== 以下不变 ======================
    public static void writeUsers(List<User> users) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIR + "users.csv"))) {
            for (User u : users) {
                writer.writeNext(new String[]{u.getId(), u.getName(), u.getEmail(), u.getPassword(), u.getRole(), u.getStatus()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static Properties readAdminConfig() {
        Properties props = new Properties();
        File configFile = new File(DATA_DIR + "admin_config.properties");
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            } catch (Exception e) { e.printStackTrace(); }
        }
        props.putIfAbsent("application.deadline.format", "YYYY-MM-DD");
        props.putIfAbsent("login.fail.limit", "3");
        props.putIfAbsent("login.lock.minutes", "5");
        return props;
    }

    public static void saveAdminConfig(Properties props) {
        try (FileOutputStream fos = new FileOutputStream(DATA_DIR + "admin_config.properties")) {
            props.store(fos, "Admin configuration") ;
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void saveResume(Resume resume) {
        List<String[]> allResumes = new ArrayList<>();
        String[] header = new String[]{"student_id", "name", "email", "major", "grade", "gpa", "englishScore", "skills", "relatedCourses", "awards", "projects", "experience", "competency", "workHours"};
        allResumes.add(header);
        boolean found = false;

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(DATA_DIR + "ta_resume.csv"), StandardCharsets.UTF_8))) {
            String[] oldHeader = reader.readNext();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length == 0 || nextLine[0] == null) continue;
                if (nextLine[0].equals(resume.getStudentId())) {
                    allResumes.add(resumeToArray(resume));
                    found = true;
                } else {
                    allResumes.add(oldResumeToArray(nextLine));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!found) {
            allResumes.add(resumeToArray(resume));
        }

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(DATA_DIR + "ta_resume.csv"), StandardCharsets.UTF_8))) {
            writer.writeAll(allResumes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] oldResumeToArray(String[] nextLine) {
        String[] converted = new String[14];
        converted[0] = nextLine.length > 0 ? nextLine[0] : "";
        converted[1] = nextLine.length > 1 ? nextLine[1] : "";
        converted[2] = nextLine.length > 2 ? nextLine[2] : "";
        converted[3] = nextLine.length > 3 ? nextLine[3] : "";
        converted[4] = nextLine.length > 4 ? "" : "";
        converted[5] = "";
        converted[6] = "";
        converted[7] = nextLine.length > 3 ? nextLine[3] : "";
        converted[8] = "";
        converted[9] = "";
        converted[10] = "";
        converted[11] = "";
        converted[12] = "";
        converted[13] = nextLine.length > 4 ? nextLine[4] : "";
        return converted;
    }

    public static void saveApplication(Application app) {
        File file = new File(DATA_DIR + "application.csv");
        List<String[]> allApps = new ArrayList<>();
        if (!file.exists()) {
            allApps.add(new String[]{"id", "position_id", "student_id", "status", "reason", "createdAt", "resumePdfPath", "interviewConfirmStatus"});
        }
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            if (header != null && allApps.isEmpty()) {
                if (header.length < 8) {
                    allApps.add(new String[]{"id", "position_id", "student_id", "status", "reason", "createdAt", "resumePdfPath", "interviewConfirmStatus"});
                } else {
                    allApps.add(header);
                }
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                allApps.add(nextLine);
            }
        } catch (FileNotFoundException ignore) {
            // first-time create
        } catch (Exception e) {
            e.printStackTrace();
        }

        allApps.add(new String[]{
            app.getId(),
            app.getPositionId(),
            app.getStudentId(),
            app.getStatus(),
            app.getReason(),
            app.getCreatedAt(),
            safe(app.getResumePdfPath()),
            safe(app.getInterviewConfirmStatus())
        });

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(allApps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveApplicationResume(String appId, Resume resume) {
        File file = new File(DATA_DIR + "application_resume.csv");
        List<String[]> rows = new ArrayList<>();
        if (!file.exists()) {
            rows.add(new String[]{"app_id", "student_id", "name", "email", "major", "grade", "gpa", "englishScore", "skills", "relatedCourses", "awards", "projects", "experience", "competency", "workHours"});
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String[] header = reader.readNext();
            if (header != null && rows.isEmpty()) {
                rows.add(header);
            }
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length > 0 && appId.equals(line[0])) {
                    continue;
                }
                rows.add(line);
            }
        } catch (FileNotFoundException ignore) {
            // first-time create
        } catch (Exception e) {
            e.printStackTrace();
        }

        rows.add(new String[]{
                appId,
                resume.getStudentId(),
                safe(resume.getName()),
                safe(resume.getEmail()),
                safe(resume.getMajor()),
                safe(resume.getGrade()),
                safe(resume.getGpa()),
                safe(resume.getEnglishScore()),
                safe(resume.getSkills()),
                safe(resume.getRelatedCourses()),
                safe(resume.getAwards()),
                safe(resume.getProjects()),
                safe(resume.getExperience()),
                safe(resume.getCompetency()),
                safe(resume.getWorkHours())
        });

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.writeAll(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Resume getApplicationResume(String appId) {
        File file = new File(DATA_DIR + "application_resume.csv");
        if (!file.exists()) {
            return null;
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 15) continue;
                if (appId.equals(line[0])) {
                    return new Resume(
                            line[1], line[2], line[3], line[4], line[5], line[6], line[7],
                            line[8], line[9], line[10], line[11], line[12], line[13], line[14]
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateApplicationStatus(String appId, String status, String reason) {
        File file = new File(DATA_DIR + "application.csv");
        List<String[]> allApps = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            if (header != null) {
                if (header.length < 8) {
                    allApps.add(new String[]{"id", "position_id", "student_id", "status", "reason", "createdAt", "resumePdfPath", "interviewConfirmStatus"});
                } else {
                    allApps.add(header);
                }
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 8) {
                    String[] expanded = new String[8];
                    for (int i = 0; i < nextLine.length; i++) {
                        expanded[i] = nextLine[i];
                    }
                    if (expanded[6] == null) expanded[6] = "";
                    if (expanded[7] == null) expanded[7] = "not_sent";
                    nextLine = expanded;
                }
                if (nextLine.length > 0 && nextLine[0].equals(appId)) {
                    if (nextLine.length > 3) nextLine[3] = status;
                    if (nextLine.length > 4) nextLine[4] = reason;
                }
                allApps.add(nextLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(allApps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteApplication(String appId) {
        File file = new File(DATA_DIR + "application.csv");
        List<String[]> allApps = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            if (header != null) {
                allApps.add(header);
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 0 && nextLine[0].equals(appId)) {
                    continue; // remove this row
                }
                allApps.add(nextLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(allApps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteApplicationResume(String appId) {
        File file = new File(DATA_DIR + "application_resume.csv");
        if (!file.exists()) return;

        List<String[]> allRows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            if (header != null) {
                allRows.add(header);
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 0 && appId.equals(nextLine[0])) {
                    continue;
                }
                allRows.add(nextLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(allRows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteApplicationsByPositionId(String positionId) {
        List<Application> apps = readApplications();
        for (Application app : apps) {
            if (positionId != null && positionId.equals(app.getPositionId())) {
                deleteApplicationResume(app.getId());
            }
        }

        File file = new File(DATA_DIR + "application.csv");
        List<String[]> allApps = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            if (header != null) {
                allApps.add(header);
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 1 && positionId.equals(nextLine[1])) {
                    continue;
                }
                allApps.add(nextLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(allApps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteApplicationsByStudentId(String studentId) {
        File file = new File(DATA_DIR + "application.csv");
        List<String[]> allApps = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            if (header != null) {
                allApps.add(header);
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 2 && nextLine[2].equals(studentId)) {
                    continue; // 删除这一行（student_id 是第3列）
                }
                allApps.add(nextLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(allApps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] resumeToArray(Resume r) {
        return new String[]{
                r.getStudentId(), r.getName(), r.getEmail(), r.getMajor(), r.getGrade(),
                r.getGpa(), r.getEnglishScore(), r.getSkills(), r.getRelatedCourses(),
                r.getAwards(), r.getProjects(), r.getExperience(), r.getCompetency(), r.getWorkHours()
        };
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    public static Application findApplicationById(String appId) {
        return readApplications().stream()
                .filter(a -> appId != null && appId.equals(a.getId()))
                .findFirst()
                .orElse(null);
    }

    public static void updateApplicationInterviewConfirmStatus(String appId, String confirmStatus) {
        File file = new File(DATA_DIR + "application.csv");
        List<String[]> allApps = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] header = reader.readNext();
            if (header != null) {
                if (header.length < 8) {
                    allApps.add(new String[]{"id", "position_id", "student_id", "status", "reason", "createdAt", "resumePdfPath", "interviewConfirmStatus"});
                } else {
                    allApps.add(header);
                }
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 8) {
                    String[] expanded = new String[8];
                    for (int i = 0; i < nextLine.length; i++) expanded[i] = nextLine[i];
                    if (expanded[6] == null) expanded[6] = "";
                    if (expanded[7] == null) expanded[7] = "not_sent";
                    nextLine = expanded;
                }
                if (nextLine.length > 0 && appId != null && appId.equals(nextLine[0])) {
                    nextLine[7] = confirmStatus == null ? "pending" : confirmStatus;
                }
                allApps.add(nextLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(allApps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isTaProfileCompleted(String studentId) {
        return readTaUsers().stream()
                .filter(u -> studentId != null && studentId.equals(u.getId()))
                .map(User::getStatus)
                .findFirst()
                .map(s -> "active".equalsIgnoreCase(s))
                .orElse(false);
    }

    public static void updateTaStatus(String studentId, String status) {
        List<User> users = readTaUsers();
        boolean changed = false;
        for (User u : users) {
            if (studentId != null && studentId.equals(u.getId())) {
                u.setStatus(status == null ? "active" : status);
                changed = true;
                break;
            }
        }
        if (changed) {
            writeTaUsers(users);
        }
    }

    public static List<ApplicationDraft> readApplicationDrafts() {
        List<ApplicationDraft> drafts = new ArrayList<>();
        File file = new File(DATA_DIR + "application_draft.csv");
        if (!file.exists()) {
            return drafts;
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 22) {
                    continue;
                }

                Resume resume = new Resume(
                        line[1], line[7], line[8], line[9], line[10], line[11], line[12], line[13], line[14], line[15],
                        line[16], line[17], line[18], line[19]
                );
                ApplicationDraft draft = new ApplicationDraft(line[0], line[1], line[2], line[3], line[4], line[5], line[6], resume);
                drafts.add(draft);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drafts;
    }

    public static ApplicationDraft findValidDraft(String studentId, String jobId) {
        LocalDateTime now = LocalDateTime.now();
        return readApplicationDrafts().stream()
                .filter(d -> studentId != null && studentId.equals(d.getStudentId()))
                .filter(d -> jobId != null && jobId.equals(d.getJobId()))
                .filter(d -> {
                    try {
                        return d.getExpiresAt() != null && LocalDateTime.parse(d.getExpiresAt()).isAfter(now);
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }

    public static void saveApplicationDraft(ApplicationDraft draft) {
        List<ApplicationDraft> allDrafts = readApplicationDrafts();
        allDrafts = allDrafts.stream()
                .filter(d -> !(Objects.equals(d.getStudentId(), draft.getStudentId()) && Objects.equals(d.getJobId(), draft.getJobId())))
                .collect(Collectors.toList());
        allDrafts.add(draft);
        writeApplicationDrafts(allDrafts);
    }

    public static void deleteApplicationDraft(String studentId, String jobId) {
        List<ApplicationDraft> allDrafts = readApplicationDrafts().stream()
                .filter(d -> !(Objects.equals(d.getStudentId(), studentId) && Objects.equals(d.getJobId(), jobId)))
                .collect(Collectors.toList());
        writeApplicationDrafts(allDrafts);
    }

    private static void writeApplicationDrafts(List<ApplicationDraft> drafts) {
        File file = new File(DATA_DIR + "application_draft.csv");
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.writeNext(new String[]{
                    "id", "student_id", "job_id", "mode", "resume_pdf_path", "updated_at", "expires_at",
                    "name", "email", "major", "grade", "gpa", "englishScore", "skills", "relatedCourses",
                    "awards", "projects", "experience", "competency", "workHours", "_reserved1", "_reserved2"
            });

            for (ApplicationDraft d : drafts) {
                Resume r = d.getResume() == null ? new Resume() : d.getResume();
                writer.writeNext(new String[]{
                        safe(d.getId()),
                        safe(d.getStudentId()),
                        safe(d.getJobId()),
                        safe(d.getMode()),
                        safe(d.getResumePdfPath()),
                        safe(d.getUpdatedAt()),
                        safe(d.getExpiresAt()),
                        safe(r.getName()),
                        safe(r.getEmail()),
                        safe(r.getMajor()),
                        safe(r.getGrade()),
                        safe(r.getGpa()),
                        safe(r.getEnglishScore()),
                        safe(r.getSkills()),
                        safe(r.getRelatedCourses()),
                        safe(r.getAwards()),
                        safe(r.getProjects()),
                        safe(r.getExperience()),
                        safe(r.getCompetency()),
                        safe(r.getWorkHours()),
                        "",
                        ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Feedback> readFeedbacks() {
        List<Feedback> feedbacks = new ArrayList<>();
        File file = new File(DATA_DIR + "feedback.csv");
        if (!file.exists()) {
            return feedbacks;
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 8) {
                    continue;
                }
                feedbacks.add(new Feedback(line[0], line[1], line[2], line[3], line[4], line[5], line[6], line[7]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feedbacks;
    }

    public static void saveFeedback(Feedback feedback) {
        List<Feedback> all = readFeedbacks();
        all.add(feedback);
        writeFeedbacks(all);
    }

    public static void updateFeedback(String feedbackId, String status, String reply) {
        List<Feedback> all = readFeedbacks();
        for (Feedback f : all) {
            if (feedbackId != null && feedbackId.equals(f.getId())) {
                if (status != null && !status.trim().isEmpty()) {
                    f.setStatus(status.trim());
                }
                f.setReply(reply == null ? "" : reply.trim());
                break;
            }
        }
        writeFeedbacks(all);
    }

    private static void writeFeedbacks(List<Feedback> feedbacks) {
        File file = new File(DATA_DIR + "feedback.csv");
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.writeNext(new String[]{"id", "from_role", "from_user_id", "title", "content", "status", "reply", "created_at"});
            for (Feedback f : feedbacks) {
                writer.writeNext(new String[]{
                        safe(f.getId()),
                        safe(f.getFromRole()),
                        safe(f.getFromUserId()),
                        safe(f.getTitle()),
                        safe(f.getContent()),
                        safe(f.getStatus()),
                        safe(f.getReply()),
                        safe(f.getCreatedAt())
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
