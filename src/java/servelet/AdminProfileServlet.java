package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class AdminProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.setAttribute("admin", user);
        request.getRequestDispatcher("/jsp/admin-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        if (!"admin".equals(sessionUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        List<User> admins = CSVService.readAdminUsers();
        for (User admin : admins) {
            if (admin.getId().equals(sessionUser.getId())) {
                if (name != null && !name.trim().isEmpty()) {
                    admin.setName(name.trim());
                }
                if (email != null && !email.trim().isEmpty()) {
                    admin.setEmail(email.trim());
                }
                if (password != null && !password.trim().isEmpty()) {
                    admin.setPassword(password.trim());
                }
                sessionUser.setName(admin.getName());
                sessionUser.setEmail(admin.getEmail());
                if (password != null && !password.trim().isEmpty()) {
                    sessionUser.setPassword(admin.getPassword());
                }
                break;
            }
        }
        CSVService.writeAdminUsers(admins);

        session.setAttribute("user", sessionUser);
        request.setAttribute("admin", sessionUser);
        request.setAttribute("message", "Profile updated successfully.");
        request.getRequestDispatcher("/jsp/admin-profile.jsp").forward(request, response);
    }
}
