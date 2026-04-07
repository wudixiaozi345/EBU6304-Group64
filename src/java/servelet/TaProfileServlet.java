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

        List<User> users = CSVService.readTaUsers();
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                u.setName(name);
                u.setEmail(email);
                if (password != null && !password.isEmpty()) {
                    u.setPassword(password);
                }
                session.setAttribute("user", u);
                break;
            }
        }
        CSVService.writeTaUsers(users);
        response.sendRedirect(request.getContextPath() + "/ta/profile?success=true");
    }
}