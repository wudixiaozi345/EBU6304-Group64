package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class AdminMoStatusServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        List<User> users = CSVService.readMoUsers();
        for (User u : users) {
            if (u.getId().equals(id)) {
                u.setStatus("active".equals(u.getStatus()) ? "disabled" : "active");
                break;
            }
        }
        CSVService.writeMoUsers(users);
        response.sendRedirect("../mos");
    }
}
