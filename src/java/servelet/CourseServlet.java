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


public class CourseServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"mo".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        // In a real app, we'd have a readCourses method in CSVService
        // For this demo, we'll assume it exists
        // List<Course> courses = CSVService.readCourses();
        // List<Course> myCourses = courses.stream()
        //         .filter(c -> c.getMoStaffId().equals(user.getId()))
        //         .collect(Collectors.toList());
        // request.setAttribute("courses", myCourses);
        request.getRequestDispatcher("/jsp/mo-courses.jsp").forward(request, response);
    }
}
