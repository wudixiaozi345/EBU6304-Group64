package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Resume;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class ResumeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Resume resume = CSVService.getResume(user.getId());
        request.setAttribute("resume", resume);
        request.getRequestDispatcher("/jsp/ta-resume.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendError(403);
            return;
        }

        Resume resume = new Resume(
                user.getId(),
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("major"),
                request.getParameter("grade"),
                request.getParameter("gpa"),
                request.getParameter("englishScore"),
                request.getParameter("skills"),
                request.getParameter("relatedCourses"),
                request.getParameter("awards"),
                request.getParameter("projects"),
                request.getParameter("experience"),
                request.getParameter("competency"),
                request.getParameter("workHours")
        );

        CSVService.saveResume(resume);
        response.sendRedirect(request.getContextPath() + "/ta/resume?success=true");
    }
}