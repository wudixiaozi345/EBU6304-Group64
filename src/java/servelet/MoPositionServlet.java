package com.bupt.recruit.servlet;

import com.bupt.recruit.model.Course;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

public class MoPositionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"mo".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Course> myCourses = CSVService.readCourses().stream()
                .filter(c -> user.getId().equals(c.getMoId()))
            .sorted(Comparator
                .comparing((Course c) -> !"active".equalsIgnoreCase(c.getStatus()))
                .thenComparing(Course::getId))
                .collect(Collectors.toList());

        Set<String> myCourseIds = myCourses.stream().map(Course::getId).collect(Collectors.toSet());

        List<Position> myPositions = CSVService.readPositions().stream()
                .filter(p -> myCourseIds.contains(p.getCourseId()) || user.getId().equals(p.getMoId()))
                .collect(Collectors.toList());

        Map<String, String> courseNameMap = new HashMap<>();
        Map<String, String> courseStatusMap = new HashMap<>();
        for (Course c : myCourses) {
            courseNameMap.put(c.getId(), c.getName());
            courseStatusMap.put(c.getId(), c.getStatus());
        }

        for (Position p : myPositions) {
            p.setCourseName(courseNameMap.getOrDefault(p.getCourseId(), "Unknown"));
            if ("disabled".equalsIgnoreCase(courseStatusMap.get(p.getCourseId()))) {
                p.setStatus("closed");
            }
        }

        String editId = request.getParameter("editId");
        if (editId != null && !editId.isEmpty()) {
            myPositions.stream().filter(p -> p.getId().equals(editId)).findFirst().ifPresent(pos -> request.setAttribute("editPosition", pos));
        }

        request.setAttribute("courses", myCourses);
        request.setAttribute("positions", myPositions);
        request.getRequestDispatcher("/jsp/mo-positions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"mo".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            String courseId = request.getParameter("courseId");
            String title = request.getParameter("title");
            String requirements = request.getParameter("requirements");
            String deadline = request.getParameter("deadline");
            String vacancies = request.getParameter("vacancies");

            if (courseId == null || courseId.isEmpty() || title == null || title.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/positions?error=missing");
                return;
            }

            if (!isActiveCourseOwnedByMo(courseId, user.getId())) {
                response.sendRedirect(request.getContextPath() + "/mo/positions?error=courseDisabled");
                return;
            }

            Position position = new Position(UUID.randomUUID().toString(), title, courseId, requirements, "open", deadline, vacancies, user.getId());
            CSVService.addPosition(position);
            response.sendRedirect(request.getContextPath() + "/mo/positions?success=created");
            return;
        }

        if ("update".equals(action)) {
            String id = request.getParameter("id");
            String courseId = request.getParameter("courseId");
            String title = request.getParameter("title");
            String requirements = request.getParameter("requirements");
            String deadline = request.getParameter("deadline");
            String vacancies = request.getParameter("vacancies");

            if (id == null || id.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/mo/positions?error=missingId");
                return;
            }

            if (courseId == null || courseId.isEmpty() || !isActiveCourseOwnedByMo(courseId, user.getId())) {
                response.sendRedirect(request.getContextPath() + "/mo/positions?error=courseDisabled");
                return;
            }

            List<Position> allPositions = CSVService.readPositions();
            boolean updated = false;
            for (Position p : allPositions) {
                if (id.equals(p.getId()) && user.getId().equals(p.getMoId())) {
                    if (!"closed".equalsIgnoreCase(p.getStatus())) {
                        response.sendRedirect(request.getContextPath() + "/mo/positions?error=editClosedOnly");
                        return;
                    }
                    p.setTitle(title);
                    p.setCourseId(courseId);
                    p.setRequirements(requirements);
                    p.setDeadline(deadline);
                    p.setVacancies(vacancies);
                    p.setMoId(user.getId());
                    CSVService.updatePosition(p);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                response.sendRedirect(request.getContextPath() + "/mo/positions?error=notfound");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/mo/positions?success=updated");
            return;
        }

        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            if (id != null && !id.isEmpty()) {
                List<Position> allPositions = CSVService.readPositions();
                for (Position p : allPositions) {
                    if (id.equals(p.getId()) && user.getId().equals(p.getMoId())) {
                        if (!"closed".equalsIgnoreCase(p.getStatus())) {
                            response.sendRedirect(request.getContextPath() + "/mo/positions?error=editClosedOnly");
                            return;
                        }
                        CSVService.deletePosition(id);
                        response.sendRedirect(request.getContextPath() + "/mo/positions?success=deleted");
                        return;
                    }
                }
                response.sendRedirect(request.getContextPath() + "/mo/positions?error=notfound");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/mo/positions?error=missingId");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/mo/positions");
    }

    private boolean isActiveCourseOwnedByMo(String courseId, String moId) {
        return CSVService.readCourses().stream()
                .anyMatch(c -> courseId.equals(c.getId())
                        && moId.equals(c.getMoId())
                        && "active".equalsIgnoreCase(c.getStatus()));
    }
}
