package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.Application;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setAttribute("user", user);
        if ("mo".equals(user.getRole())) {
            List<Course> myCourses = CSVService.readCourses().stream()
                    .filter(c -> user.getId().equals(c.getMoId()))
                    .collect(Collectors.toList());

            Set<String> myCourseIds = myCourses.stream().map(Course::getId).collect(Collectors.toSet());
            List<Position> myPositions = CSVService.readPositions().stream()
                    .filter(p -> user.getId().equals(p.getMoId()) || myCourseIds.contains(p.getCourseId()))
                    .collect(Collectors.toList());

            Set<String> myPositionIds = myPositions.stream().map(Position::getId).collect(Collectors.toSet());
            List<Application> relatedApps = CSVService.readApplications().stream()
                    .filter(a -> myPositionIds.contains(a.getPositionId()))
                    .collect(Collectors.toList());

            long pendingCount = relatedApps.stream().filter(app -> "pending".equalsIgnoreCase(app.getStatus())).count();
            long acceptedCount = relatedApps.stream().filter(app -> "accepted".equalsIgnoreCase(app.getStatus())).count();
            long rejectedCount = relatedApps.stream().filter(app -> "rejected".equalsIgnoreCase(app.getStatus())).count();

            request.setAttribute("moTotalCourses", myCourses.size());
            request.setAttribute("moTotalPositions", myPositions.size());
            request.setAttribute("moPendingReviews", pendingCount);
            request.setAttribute("moAccepted", acceptedCount);
            request.setAttribute("moRejected", rejectedCount);
        }

        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }
}
