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
<c:set var="favoritePositionIds" value="${requestScope.favoritePositionIds}" />
<c:set var="favoritePositionMap" value="${requestScope.favoritePositionMap}" />
<c:set var="moNameMap" value="${requestScope.moNameMap}" />

<html>
<head>
    <title>Apply Jobs</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
    <body class="bg-gray-100 p-10">
<div class="max-w-5xl mx-auto">
    <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
        <h2 class="text-3xl font-bold">Positions</h2>
        <form action="${pageContext.request.contextPath}/ta/jobs" method="get" class="flex flex-wrap gap-2">
            <input type="text" name="search" value="${param.search}" placeholder="Search by title/course" class="px-3 py-2 border border-gray-300 rounded-lg" />
            <input type="text" name="courseName" value="${param.courseName}" placeholder="Filter by course name" class="px-3 py-2 border border-gray-300 rounded-lg" />
            <input type="number" min="0" name="minVacancies" value="${param.minVacancies}" placeholder="Min vacancies" class="w-36 px-3 py-2 border border-gray-300 rounded-lg" />
            <select name="sortBy" class="px-3 py-2 border border-gray-300 rounded-lg">
                <option value="deadlineAsc" ${sortBy == 'deadlineAsc' ? 'selected' : ''}>Deadline: earliest first</option>
                <option value="deadlineDesc" ${sortBy == 'deadlineDesc' ? 'selected' : ''}>Deadline: latest first</option>
            </select>
            <label class="inline-flex items-center gap-2 px-3 py-2 border border-gray-300 rounded-lg bg-white text-sm">
                <input type="checkbox" name="onlyFavorites" value="true" ${param.onlyFavorites == 'true' ? 'checked' : ''} />
                Favorites only
            </label>
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
    <c:if test="${param.limitOne == 'true'}">
        <div class="bg-yellow-100 text-yellow-800 p-3 rounded mb-4">Each TA can apply for only one position.</div>
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
                    <p class="text-gray-500 text-sm">MO: ${moNameMap[job.moId]}</p>
                    <p class="text-gray-500 text-sm">Deadline: ${job.deadline} | Vacancies: ${job.vacancies}</p>
                    <c:if test="${not empty job.preferredCondition}">
                        <p class="text-gray-500 text-sm">Preferred: ${job.preferredCondition}</p>
                    </c:if>
                    <c:if test="${not empty job.minGpa or not empty job.minEnglishScore}">
                        <p class="text-gray-500 text-sm">Hard Criteria: GPA &gt;= ${empty job.minGpa ? '-' : job.minGpa}, English &gt;= ${empty job.minEnglishScore ? '-' : job.minEnglishScore}</p>
                    </c:if>
                    <p class="text-blue-600 text-sm mt-1">Req: ${job.requirements}</p>
                    <c:if test="${not empty job.applyDisabledReason && !job.applied}">
                        <p class="text-xs text-amber-700 mt-1">${job.applyDisabledReason}</p>
                    </c:if>
                </div>

                <div class="flex items-center gap-2">
                    <form action="${pageContext.request.contextPath}/ta/jobs" method="post">
                        <input type="hidden" name="action" value="toggleFavorite" />
                        <input type="hidden" name="positionId" value="${job.id}" />
                        <button type="submit" class="px-3 py-2 rounded border ${not empty favoritePositionMap[job.id] ? 'bg-yellow-100 text-yellow-700 border-yellow-300' : 'bg-white text-gray-600 border-gray-300'}">
                            ${not empty favoritePositionMap[job.id] ? 'Favorited' : 'Favorite'}
                        </button>
                    </form>

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
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>