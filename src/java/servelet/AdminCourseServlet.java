package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class AdminCourseServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Course> courses = CSVService.readCourses();
        List<User> moUsers = CSVService.readMoUsers();
        request.setAttribute("courses", courses);
        request.setAttribute("moUsers", moUsers);
        request.getRequestDispatcher("/jsp/admin-courses.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = trim(request.getParameter("action"));
        if ("toggleStatus".equals(action)) {
            String courseId = trim(request.getParameter("courseId"));
            List<Course> courses = CSVService.readCourses();
            boolean updated = false;
            boolean courseDisabled = false;
            boolean courseEnabled = false;

            for (Course c : courses) {
                if (courseId.equals(c.getId())) {
                    courseDisabled = "active".equalsIgnoreCase(c.getStatus());
                    courseEnabled = !courseDisabled;
                    c.setStatus(courseDisabled ? "disabled" : "active");
                    updated = true;
                    break;
                }
            }

            if (updated) {
                CSVService.writeCourses(courses);

                // If a course is disabled, close all related positions immediately.
                if (courseDisabled || courseEnabled) {
                    List<Position> positions = CSVService.readPositions();
                    boolean positionUpdated = false;
                    for (Position p : positions) {
                        if (courseId.equals(p.getCourseId())) {
                            if (courseDisabled && !"closed".equalsIgnoreCase(p.getStatus())) {
                                p.setStatus("closed");
                                positionUpdated = true;
                            }
                            if (courseEnabled && "closed".equalsIgnoreCase(p.getStatus())) {
                                p.setStatus("open");
                                positionUpdated = true;
                            }
                        }
                    }
                    if (positionUpdated) {
                        CSVService.writePositions(positions);
                    }
                }

                response.sendRedirect(request.getContextPath() + "/admin/courses?success=statusUpdated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/courses?error=notfound");
            }
            return;
        }

        if ("deleteCourse".equals(action)) {
            String courseId = trim(request.getParameter("courseId"));
            List<Course> courses = CSVService.readCourses();
            boolean removed = courses.removeIf(c -> courseId.equals(c.getId()));

            if (!removed) {
                response.sendRedirect(request.getContextPath() + "/admin/courses?error=notfound");
                return;
            }

            // Cascade delete: course -> positions -> applications + application resumes
            List<Position> positions = CSVService.readPositions();
            for (Position p : positions) {
                if (courseId.equals(p.getCourseId())) {
                    CSVService.deleteApplicationsByPositionId(p.getId());
                }
            }
            CSVService.deletePositionsByCourseId(courseId);
            CSVService.writeCourses(courses);

            response.sendRedirect(request.getContextPath() + "/admin/courses?success=deleted");
            return;
        }

        String courseId = trim(request.getParameter("courseId"));
        String name = trim(request.getParameter("name"));
        String credits = trim(request.getParameter("credits"));
        String moId = trim(request.getParameter("moId"));
        String semester = trim(request.getParameter("semester"));

        if (courseId.isEmpty() || name.isEmpty() || credits.isEmpty() || moId.isEmpty() || semester.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?error=missing");
            return;
        }

        if (CSVService.courseExists(courseId)) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?error=duplicate");
            return;
        }

        CSVService.addCourse(new Course(courseId, name, credits, moId, semester, "active"));
        response.sendRedirect(request.getContextPath() + "/admin/courses?success=created");
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
