package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

public class AdminSettingsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Properties config = CSVService.readAdminConfig();
        request.setAttribute("config", config);
        request.getRequestDispatcher("/jsp/admin-settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String deadlineFormat = request.getParameter("deadlineFormat");
        String failLimit = request.getParameter("failLimit");
        String lockMinutes = request.getParameter("lockMinutes");

        Properties config = CSVService.readAdminConfig();
        if (deadlineFormat != null && !deadlineFormat.trim().isEmpty()) {
            config.setProperty("application.deadline.format", deadlineFormat.trim());
        }
        if (failLimit != null && !failLimit.trim().isEmpty()) {
            config.setProperty("login.fail.limit", failLimit.trim());
        }
        if (lockMinutes != null && !lockMinutes.trim().isEmpty()) {
            config.setProperty("login.lock.minutes", lockMinutes.trim());
        }

        CSVService.saveAdminConfig(config);

        response.sendRedirect(request.getContextPath() + "/admin/settings");
    }
}
