package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class TaProfileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        if (!"ta".equalsIgnoreCase(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        request.getRequestDispatcher("/jsp/ta-profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"ta".equalsIgnoreCase(user.getRole())) {
            response.sendError(403);
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || !email.toLowerCase().endsWith("@qmul.ac.uk")) {
            response.sendRedirect(request.getContextPath() + "/ta/profile?error=email");
            return;
        }

        if (password != null && !password.trim().isEmpty() && !isStrongPassword(password)) {
            response.sendRedirect(request.getContextPath() + "/ta/profile?error=password");
            return;
        }

        List<User> users = CSVService.readTaUsers();
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                u.setName(name);
                u.setEmail(email);
                if (password != null && !password.isEmpty()) {
                    u.setPassword(password);
                }
                u.setStatus("active");
                session.setAttribute("user", u);
                break;
            }
        }
        CSVService.writeTaUsers(users);
        response.sendRedirect(request.getContextPath() + "/ta/profile?success=true");
    }

    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        return hasUpper && hasLower && hasDigit;
    }
}