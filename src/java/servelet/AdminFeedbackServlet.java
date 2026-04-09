package com.bupt.recruit.servlet;

import com.bupt.recruit.model.Feedback;
import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminFeedbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String status = request.getParameter("status");
        if (status == null || status.trim().isEmpty()) {
            status = "all";
        }

        final String statusFilter = status;
        List<Feedback> all = CSVService.readFeedbacks().stream()
                .filter(f -> "all".equalsIgnoreCase(statusFilter) || statusFilter.equalsIgnoreCase(f.getStatus()))
                .sorted((a, b) -> safe(b.getCreatedAt()).compareTo(safe(a.getCreatedAt())))
                .collect(Collectors.toList());

        request.setAttribute("feedbacks", all);
        request.setAttribute("statusFilter", statusFilter);
        request.getRequestDispatcher("/jsp/admin-feedback.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String id = request.getParameter("id");
        String status = request.getParameter("status");
        String reply = request.getParameter("reply");
        CSVService.updateFeedback(id, status, reply);
        response.sendRedirect(request.getContextPath() + "/admin/feedback?success=updated");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
