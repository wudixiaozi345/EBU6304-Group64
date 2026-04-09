package com.bupt.recruit.servlet;

import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminPositionExportServlet extends HttpServlet {

    private static class PositionStats {
        int total;
        int pending;
        int accepted;
        int rejected;
        int interview;
        int waitlist;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Position> positions = CSVService.readPositions();
        List<Application> applications = CSVService.readApplications();

        Map<String, PositionStats> statsByPosition = new HashMap<>();
        for (Application app : applications) {
            PositionStats stats = statsByPosition.computeIfAbsent(app.getPositionId(), k -> new PositionStats());
            stats.total++;

            String status = app.getStatus() == null ? "" : app.getStatus().toLowerCase(Locale.ROOT);
            if ("pending".equals(status)) {
                stats.pending++;
            } else if ("accepted".equals(status)) {
                stats.accepted++;
            } else if ("rejected".equals(status)) {
                stats.rejected++;
            } else if ("waitlist".equals(status)) {
                stats.waitlist++;
            } else if ("interview".equals(status) || "shortlisted".equals(status)) {
                stats.interview++;
            }
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String filename = "position-progress-" + timestamp + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Position Progress");

            String[] headers = new String[]{
                    "position_id", "title", "course_id", "status", "vacancies",
                    "total_applications", "pending", "accepted", "rejected", "waitlist", "interview"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;
            for (Position position : positions) {
                PositionStats stats = statsByPosition.getOrDefault(position.getId(), new PositionStats());
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(safe(position.getId()));
                row.createCell(1).setCellValue(safe(position.getTitle()));
                row.createCell(2).setCellValue(safe(position.getCourseId()));
                row.createCell(3).setCellValue(safe(position.getStatus()));
                row.createCell(4).setCellValue(safe(position.getVacancies()));
                row.createCell(5).setCellValue(stats.total);
                row.createCell(6).setCellValue(stats.pending);
                row.createCell(7).setCellValue(stats.accepted);
                row.createCell(8).setCellValue(stats.rejected);
                row.createCell(9).setCellValue(stats.waitlist);
                row.createCell(10).setCellValue(stats.interview);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
