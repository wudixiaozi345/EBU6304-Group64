package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminMoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<User> mos = CSVService.readMoUsers();
        List<User> tas = CSVService.readTaUsers();

        request.setAttribute("mos", mos);
        request.setAttribute("tas", tas);
        request.getRequestDispatcher("/jsp/admin-mos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("addMO".equals(action)) {
            String id = request.getParameter("id");
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if (password == null || password.trim().isEmpty()) {
                password = "mo123";
            }

            List<User> mos = CSVService.readMoUsers();
            boolean exists = mos.stream().anyMatch(m -> m.getId().equals(id));
            if (!exists) {
                mos.add(new User(id, name, email, password, "mo", "active"));
                CSVService.writeMoUsers(mos);
            }
            response.sendRedirect(request.getContextPath() + "/admin/mos");
            return;
        }

        if ("toggleStatus".equals(action)) {
            String role = request.getParameter("role");
            String id = request.getParameter("id");
            if ("mo".equals(role)) {
                List<User> mos = CSVService.readMoUsers();
                mos.stream().filter(m -> m.getId().equals(id)).findFirst().ifPresent(m -> {
                    m.setStatus("active".equalsIgnoreCase(m.getStatus()) ? "disabled" : "active");
                });
                CSVService.writeMoUsers(mos);
            } else if ("ta".equals(role)) {
                List<User> tas = CSVService.readTaUsers();
                tas.stream().filter(t -> t.getId().equals(id)).findFirst().ifPresent(t -> {
                    t.setStatus("active".equalsIgnoreCase(t.getStatus()) ? "disabled" : "active");
                });
                CSVService.writeTaUsers(tas);
            }
            response.sendRedirect(request.getContextPath() + "/admin/mos");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/mos");
    }
}
