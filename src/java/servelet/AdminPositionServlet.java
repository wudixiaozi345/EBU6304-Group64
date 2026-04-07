package com.bupt.recruit.servlet;

import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminPositionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Position> positions = CSVService.readPositions();
        List<Application> apps = CSVService.readApplications();

        long totalPositions = positions.size();
        Set<String> appliedPositionIds = apps.stream().map(Application::getPositionId).collect(Collectors.toSet());
        long appliedPositions = appliedPositionIds.size();
        long pendingApplications = apps.stream().filter(a -> "pending".equalsIgnoreCase(a.getStatus())).count();

        request.setAttribute("positions", positions);
        request.setAttribute("totalPositions", totalPositions);
        request.setAttribute("appliedPositions", appliedPositions);
        request.setAttribute("pendingApplications", pendingApplications);

        request.getRequestDispatcher("/jsp/admin-positions.jsp").forward(request, response);
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
        if ("togglePosition".equals(action)) {
            String positionId = request.getParameter("positionId");
            List<Position> positions = CSVService.readPositions();
            for (Position p : positions) {
                if (p.getId().equals(positionId)) {
                    p.setStatus("open".equalsIgnoreCase(p.getStatus()) ? "closed" : "open");
                    break;
                }
            }
            CSVService.writePositions(positions);
            response.sendRedirect(request.getContextPath() + "/admin/positions?success=statusUpdated");
            return;
        }

        if ("deletePosition".equals(action)) {
            String positionId = request.getParameter("positionId");
            if (positionId == null || positionId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/positions?error=invalidPosition");
                return;
            }

            CSVService.deleteApplicationsByPositionId(positionId);
            CSVService.deletePosition(positionId);
            response.sendRedirect(request.getContextPath() + "/admin/positions?success=deleted");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/positions");
    }
}
