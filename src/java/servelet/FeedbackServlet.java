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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FeedbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || (!"ta".equals(user.getRole()) && !"mo".equals(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Feedback> myFeedbacks = CSVService.readFeedbacks().stream()
                .filter(f -> user.getRole().equalsIgnoreCase(f.getFromRole()))
                .filter(f -> user.getId().equals(f.getFromUserId()))
                .sorted((a, b) -> safe(b.getCreatedAt()).compareTo(safe(a.getCreatedAt())))
                .collect(Collectors.toList());

        request.setAttribute("feedbacks", myFeedbacks);
        request.getRequestDispatcher("/jsp/feedback.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || (!"ta".equals(user.getRole()) && !"mo".equals(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String title = trim(request.getParameter("title"));
        String content = trim(request.getParameter("content"));
        if (title.isEmpty() || content.isEmpty()) {
            response.sendRedirect(request.getRequestURI() + "?error=missing");
            return;
        }

        Feedback feedback = new Feedback(
                UUID.randomUUID().toString(),
                user.getRole(),
                user.getId(),
                title,
                content,
            "pending",
                "",
                LocalDateTime.now().toString()
        );
        CSVService.saveFeedback(feedback);
        response.sendRedirect(request.getRequestURI() + "?success=created");
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
