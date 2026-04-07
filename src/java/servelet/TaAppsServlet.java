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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// ✅ 必须加！！！
@WebServlet("/ta-apps")
public class TaAppsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // ✅ 登录校验
        if (user == null || !"ta".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        List<Application> allApps = CSVService.readApplications();
        List<Application> userApps = allApps.stream()
                .filter(app -> user.getId().equals(app.getStudentId()))
                .collect(Collectors.toList());

        List<Position> positions = CSVService.readPositions();
        List<Course> courses = CSVService.readCourses();

        for (Application app : userApps) {
            for (Position pos : positions) {
                if (Objects.equals(pos.getId(), app.getPositionId())) {
                    app.setPositionTitle(pos.getTitle());
                    for (Course c : courses) {
                        if (Objects.equals(c.getId(), pos.getCourseId())) {
                            app.setCourseName(c.getName());
                            app.setCourseId(c.getId());
                        }
                    }
                }
            }
        }

        request.setAttribute("apps", userApps);
        request.getRequestDispatcher("/jsp/ta-apps.jsp").forward(request, response);
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
        String appId = request.getParameter("appId");

        if ("cancel".equals(action) && appId != null && !appId.isEmpty()) {
            CSVService.deleteApplication(appId);
            response.sendRedirect(request.getContextPath() + "/ta-apps?cancel=true");
            return;
        }

        doGet(request, response);
    }
}