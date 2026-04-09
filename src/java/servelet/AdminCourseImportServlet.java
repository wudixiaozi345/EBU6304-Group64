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
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@MultipartConfig
public class AdminCourseImportServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = trim(request.getParameter("action"));
        if ("confirm".equalsIgnoreCase(action)) {
            List<Course> previewCourses = (List<Course>) session.getAttribute("courseImportPreview");
            if (previewCourses == null || previewCourses.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/courses?error=import");
                return;
            }

            List<Course> all = CSVService.readCourses();
            int imported = 0;
            int duplicate = 0;
            for (Course c : previewCourses) {
                boolean exists = all.stream().anyMatch(old -> old.getId().equalsIgnoreCase(c.getId()));
                if (exists) {
                    duplicate++;
                } else {
                    all.add(c);
                    imported++;
                }
            }
            CSVService.writeCourses(all);
            session.removeAttribute("courseImportPreview");
            response.sendRedirect(request.getContextPath() + "/admin/courses?success=imported&imported=" + imported + "&duplicate=" + duplicate + "&invalid=0");
            return;
        }

        Part filePart = request.getPart("file");
        String semester = trim(request.getParameter("semester"));
        String moId = trim(request.getParameter("moId"));
        if (semester.isEmpty()) {
            semester = "TBD";
        }

        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?error=missing");
            return;
        }

        int imported = 0;
        int duplicate = 0;
        int invalid = 0;
        List<Course> allCourses = CSVService.readCourses();
        List<Course> toAdd = new ArrayList<>();

        try (InputStream fileContent = filePart.getInputStream(); Workbook workbook = new XSSFWorkbook(fileContent)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                response.sendRedirect(request.getContextPath() + "/admin/courses?error=import");
                return;
            }

            DataFormatter formatter = new DataFormatter();
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                String courseId = textOf(row.getCell(0), formatter);
                String name = textOf(row.getCell(1), formatter);
                String credits = textOf(row.getCell(2), formatter);

                if (courseId.isEmpty() && name.isEmpty() && credits.isEmpty()) {
                    continue;
                }

                if (courseId.isEmpty() || name.isEmpty() || credits.isEmpty()) {
                    invalid++;
                    continue;
                }

                boolean exists = allCourses.stream().anyMatch(c -> courseId.equalsIgnoreCase(c.getId()))
                        || toAdd.stream().anyMatch(c -> courseId.equalsIgnoreCase(c.getId()));
                if (exists) {
                    duplicate++;
                    continue;
                }

                toAdd.add(new Course(courseId, name, credits, moId, semester, "active"));
                imported++;
            }
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/admin/courses?error=import");
            return;
        }

        session.setAttribute("courseImportPreview", toAdd);
        request.setAttribute("previewRows", toAdd);
        request.setAttribute("previewImported", imported);
        request.setAttribute("previewDuplicate", duplicate);
        request.setAttribute("previewInvalid", invalid);
        request.setAttribute("courses", allCourses);
        request.setAttribute("moUsers", CSVService.readMoUsers());
        request.getRequestDispatcher("/jsp/admin-courses.jsp").forward(request, response);
    }

    private String textOf(Cell cell, DataFormatter formatter) {
        return cell == null ? "" : formatter.formatCellValue(cell).trim();
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
