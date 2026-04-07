<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Position Monitoring - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
<div class="max-w-6xl mx-auto">
    <div class="flex items-center justify-between mb-8">
        <h2 class="text-3xl font-bold">Position Monitoring</h2>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800">&larr; Back to Dashboard</a>
    </div>

    <div class="bg-white p-8 rounded-xl shadow-lg mb-8">
        <h3 class="text-xl font-semibold mb-4">Summary</h3>
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div class="p-4 rounded-lg border">
                <p class="text-sm text-gray-500">Total Positions</p>
                <p class="text-3xl font-bold">${totalPositions}</p>
            </div>
            <div class="p-4 rounded-lg border">
                <p class="text-sm text-gray-500">Positions Applied</p>
                <p class="text-3xl font-bold">${appliedPositions}</p>
            </div>
            <div class="p-4 rounded-lg border">
                <p class="text-sm text-gray-500">Pending Applications</p>
                <p class="text-3xl font-bold">${pendingApplications}</p>
            </div>
        </div>
    </div>

    <c:if test="${param.success == 'deleted'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Position deleted successfully (related applications were removed).</div>
    </c:if>
    <c:if test="${param.success == 'statusUpdated'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Position status updated successfully.</div>
    </c:if>
    <c:if test="${param.error == 'invalidPosition'}">
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Invalid position request.</div>
    </c:if>

    <div class="bg-white rounded-xl shadow-lg overflow-hidden">
        <table class="w-full text-left">
            <thead class="bg-slate-900 text-white">
            <tr>
                <th class="px-6 py-3">Position ID</th>
                <th class="px-6 py-3">Title</th>
                <th class="px-6 py-3">Course ID</th>
                <th class="px-6 py-3">Requirements</th>
                <th class="px-6 py-3">Status</th>
                <th class="px-6 py-3">Action</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
            <c:forEach var="pos" items="${positions}">
                <tr>
                    <td class="px-6 py-3">${pos.id}</td>
                    <td class="px-6 py-3">${pos.title}</td>
                    <td class="px-6 py-3">${pos.courseId}</td>
                    <td class="px-6 py-3">${pos.requirements}</td>
                    <td class="px-6 py-3">
                        <span class="px-2 py-1 rounded-full text-xs ${pos.status == 'open' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">
                            ${pos.status}
                        </span>
                    </td>
                    <td class="px-6 py-3">
                        <form action="${pageContext.request.contextPath}/admin/positions" method="post">
                            <input type="hidden" name="action" value="togglePosition" />
                            <input type="hidden" name="positionId" value="${pos.id}" />
                            <button type="submit" class="text-sm font-medium ${pos.status == 'open' ? 'text-red-600 hover:text-red-800' : 'text-green-600 hover:text-green-800'}">
                                ${pos.status == 'open' ? 'Take Down' : 'Restore'}
                            </button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/positions" method="post" class="mt-1" onsubmit="return confirm('Delete this position and all related applications?');">
                            <input type="hidden" name="action" value="deletePosition" />
                            <input type="hidden" name="positionId" value="${pos.id}" />
                            <button type="submit" class="text-sm font-medium text-red-600 hover:text-red-800">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
