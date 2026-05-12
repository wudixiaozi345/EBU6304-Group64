package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class MoCourseServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"mo".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Course> courses = CSVService.readCourses().stream()
                .filter(c -> user.getId().equals(c.getMoId()))
                .collect(Collectors.toList());

        System.out.println("[MoCourseServlet] User=" + user.getId() + " found " + courses.size() + " courses.");
        for (Course c : courses) {
            System.out.println("   course=" + c.getId() + " moId=" + c.getMoId());
        }

        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/jsp/mo-courses.jsp").forward(request, response);
    }
}
