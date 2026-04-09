package com.bupt.recruit.servlet;

import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.Resume;
import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;
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
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MoCandidateExportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || !"mo".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Set<String> myCourseIds = CSVService.readCourses().stream()
                .filter(c -> Objects.equals(user.getId(), c.getMoId()))
                .map(Course::getId)
                .collect(Collectors.toSet());

        List<Position> myPositions = CSVService.readPositions().stream()
                .filter(p -> myCourseIds.contains(p.getCourseId()))
                .collect(Collectors.toList());
        Set<String> myPositionIds = myPositions.stream().map(Position::getId).collect(Collectors.toSet());

        Map<String, Position> positionMap = myPositions.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Position::getId, p -> p, (a, b) -> a));
        Map<String, String> courseNameMap = CSVService.readCourses().stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Course::getId, c -> safe(c.getName()), (a, b) -> a));

        String statusFilter = Optional.ofNullable(request.getParameter("status")).orElse("all").toLowerCase();
        String sortOrder = Optional.ofNullable(request.getParameter("sort")).orElse("desc").toLowerCase();
        String sortBy = Optional.ofNullable(request.getParameter("sortBy")).orElse("time").toLowerCase();

        List<Application> apps = CSVService.readApplications().stream()
                .filter(a -> myPositionIds.contains(a.getPositionId()))
                .filter(a -> "all".equals(statusFilter)
                        || statusFilter.equalsIgnoreCase(Optional.ofNullable(a.getStatus()).orElse("pending")))
                .collect(Collectors.toList());

        Comparator<Application> comparator;
        if ("gpa".equals(sortBy)) {
            comparator = Comparator.comparing(a -> parseGpaSafe(CSVService.getApplicationResume(a.getId()), a.getStudentId()));
        } else if ("major".equals(sortBy)) {
            comparator = Comparator.comparing(a -> {
                Resume resume = CSVService.getApplicationResume(a.getId());
                if (resume == null) {
                    resume = CSVService.getResume(a.getStudentId());
                }
                return resume == null || resume.getMajor() == null ? "" : resume.getMajor().toLowerCase();
            });
        } else {
            comparator = Comparator.comparing(a -> Optional.ofNullable(a.getCreatedAt()).orElse(""));
        }
        if (!"asc".equals(sortOrder)) {
            comparator = comparator.reversed();
        }
        apps.sort(comparator);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "candidate-list-" + user.getId() + "-" + LocalDate.now() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replace("+", "%20"));

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Candidates");
            String[] headers = new String[]{
                    "application_id", "course_id", "course_name", "position_id", "position_title",
                    "student_id", "status", "applied_at", "gpa", "english_score", "major", "work_hours", "pdf_attached"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (Application app : apps) {
                Position p = positionMap.get(app.getPositionId());
                Resume resume = CSVService.getApplicationResume(app.getId());
                if (resume == null) {
                    resume = CSVService.getResume(app.getStudentId());
                }
                String courseId = p == null ? "" : safe(p.getCourseId());
                String courseName = courseNameMap.getOrDefault(courseId, "");

                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(app.getId());
                row.createCell(1).setCellValue(courseId);
                row.createCell(2).setCellValue(courseName);
                row.createCell(3).setCellValue(app.getPositionId());
                row.createCell(4).setCellValue(p == null ? "" : safe(p.getTitle()));
                row.createCell(5).setCellValue(app.getStudentId());
                row.createCell(6).setCellValue(Optional.ofNullable(app.getStatus()).orElse("pending"));
                row.createCell(7).setCellValue(safe(app.getCreatedAt()));
                row.createCell(8).setCellValue(resume == null ? "" : safe(resume.getGpa()));
                row.createCell(9).setCellValue(resume == null ? "" : safe(resume.getEnglishScore()));
                row.createCell(10).setCellValue(resume == null ? "" : safe(resume.getMajor()));
                row.createCell(11).setCellValue(resume == null ? "" : safe(resume.getWorkHours()));
                row.createCell(12).setCellValue((app.getResumePdfPath() == null || app.getResumePdfPath().trim().isEmpty()) ? "No" : "Yes");
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

    private double parseGpaSafe(Resume appResume, String studentId) {
        Resume resume = appResume == null ? CSVService.getResume(studentId) : appResume;
        if (resume == null || resume.getGpa() == null) {
            return -1.0;
        }
        try {
            return Double.parseDouble(resume.getGpa().trim());
        } catch (Exception ex) {
            return -1.0;
        }
    }
}
