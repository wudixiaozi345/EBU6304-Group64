package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.ApplicationDraft;
import com.bupt.recruit.model.Resume;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@MultipartConfig(maxFileSize = 10 * 1024 * 1024)
public class TaApplyServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"ta".equals(user.getRole())) {
            response.sendError(403);
            return;
        }

        if (!CSVService.isTaProfileCompleted(user.getId())) {
            response.sendRedirect(request.getContextPath() + "/ta/profile?mustComplete=true");
            return;
        }

        String jobId = request.getParameter("jobId");
        if (jobId == null || jobId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs");
            return;
        }

        List<Application> existing = CSVService.readApplications();
        if (!canSubmitNewApplication(existing, user.getId())) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?limitOne=true");
            return;
        }

        boolean alreadyApplied = existing.stream().anyMatch(a -> a.getStudentId().equals(user.getId()) && a.getPositionId().equals(jobId) && !"rejected".equalsIgnoreCase(a.getStatus()));
        if (alreadyApplied) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?already=true");
            return;
        }

        Position targetPos = CSVService.readPositions().stream()
                .filter(p -> Objects.equals(p.getId(), jobId))
                .findFirst()
                .orElse(null);

        if (targetPos == null) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?unavailable=true");
            return;
        }

        Course targetCourse = CSVService.readCourses().stream()
            .filter(c -> Objects.equals(c.getId(), targetPos.getCourseId()))
            .findFirst()
            .orElse(null);

        boolean courseActive = targetCourse != null && "active".equalsIgnoreCase(targetCourse.getStatus());
        boolean positionOpen = "open".equalsIgnoreCase(targetPos.getStatus());
        if (!courseActive || !positionOpen) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?unavailable=true");
            return;
        }

        String courseName = "";
        if (targetPos != null) {
            courseName = CSVService.readCourses().stream()
                    .filter(c -> Objects.equals(c.getId(), targetPos.getCourseId()))
                    .map(Course::getName)
                    .findFirst()
                    .orElse("");
        }

        request.setAttribute("jobId", jobId);
        request.setAttribute("jobTitle", targetPos == null ? "" : targetPos.getTitle());
        request.setAttribute("courseName", courseName);
        ApplicationDraft draft = CSVService.findValidDraft(user.getId(), jobId);
        if (draft != null) {
            request.setAttribute("draft", draft);
            request.setAttribute("draftResume", draft.getResume());
        }
        request.getRequestDispatcher("/jsp/ta-apply-resume.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"ta".equals(user.getRole())) {
            response.sendError(403);
            return;
        }

        if (!CSVService.isTaProfileCompleted(user.getId())) {
            response.sendRedirect(request.getContextPath() + "/ta/profile?mustComplete=true");
            return;
        }

        String jobId = request.getParameter("jobId");
        Position targetPos = CSVService.readPositions().stream()
            .filter(p -> Objects.equals(p.getId(), jobId))
            .findFirst()
            .orElse(null);
        Course targetCourse = targetPos == null ? null : CSVService.readCourses().stream()
            .filter(c -> Objects.equals(c.getId(), targetPos.getCourseId()))
            .findFirst()
            .orElse(null);

        if (targetPos == null || targetCourse == null
            || !"active".equalsIgnoreCase(targetCourse.getStatus())
            || !"open".equalsIgnoreCase(targetPos.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?unavailable=true");
            return;
        }

        String action = request.getParameter("action");
        String submitAction = action == null ? "submit" : action;
        String resumeMode = request.getParameter("resumeMode");
        if (!"pdf".equalsIgnoreCase(resumeMode)) {
            resumeMode = "online";
        }

        Resume resumeInput = new Resume(
                user.getId(),
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("major"),
                request.getParameter("grade"),
                request.getParameter("gpa"),
                request.getParameter("englishScore"),
                request.getParameter("skills"),
                request.getParameter("relatedCourses"),
                request.getParameter("awards"),
                request.getParameter("projects"),
                request.getParameter("experience"),
                request.getParameter("competency"),
                request.getParameter("workHours")
        );

        if (!filled(resumeInput.getWorkHours())) {
            response.sendRedirect(request.getContextPath() + "/ta/apply?jobId=" + jobId + "&timeRequired=true");
            return;
        }

        String uploadError = "";
        String pdfPath = request.getParameter("existingPdfPath") == null ? "" : request.getParameter("existingPdfPath");
        try {
            String uploadedPath = saveResumePdf(request, user.getId(), UUID.randomUUID().toString());
            if (uploadedPath != null && !uploadedPath.trim().isEmpty()) {
                pdfPath = uploadedPath;
            }
        } catch (Exception ex) {
            uploadError = "uploadError=true";
        }

        if ("saveDraft".equalsIgnoreCase(submitAction)) {
            LocalDateTime now = LocalDateTime.now();
            ApplicationDraft draft = new ApplicationDraft(
                    UUID.randomUUID().toString(),
                    user.getId(),
                    jobId,
                    resumeMode,
                    pdfPath,
                    now.toString(),
                    now.plusDays(7).toString(),
                    resumeInput
            );
            CSVService.saveApplicationDraft(draft);
            String suffix = uploadError.isEmpty() ? "" : "&" + uploadError;
            response.sendRedirect(request.getContextPath() + "/ta/apply?jobId=" + jobId + "&draftSaved=true" + suffix);
            return;
        }

        if ("online".equalsIgnoreCase(resumeMode) && !isCompleteResumeInput(resumeInput)) {
            response.sendRedirect(request.getContextPath() + "/ta/apply?jobId=" + jobId + "&resumeIncomplete=true");
            return;
        }

        if ("pdf".equalsIgnoreCase(resumeMode) && !filled(pdfPath)) {
            response.sendRedirect(request.getContextPath() + "/ta/apply?jobId=" + jobId + "&pdfRequired=true");
            return;
        }

        if (!uploadError.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ta/apply?jobId=" + jobId + "&" + uploadError);
            return;
        }

        List<Application> existing = CSVService.readApplications();
        if (!canSubmitNewApplication(existing, user.getId())) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?limitOne=true");
            return;
        }

        boolean alreadyApplied = existing.stream().anyMatch(a -> a.getStudentId().equals(user.getId()) && a.getPositionId().equals(jobId) && !"rejected".equalsIgnoreCase(a.getStatus()));
        if (alreadyApplied) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?already=true");
            return;
        }

        String appId = UUID.randomUUID().toString();
        String createdAt = LocalDateTime.now().toString();

        Application app = new Application(appId, user.getId(), jobId, "pending", "", createdAt);
        app.setResumePdfPath(pdfPath);
        app.setInterviewConfirmStatus("not_sent");
        CSVService.saveApplication(app);

        if ("online".equalsIgnoreCase(resumeMode)) {
            CSVService.saveApplicationResume(appId, resumeInput);
            CSVService.saveResume(resumeInput);
        }

        CSVService.deleteApplicationDraft(user.getId(), jobId);

        System.out.println("Application saved: " + appId + " for user " + user.getId() + " position " + jobId);

        response.sendRedirect(request.getContextPath() + "/ta/apps?success=true");
    }

    private boolean canSubmitNewApplication(List<Application> allApps, String studentId) {
        List<Application> mine = allApps.stream()
                .filter(a -> studentId.equals(a.getStudentId()))
                .collect(java.util.stream.Collectors.toList());

        boolean hasActive = mine.stream().anyMatch(a -> !"rejected".equalsIgnoreCase(a.getStatus()));
        if (hasActive) {
            return false;
        }

        long rejectedCount = mine.stream().filter(a -> "rejected".equalsIgnoreCase(a.getStatus())).count();
        return rejectedCount < 2;
    }

    private String saveResumePdf(HttpServletRequest request, String studentId, String appId) throws IOException, ServletException {
        Part pdfPart;
        try {
            pdfPart = request.getPart("resumePdf");
        } catch (IllegalStateException ex) {
            throw new ServletException("Uploaded file exceeds 10MB limit", ex);
        }

        if (pdfPart == null || pdfPart.getSize() <= 0) {
            return "";
        }

        String filename = extractSubmittedFileName(pdfPart);
        String lowerName = filename == null ? "" : filename.toLowerCase();
        String contentType = pdfPart.getContentType() == null ? "" : pdfPart.getContentType().toLowerCase();
        if (!lowerName.endsWith(".pdf") && !"application/pdf".equals(contentType)) {
            throw new ServletException("Only PDF resume is supported");
        }

        if (pdfPart.getSize() > 10L * 1024L * 1024L) {
            throw new ServletException("PDF size cannot exceed 10MB");
        }

        File uploadDir = new File(CSVService.getDataDirectory(), "resume_uploads");
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("Failed to create resume upload directory");
        }

        String storedName = studentId + "_" + appId + ".pdf";
        File storedFile = new File(uploadDir, storedName);
        pdfPart.write(storedFile.getAbsolutePath());
        // Store only the filename to avoid Windows path escaping issues in CSV.
        return storedName;
    }

    private boolean isCompleteResumeInput(Resume r) {
        return filled(r.getName())
                && filled(r.getEmail())
                && filled(r.getMajor())
                && filled(r.getGrade())
                && filled(r.getGpa())
                && filled(r.getEnglishScore())
                && filled(r.getSkills())
                && filled(r.getRelatedCourses())
                && filled(r.getAwards())
                && filled(r.getProjects())
                && filled(r.getExperience())
                && filled(r.getCompetency())
                && filled(r.getWorkHours());
    }

    private boolean filled(String v) {
        return v != null && !v.trim().isEmpty();
    }

    private String extractSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return "";
        }

        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.startsWith("filename=")) {
                String filename = trimmed.substring("filename=".length()).trim().replace("\"", "");
                int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
                return slash >= 0 ? filename.substring(slash + 1) : filename;
            }
        }
        return "";
    }
}
