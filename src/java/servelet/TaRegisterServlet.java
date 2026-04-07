package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class TaRegisterServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        List<User> users = CSVService.readUsers();
        boolean exists = users.stream().anyMatch(u -> u.getId().equals(id));
        if (exists) {
            request.setAttribute("error", "Student ID already registered");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        users.add(new User(id, name, email, password, "ta", "active"));
        CSVService.writeUsers(users);
        response.sendRedirect("login.jsp?registered=true");
    }
}
