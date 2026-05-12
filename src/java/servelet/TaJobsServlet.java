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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        if (!CSVService.isTaProfileCompleted(user.getId())) {
            response.sendRedirect(request.getContextPath() + "/ta/profile?mustComplete=true");
            return;
        }

        String search = request.getParameter("search");
        String courseName = request.getParameter("courseName");
        String minVacanciesRaw = request.getParameter("minVacancies");
        String sortBy = request.getParameter("sortBy");
        String onlyFavorites = request.getParameter("onlyFavorites");

        List<Course> courses = CSVService.readCourses();
        Map<String, Course> courseMap = courses.stream().collect(Collectors.toMap(Course::getId, c -> c));
        Map<String, String> moNameMap = CSVService.readMoUsers().stream()
            .collect(Collectors.toMap(User::getId, User::getName, (a, b) -> a));

        List<Position> allPositions = CSVService.readPositions();

        Map<String, String> courseNames = courses.stream().collect(Collectors.toMap(Course::getId, Course::getName));
        allPositions.forEach(p -> p.setCourseName(courseNames.getOrDefault(p.getCourseId(), "Unknown")));

        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            allPositions = allPositions.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(q) || p.getCourseName().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (courseName != null && !courseName.trim().isEmpty()) {
            String q = courseName.trim().toLowerCase();
            allPositions = allPositions.stream()
                .filter(p -> p.getCourseName() != null && p.getCourseName().toLowerCase().contains(q))
                .collect(Collectors.toList());
        }

        int minVacancies = parseIntSafe(minVacanciesRaw, -1);
        if (minVacancies >= 0) {
            allPositions = allPositions.stream()
                .filter(p -> parseIntSafe(p.getVacancies(), 0) >= minVacancies)
                .collect(Collectors.toList());
        }

        List<Application> userApps = CSVService.readApplications();
        List<String> appliedJobIds = new ArrayList<>();

        for (Application app : userApps) {
            if (user.getId().equals(app.getStudentId()) && !"rejected".equalsIgnoreCase(app.getStatus())) {
                appliedJobIds.add(app.getPositionId());
            }
        }

        long rejectedCount = userApps.stream()
                .filter(a -> user.getId().equals(a.getStudentId()))
                .filter(a -> "rejected".equalsIgnoreCase(a.getStatus()))
                .count();

        Set<String> favoritePositionIds = getFavoritePositionIds(session);
        Map<String, Boolean> favoritePositionMap = favoritePositionIds.stream()
            .collect(Collectors.toMap(id -> id, id -> Boolean.TRUE, (a, b) -> a));
        boolean filterFavorites = "true".equalsIgnoreCase(onlyFavorites);
        if (filterFavorites) {
            allPositions = allPositions.stream()
                    .filter(p -> favoritePositionIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        String normalizedSort = (sortBy == null || sortBy.trim().isEmpty()) ? "deadlineAsc" : sortBy;
        if ("deadlineDesc".equalsIgnoreCase(normalizedSort)) {
            allPositions.sort(Comparator.comparing(Position::getDeadline, Comparator.nullsLast(String::compareTo)).reversed());
        } else {
            allPositions.sort(Comparator.comparing(Position::getDeadline, Comparator.nullsLast(String::compareTo)));
            normalizedSort = "deadlineAsc";
        }

        boolean hasAppliedAny = !appliedJobIds.isEmpty();
        boolean reapplyExceeded = !hasAppliedAny && rejectedCount >= 2;

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

            if (!pos.isApplied() && hasAppliedAny) {
                pos.setApplyDisabledReason("You can only apply to one position.");
                continue;
            }

            if (!pos.isApplied() && reapplyExceeded) {
                pos.setApplyDisabledReason("You have used all re-apply chances after rejection.");
                continue;
            }

            pos.setApplyDisabledReason("");
        }
        request.setAttribute("jobs", allPositions);
        request.setAttribute("favoritePositionIds", favoritePositionIds);
        request.setAttribute("favoritePositionMap", favoritePositionMap);
        request.setAttribute("moNameMap", moNameMap);
        request.setAttribute("sortBy", normalizedSort);
        request.setAttribute("hasAppliedAny", hasAppliedAny);
        request.setAttribute("reapplyExceeded", reapplyExceeded);

        // Forward to JSP
        request.getRequestDispatcher("/jsp/ta-jobs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"ta".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("toggleFavorite".equals(action)) {
            String positionId = request.getParameter("positionId");
            if (positionId != null && !positionId.trim().isEmpty()) {
                Set<String> favorites = getFavoritePositionIds(session);
                if (favorites.contains(positionId)) {
                    favorites.remove(positionId);
                } else {
                    favorites.add(positionId);
                }
                session.setAttribute("favoritePositionIds", favorites);
            }
        }

        response.sendRedirect(request.getContextPath() + "/ta/jobs");
    }

    @SuppressWarnings("unchecked")
    private Set<String> getFavoritePositionIds(HttpSession session) {
        Object obj = session.getAttribute("favoritePositionIds");
        if (obj instanceof Set) {
            return (Set<String>) obj;
        }
        Set<String> created = new HashSet<>();
        session.setAttribute("favoritePositionIds", created);
        return created;
    }

    private int parseIntSafe(String v, int defaultValue) {
        try {
            return Integer.parseInt(v == null ? "" : v.trim());
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}