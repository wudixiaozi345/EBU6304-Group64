package com.bupt.recruit.servlet;

import com.bupt.recruit.model.Application;
import com.bupt.recruit.model.Course;
import com.bupt.recruit.model.Position;
import com.bupt.recruit.model.User;
import com.bupt.recruit.service.CSVService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResumePdfDownloadServlet extends HttpServlet {
    private static final Pattern PDF_FILENAME_PATTERN = Pattern.compile("([A-Za-z0-9]+_[0-9a-fA-F-]+\\.pdf)$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String appId = request.getParameter("appId");
        Application app = CSVService.findApplicationById(appId);
        if (app == null || app.getResumePdfPath() == null || app.getResumePdfPath().trim().isEmpty()) {
            System.out.println("[ResumePdfDownloadServlet] 404 app not found or no pdf. appId=" + appId
                    + ", dataDir=" + CSVService.getDataDirectory().getAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!canAccessPdf(user, app)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        File pdf = resolvePdfFile(app.getResumePdfPath());
        if (!pdf.exists() || !pdf.isFile()) {
            System.out.println("[ResumePdfDownloadServlet] 404 pdf file missing. appId=" + appId
                    + ", savedPath=" + app.getResumePdfPath()
                    + ", resolvedPath=" + pdf.getAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=resume_" + app.getStudentId() + ".pdf");
        long fileLength = pdf.length();
        if (fileLength <= Integer.MAX_VALUE) {
            response.setContentLength((int) fileLength);
        } else {
            response.setHeader("Content-Length", String.valueOf(fileLength));
        }

        try (FileInputStream fis = new FileInputStream(pdf)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, len);
            }
            response.getOutputStream().flush();
        }
    }

    private boolean canAccessPdf(User user, Application app) {
        if ("admin".equalsIgnoreCase(user.getRole())) {
            return true;
        }

        if ("ta".equalsIgnoreCase(user.getRole())) {
            return Objects.equals(user.getId(), app.getStudentId());
        }

        if (!"mo".equalsIgnoreCase(user.getRole())) {
            return false;
        }

        List<Course> myCourses = CSVService.readCourses().stream()
                .filter(c -> Objects.equals(user.getId(), c.getMoId()))
                .collect(Collectors.toList());
        Set<String> myCourseIds = myCourses.stream().map(Course::getId).collect(Collectors.toSet());

        Position targetPos = CSVService.readPositions().stream()
                .filter(p -> Objects.equals(p.getId(), app.getPositionId()))
                .findFirst()
                .orElse(null);

        return targetPos != null && myCourseIds.contains(targetPos.getCourseId());
    }

    private File resolvePdfFile(String savedPath) {
        if (savedPath == null || savedPath.trim().isEmpty()) {
            return new File(new File(CSVService.getDataDirectory(), "resume_uploads"), "");
        }

        File pdf = new File(savedPath.trim());
        if (pdf.exists() && pdf.isFile()) {
            return pdf;
        }

        String filename = extractPdfFilename(savedPath, pdf.getName());
        if (filename == null || filename.trim().isEmpty()) {
            return pdf;
        }

        return new File(new File(CSVService.getDataDirectory(), "resume_uploads"), filename);
    }

    private String extractPdfFilename(String savedPath, String fileNameByJavaIo) {
        if (fileNameByJavaIo != null && fileNameByJavaIo.toLowerCase().endsWith(".pdf")
                && !fileNameByJavaIo.toLowerCase().contains("resume_uploads")) {
            return fileNameByJavaIo;
        }

        String normalized = savedPath == null ? "" : savedPath.trim().replace("\"", "");
        Matcher matcher = PDF_FILENAME_PATTERN.matcher(normalized);
        if (matcher.find()) {
            return matcher.group(1);
        }

        int lastSlash = Math.max(normalized.lastIndexOf('/'), normalized.lastIndexOf('\\'));
        if (lastSlash >= 0 && lastSlash + 1 < normalized.length()) {
            String tail = normalized.substring(lastSlash + 1);
            if (tail.toLowerCase().endsWith(".pdf")) {
                return tail;
            }
        }

        if (normalized.toLowerCase().endsWith(".pdf")) {
            int marker = normalized.toLowerCase().lastIndexOf("resume_uploads");
            if (marker >= 0) {
                String tail = normalized.substring(marker + "resume_uploads".length())
                        .replace("\\", "")
                        .replace("/", "");
                if (!tail.isEmpty()) {
                    return tail;
                }
            }
        }

        return fileNameByJavaIo;
    }
}
