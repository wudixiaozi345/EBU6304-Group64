<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.recruit.service.CSVService" %>
<%@ page import="com.bupt.recruit.model.Application" %>
<%@ page import="com.bupt.recruit.model.Position" %>
<%@ page import="com.bupt.recruit.model.Course" %>
<%@ page import="com.bupt.recruit.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    try {
        List<Application> allApps = CSVService.readApplications();
        List<Position> positions = CSVService.readPositions();
        List<Course> courses = CSVService.readCourses();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Application> myApps = new ArrayList<Application>();
        for (Application app : allApps) {
            if (user.getId().equals(app.getStudentId())) {
                myApps.add(app);
            }
        }

        for (Application app : myApps) {
            for (Position pos : positions) {
                if (pos.getId().equals(app.getPositionId())) {
                    app.setPositionTitle(pos.getTitle());
                    for (Course c : courses) {
                        if (c.getId().equals(pos.getCourseId())) {
                            app.setCourseName(c.getName());
                            app.setCourseId(c.getId());
                            break;
                        }
                    }
                    break;
                }
            }
        }

        pageContext.setAttribute("apps", myApps);
    } catch (Exception e) {
        pageContext.setAttribute("apps", new ArrayList<Application>());
    }
%>

<html>
<head>
    <title>My Applications</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
    <body class="bg-gray-100 p-10">
<div class="max-w-5xl mx-auto">
    <div class="flex items-center justify-between mb-6">
        <h2 class="text-3xl font-bold">My Applications</h2>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800">Back to Dashboard</a>
    </div>
    <c:if test="${param.success == 'true'}">
        <div class="bg-green-100 text-green-800 p-3 rounded mb-4">Application submitted successfully. Your status page has been updated.</div>
    </c:if>
    <c:if test="${param.cancel == 'true'}">
        <div class="bg-green-100 text-green-800 p-3 rounded mb-4">Application cancelled successfully.</div>
    </c:if>
    <c:if test="${empty apps}">
        <div class="bg-white p-6 rounded-lg shadow-md">You haven't submitted any applications yet. Go <a href="${pageContext.request.contextPath}/ta/jobs" class="text-blue-600 hover:underline">apply for jobs</a> now.</div>
    </c:if>
    <div class="grid gap-4">
        <c:forEach var="app" items="${apps}">
            <div class="bg-white p-5 rounded-lg shadow flex justify-between items-center">
                <div>
                    <h3 class="text-xl font-bold">${app.positionTitle}</h3>
                    <p class="text-gray-500">${app.courseName} (${app.courseId})</p>
                    <p class="text-gray-400 text-sm italic">Applied on: ${app.createdAt}</p>
                    <c:if test="${not empty app.reason}">
                        <p class="mt-2 text-gray-600 italic">MO: ${app.reason}</p>
                    </c:if>
                </div>

                <div class="flex items-center gap-3">
                    <span class="px-3 py-1 rounded-full text-xs font-bold
                        ${app.status == 'pending' ? 'bg-yellow-100 text-yellow-700' :
                          app.status == 'accepted' ? 'bg-green-100 text-green-700' :
                          'bg-red-100 text-red-700'}">
                        ${app.status}
                    </span>
                    <c:if test="${app.status == 'pending'}">
                        <form action="ta-apps" method="post" onsubmit="return confirm('Are you sure you want to cancel this application?');">
                            <input type="hidden" name="action" value="cancel" />
                            <input type="hidden" name="appId" value="${app.id}" />
                            <button type="submit" class="px-3 py-1 bg-red-500 text-white rounded-lg text-xs">Cancel</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>