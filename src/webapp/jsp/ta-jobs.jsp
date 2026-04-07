<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.recruit.service.CSVService" %>
<%@ page import="com.bupt.recruit.model.Position" %>
<%@ page import="com.bupt.recruit.model.Course" %>
<%@ page import="com.bupt.recruit.model.Application" %>
<%@ page import="com.bupt.recruit.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="jobs" value="${requestScope.jobs}" />
<c:set var="resumeComplete" value="${requestScope.resumeComplete}" />

<html>
<head>
    <title>Apply Jobs</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
    <body class="bg-gray-100 p-10">
<div class="max-w-5xl mx-auto">
    <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
        <h2 class="text-3xl font-bold">Positions</h2>
        <form action="${pageContext.request.contextPath}/ta/jobs" method="get" class="flex gap-2">
            <input type="text" name="search" value="${param.search}" placeholder="Search by job title or course" class="px-3 py-2 border border-gray-300 rounded-lg" />
            <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Search</button>
        </form>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800">Back to Dashboard</a>
    </div>
    <c:if test="${param.success == 'true'}">
        <div class="bg-green-100 text-green-800 p-3 rounded mb-4">Application successful! Your submission has been recorded.</div>
    </c:if>
    <c:if test="${param.already == 'true'}">
        <div class="bg-yellow-100 text-yellow-800 p-3 rounded mb-4">You have already applied for this position; duplicate applications are not allowed.</div>
    </c:if>
    <c:if test="${param.resumeIncomplete == 'true'}">
        <div class="bg-red-100 text-red-800 p-3 rounded mb-4">Please complete your resume first before applying.</div>
    </c:if>
    <c:if test="${param.unavailable == 'true'}">
        <div class="bg-yellow-100 text-yellow-800 p-3 rounded mb-4">This position is unavailable for application right now.</div>
    </c:if>
    <div class="grid gap-4">
        <c:forEach var="job" items="${jobs}">
            <div class="bg-white p-5 rounded-lg shadow flex justify-between items-center">
                <div>
                    <h3 class="text-xl font-bold">${job.title}</h3>
                    <p class="text-gray-500">${job.courseName} (${job.courseId})</p>
                    <p class="text-blue-600 text-sm mt-1">Req: ${job.requirements}</p>
                    <c:if test="${not empty job.applyDisabledReason && !job.applied}">
                        <p class="text-xs text-amber-700 mt-1">${job.applyDisabledReason}</p>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${job.applied}">
                        <button disabled class="bg-gray-200 px-4 py-2 rounded text-gray-500">Applied</button>
                    </c:when>
                    <c:when test="${not empty job.applyDisabledReason}">
                        <button disabled class="bg-gray-200 px-4 py-2 rounded text-gray-500">Apply</button>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/ta/apply?jobId=${job.id}" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 inline-block">Apply</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>