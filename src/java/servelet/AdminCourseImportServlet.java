package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@MultipartConfig
public class AdminCourseImportServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("file");
        if (filePart != null) {
            InputStream fileContent = filePart.getInputStream();
            // In a real app, parse the CSV and save to database
            // List<Course> courses = CSVService.parseCourses(fileContent);
            // CSVService.saveCourses(courses);
        }
        response.sendRedirect("../courses?success=true");
    }
}
