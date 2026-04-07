package com.bupt.recruit.servlet;

import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class LoginServlet extends HttpServlet {
    private static final Map<String, Integer> failedLoginCount = new HashMap<>();
    private static final Map<String, Long> accountLockUntil = new HashMap<>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 编码设置
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取前端参数
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // 日志打印
        System.out.println("=== 登录参数打印 ===");
        System.out.println("前端传的 role: " + role);
        System.out.println("前端传的 id: " + id);
        System.out.println("前端传的 password: " + password);

        // 根据角色读取账号
        List<User> users;
        switch (role) {
            case "admin":
                users = CSVService.readAdminUsers();
                break;
            case "mo":
                users = CSVService.readMoUsers();
                break;
            case "ta":
                users = CSVService.readTaUsers();
                break;
            default:
                users = new ArrayList<>();
                break;
        }

        // 打印读到的账号数量
        System.out.println("读到的账号数量：" + users.size());

        Properties config = CSVService.readAdminConfig();
        int maxFail = Integer.parseInt(config.getProperty("login.fail.limit", "3"));
        int lockMinutes = Integer.parseInt(config.getProperty("login.lock.minutes", "5"));

        long now = System.currentTimeMillis();
        Long lockEnd = accountLockUntil.get(id);
        if (lockEnd != null && now < lockEnd) {
            long remaining = (lockEnd - now) / 1000;
            request.setAttribute("error", "Account locked. Please wait " + remaining + " seconds.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // 验证账号密码
        User user = users.stream()
                .filter(u -> u.getId().equals(id) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            if (!"admin".equals(role) && "disabled".equalsIgnoreCase(user.getStatus())) {
                request.setAttribute("error", "This account is disabled. Please contact administrator.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            System.out.println("登录成功！用户：" + user.getId());
            failedLoginCount.remove(id);
            accountLockUntil.remove(id);

            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // 按角色跳转到对应页面（使用存在的 URL 映射避免 404）
            String ctx = request.getContextPath();
            if ("admin".equals(role)) {
                response.sendRedirect(ctx + "/dashboard");
            } else if ("ta".equals(role)) {
                response.sendRedirect(ctx + "/dashboard");
            } else if ("mo".equals(role)) {
                response.sendRedirect(ctx + "/dashboard");
            } else {
                response.sendRedirect(ctx + "/dashboard");
            }
            return;
        }

        int fails = failedLoginCount.getOrDefault(id, 0) + 1;
        failedLoginCount.put(id, fails);
        if (fails >= maxFail) {
            accountLockUntil.put(id, now + lockMinutes * 60 * 1000L);
            failedLoginCount.remove(id);
            request.setAttribute("error", "Too many failed attempts. Account locked for " + lockMinutes + " minutes.");
        } else {
            request.setAttribute("error", "Invalid ID, password, or role. " + (maxFail - fails) + " attempts remaining.");
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
