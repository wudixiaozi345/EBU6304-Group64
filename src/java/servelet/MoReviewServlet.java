package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MoReviewServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"mo".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Course> myCourses = CSVService.readCourses().stream()
                .filter(c -> user.getId().equals(c.getMoId()))
                .collect(Collectors.toList());

        Set<String> myCourseIds = myCourses.stream().map(Course::getId).collect(Collectors.toSet());

        List<Position> positions = CSVService.readPositions().stream()
                .filter(p -> myCourseIds.contains(p.getCourseId()))
                .collect(Collectors.toList());

        Map<String, String> courseNameMap = myCourses.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Course::getId, c -> Objects.toString(c.getName(), "Unknown"), (a, b) -> a));

        for (Position p : positions) {
            p.setCourseName(courseNameMap.getOrDefault(p.getCourseId(), "Unknown"));
        }

        Map<String, String> posTitle = positions.stream()
                .filter(p -> p.getId() != null)
                .collect(
                        Collectors.toMap(Position::getId, p -> Objects.toString(p.getTitle(), "Unknown"), (a, b) -> a));

        String statusFilter = Optional.ofNullable(request.getParameter("status")).orElse("all").toLowerCase();
        String sortOrder = Optional.ofNullable(request.getParameter("sort")).orElse("desc").toLowerCase();
        String sortBy = Optional.ofNullable(request.getParameter("sortBy")).orElse("time").toLowerCase();

        List<Application> apps = CSVService.readApplications().stream()
                .filter(a -> positions.stream().anyMatch(p -> Objects.equals(p.getId(), a.getPositionId())))
                .peek(a -> a.setStatus(a.getStatus() == null || a.getStatus().isEmpty() ? "pending" : a.getStatus()))
                .filter(a -> "all".equals(statusFilter) || statusFilter.equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        for (Application app : apps) {
            app.setPositionTitle(posTitle.getOrDefault(app.getPositionId(), "Unknown"));
            String courseName = positions.stream()
                    .filter(p -> Objects.equals(p.getId(), app.getPositionId()))
                    .map(Position::getCourseName)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("Unknown");
            app.setCourseName(courseName);

            String courseId = positions.stream()
                    .filter(p -> Objects.equals(p.getId(), app.getPositionId()))
                    .map(Position::getCourseId)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("");
            app.setCourseId(courseId);

            // Load resume snapshot for this specific application, fallback to latest TA
            // profile resume
            app.setResume(Optional.ofNullable(CSVService.getApplicationResume(app.getId()))
                    .orElse(CSVService.getResume(app.getStudentId())));
        }

        Comparator<Application> comparator;
        if ("gpa".equals(sortBy)) {
            comparator = Comparator.comparing(this::parseGpaSafe);
        } else if ("major".equals(sortBy)) {
            comparator = Comparator.comparing(a -> {
                if (a.getResume() == null || a.getResume().getMajor() == null)
                    return "";
                return a.getResume().getMajor().toLowerCase();
            });
        } else {
            comparator = Comparator.comparing(a -> a.getCreatedAt() == null ? "" : a.getCreatedAt());
        }

        if (!"asc".equals(sortOrder)) {
            comparator = comparator.reversed();
        }
        apps.sort(comparator);

        System.out.println("[MoReviewServlet] user=" + user.getId() + ", myCourses=" + myCourses.size() + ", positions="
                + positions.size() + ", appsFound=" + apps.size());

        long pendingCount = CSVService.readApplications().stream()
                .filter(a -> positions.stream().anyMatch(p -> Objects.equals(p.getId(), a.getPositionId())))
                .map(a -> Optional.ofNullable(a.getStatus()).orElse("pending"))
                .filter(s -> "pending".equalsIgnoreCase(s)).count();
        long acceptedCount = CSVService.readApplications().stream()
                .filter(a -> positions.stream().anyMatch(p -> Objects.equals(p.getId(), a.getPositionId())))
                .map(a -> Optional.ofNullable(a.getStatus()).orElse("pending"))
                .filter(s -> "accepted".equalsIgnoreCase(s)).count();
        long rejectedCount = CSVService.readApplications().stream()
                .filter(a -> positions.stream().anyMatch(p -> Objects.equals(p.getId(), a.getPositionId())))
                .map(a -> Optional.ofNullable(a.getStatus()).orElse("pending"))
                .filter(s -> "rejected".equalsIgnoreCase(s)).count();
        long waitlistCount = CSVService.readApplications().stream()
                .filter(a -> positions.stream().anyMatch(p -> Objects.equals(p.getId(), a.getPositionId())))
                .map(a -> Optional.ofNullable(a.getStatus()).orElse("pending"))
                .filter(s -> "waitlist".equalsIgnoreCase(s)).count();

        request.setAttribute("apps", apps);
        request.setAttribute("totalCount", apps.size());
        request.setAttribute("filterStatus", statusFilter);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("acceptedCount", acceptedCount);
        request.setAttribute("rejectedCount", rejectedCount);
        request.setAttribute("waitlistCount", waitlistCount);
        request.getRequestDispatcher("/jsp/mo-review.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"mo".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String appId = request.getParameter("appId");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");

        if (appId == null || appId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/mo/review?error=invalidApp");
            return;
        }

        List<Course> myCourses = CSVService.readCourses().stream()
                .filter(c -> user.getId().equals(c.getMoId()))
                .collect(Collectors.toList());
        Set<String> myCourseIds = myCourses.stream().map(Course::getId).collect(Collectors.toSet());
        List<Position> myPositions = CSVService.readPositions().stream()
                .filter(p -> myCourseIds.contains(p.getCourseId()))
                .collect(Collectors.toList());

        Application target = CSVService.readApplications().stream()
                .filter(a -> appId.equals(a.getId()))
                .findFirst()
                .orElse(null);

        if (target == null || myPositions.stream().noneMatch(p -> Objects.equals(p.getId(), target.getPositionId()))) {
            response.sendRedirect(request.getContextPath() + "/mo/review?error=forbidden");
            return;
        }

        if ("accepted".equalsIgnoreCase(status)) {
            Position targetPos = myPositions.stream()
                    .filter(p -> Objects.equals(p.getId(), target.getPositionId()))
                    .findFirst()
                    .orElse(null);
            int vacancies = parseVacancies(targetPos == null ? "" : targetPos.getVacancies());
            long acceptedCount = CSVService.readApplications().stream()
                    .filter(a -> Objects.equals(a.getPositionId(), target.getPositionId()))
                    .filter(a -> "accepted".equalsIgnoreCase(a.getStatus()))
                    .count();

            boolean alreadyAccepted = "accepted".equalsIgnoreCase(target.getStatus());
            if (!alreadyAccepted && acceptedCount >= vacancies) {
                response.sendRedirect(request.getContextPath() + "/mo/review?error=vacanciesExceeded");
                return;
            }
        }

        if ("rejected".equalsIgnoreCase(status)) {
            if (reason == null || reason.trim().length() < 10) {
                response.sendRedirect(request.getContextPath() + "/mo/review?error=reasonTooShort");
                return;
            }
        }

        CSVService.updateApplicationStatus(appId, status, reason);
        response.sendRedirect(request.getContextPath() + "/mo/review?success=true");
    }

    private int parseVacancies(String vacancies) {
        try {
            int parsed = Integer.parseInt(vacancies == null ? "0" : vacancies.trim());
            return Math.max(parsed, 0);
        } catch (Exception ex) {
            return 0;
        }
    }

    private double parseGpaSafe(Application app) {
        try {
            if (app.getResume() == null || app.getResume().getGpa() == null) {
                return -1.0;
            }
            return Double.parseDouble(app.getResume().getGpa().trim());
        } catch (Exception ex) {
            return -1.0;
        }
    }
}
