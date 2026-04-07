package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Resume;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class TaApplyServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"ta".equals(user.getRole())) {
            response.sendError(403);
            return;
        }

        String jobId = request.getParameter("jobId");
        if (jobId == null || jobId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs");
            return;
        }

        List<Application> existing = CSVService.readApplications();
        boolean alreadyApplied = existing.stream().anyMatch(a -> a.getStudentId().equals(user.getId()) && a.getPositionId().equals(jobId));
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
        request.getRequestDispatcher("/jsp/ta-apply-resume.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"ta".equals(user.getRole())) {
            response.sendError(403);
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

        if (!isCompleteResumeInput(resumeInput)) {
            response.sendRedirect(request.getContextPath() + "/ta/apply?jobId=" + jobId + "&resumeIncomplete=true");
            return;
        }

        List<Application> existing = CSVService.readApplications();
        boolean alreadyApplied = existing.stream().anyMatch(a -> a.getStudentId().equals(user.getId()) && a.getPositionId().equals(jobId));
        if (alreadyApplied) {
            response.sendRedirect(request.getContextPath() + "/ta/jobs?already=true");
            return;
        }

        String appId = UUID.randomUUID().toString();
        String createdAt = LocalDateTime.now().toString();

        Application app = new Application(appId, user.getId(), jobId, "pending", "", createdAt);
        CSVService.saveApplication(app);
        CSVService.saveApplicationResume(appId, resumeInput);

        // Optional: keep the latest profile resume synchronized for TA profile page
        CSVService.saveResume(resumeInput);

        System.out.println("Application saved: " + appId + " for user " + user.getId() + " position " + jobId);

        response.sendRedirect(request.getContextPath() + "/ta/apps?success=true");
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
}
