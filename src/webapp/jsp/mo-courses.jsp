<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>My Courses - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
    <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-3xl font-bold">My Assigned Courses</h2>
            <a href="${pageContext.request.contextPath}/dashboard" class="px-3 py-1 rounded-lg text-sm text-indigo-600 hover:bg-indigo-50">Back to Dashboard</a>
        </div>
        <c:if test="${empty courses}">
            <div class="mb-4 p-4 rounded-lg bg-yellow-100 text-yellow-800">
                No assigned courses found. If this is not correct, please ensure you accessed this page through <strong>/mo/courses</strong> and that your MO account matches course records.
            </div>
        </c:if>
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <table class="w-full text-left">
                <thead class="bg-slate-900 text-white">
                    <tr>
                        <th class="px-6 py-4">Course ID</th>
                        <th class="px-6 py-4">Name</th>
                        <th class="px-6 py-4">Credits</th>
                        <th class="px-6 py-4">Semester</th>
                        <th class="px-6 py-4">Status</th>
                        <th class="px-6 py-4">Action</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                    <c:forEach var="course" items="${courses}">
                        <tr>
                            <td class="px-6 py-4 font-medium">${course.id}</td>
                            <td class="px-6 py-4">${course.name}</td>
                            <td class="px-6 py-4">${course.credits}</td>
                            <td class="px-6 py-4">${course.semester}</td>
                            <td class="px-6 py-4">
                                <span class="px-2 py-1 rounded-full text-xs font-semibold ${course.status == 'active' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">${course.status}</span>
                            </td>
                            <td class="px-6 py-4">
                                <c:choose>
                                    <c:when test="${course.status == 'active'}">
                                        <a href="review?courseId=${course.id}" class="text-blue-600 hover:text-blue-800 font-medium">Review Applications</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-slate-400 font-medium">Disabled</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
