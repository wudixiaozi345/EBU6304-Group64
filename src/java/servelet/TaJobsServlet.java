package com.bupt.recruit.servlet;

import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Servlet mapping for TA jobs
@WebServlet("/ta-jobs")
public class TaJobsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Login and role check
        if (user == null || !"ta".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        String search = request.getParameter("search");

        List<Course> courses = CSVService.readCourses();
        Map<String, Course> courseMap = courses.stream().collect(Collectors.toMap(Course::getId, c -> c));

        List<Position> allPositions = CSVService.readPositions();

        Map<String, String> courseNames = courses.stream().collect(Collectors.toMap(Course::getId, Course::getName));
        allPositions.forEach(p -> p.setCourseName(courseNames.getOrDefault(p.getCourseId(), "Unknown")));

        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            allPositions = allPositions.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(q) || p.getCourseName().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        List<Application> userApps = CSVService.readApplications();
        List<String> appliedJobIds = new ArrayList<>();

        for (Application app : userApps) {
            if (user.getId().equals(app.getStudentId())) {
                appliedJobIds.add(app.getPositionId());
            }
        }

        // Resume completion status (for the frontend apply button)
        boolean resumeComplete = CSVService.isResumeComplete(user.getId());

        for (Position pos : allPositions) {
            pos.setApplied(appliedJobIds.contains(pos.getId()));
            Course course = courseMap.get(pos.getCourseId());

            if (pos.isApplied()) {
                pos.setApplyDisabledReason("You have already applied for this position.");
                continue;
            }

            if (course == null) {
                pos.setApplyDisabledReason("This course is unavailable.");
                continue;
            }

            if (!"active".equalsIgnoreCase(course.getStatus())) {
                pos.setApplyDisabledReason("This course is currently disabled by Admin.");
                continue;
            }

            if (!"open".equalsIgnoreCase(pos.getStatus())) {
                pos.setApplyDisabledReason("This position is currently closed by Admin.");
                continue;
            }

            if (!resumeComplete) {
                pos.setApplyDisabledReason("Complete your resume before applying.");
                continue;
            }

            pos.setApplyDisabledReason("");
        }
        request.setAttribute("resumeComplete", resumeComplete);
        request.setAttribute("jobs", allPositions);

        // Forward to JSP
        request.getRequestDispatcher("/jsp/ta-jobs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}