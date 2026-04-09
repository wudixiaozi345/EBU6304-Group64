package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TaEmailVerifyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Map<String, String> pending = session == null ? null : (Map<String, String>) session.getAttribute("pendingTaRegister");
        if (pending == null) {
            response.sendRedirect(request.getContextPath() + "/register.jsp");
            return;
        }

        request.setAttribute("pendingEmail", pending.get("email"));
        request.setAttribute("demoCode", pending.get("code"));
        request.getRequestDispatcher("/jsp/register-verify.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Map<String, String> pending = session == null ? null : (Map<String, String>) session.getAttribute("pendingTaRegister");
        if (pending == null) {
            response.sendRedirect(request.getContextPath() + "/register.jsp");
            return;
        }

        String code = request.getParameter("code");
        if (code == null || !code.trim().equals(pending.get("code"))) {
            request.setAttribute("pendingEmail", pending.get("email"));
            request.setAttribute("demoCode", pending.get("code"));
            request.setAttribute("error", "Invalid verification code");
            request.getRequestDispatcher("/jsp/register-verify.jsp").forward(request, response);
            return;
        }

        List<User> users = CSVService.readTaUsers();
        users.add(new User(
                pending.get("id"),
                pending.get("name"),
                pending.get("email"),
                pending.get("password"),
                "ta",
                "pending_profile"
        ));
        CSVService.writeTaUsers(users);

        session.removeAttribute("pendingTaRegister");
        response.sendRedirect(request.getContextPath() + "/login.jsp?registered=true&needProfile=true");
    }
}
