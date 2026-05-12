package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class TaRegisterServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (id == null || id.trim().isEmpty() || password == null || password.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Student ID, QMUL email and password are required");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!email.toLowerCase().endsWith("@qmul.ac.uk")) {
            request.setAttribute("error", "Email must end with @qmul.ac.uk");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!isStrongPassword(password)) {
            request.setAttribute("error", "Password must contain uppercase, lowercase and number");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Password and confirm password do not match");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        List<User> users = CSVService.readTaUsers();
        boolean exists = users.stream().anyMatch(u -> u.getId().equals(id) || (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)));
        if (exists) {
            request.setAttribute("error", "Student ID or email already registered");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        String verifyCode = String.format("%06d", new Random().nextInt(1000000));
        Map<String, String> pending = new HashMap<>();
        pending.put("id", id.trim());
        pending.put("name", name == null ? "" : name.trim());
        pending.put("email", email.trim());
        pending.put("password", password);
        pending.put("code", verifyCode);

        HttpSession session = request.getSession();
        session.setAttribute("pendingTaRegister", pending);
        response.sendRedirect(request.getContextPath() + "/register/verify-email");
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
