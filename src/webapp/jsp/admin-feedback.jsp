<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Feedback Management - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
<div class="max-w-6xl mx-auto">
    <div class="flex items-center justify-between mb-8">
        <h2 class="text-3xl font-bold">Feedback Management</h2>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800">Back to Dashboard</a>
    </div>

    <c:if test="${param.success == 'updated'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Feedback updated.</div>
    </c:if>

    <div class="mb-4 flex gap-2">
        <a href="${pageContext.request.contextPath}/admin/feedback?status=all" class="px-3 py-1 rounded ${statusFilter == 'all' ? 'bg-indigo-600 text-white' : 'bg-slate-100'}">All</a>
        <a href="${pageContext.request.contextPath}/admin/feedback?status=pending" class="px-3 py-1 rounded ${statusFilter == 'pending' ? 'bg-indigo-600 text-white' : 'bg-slate-100'}">Pending</a>
        <a href="${pageContext.request.contextPath}/admin/feedback?status=in_progress" class="px-3 py-1 rounded ${statusFilter == 'in_progress' ? 'bg-indigo-600 text-white' : 'bg-slate-100'}">In Progress</a>
        <a href="${pageContext.request.contextPath}/admin/feedback?status=resolved" class="px-3 py-1 rounded ${statusFilter == 'resolved' ? 'bg-indigo-600 text-white' : 'bg-slate-100'}">Resolved</a>
    </div>

    <div class="space-y-4">
        <c:forEach var="f" items="${feedbacks}">
            <div class="bg-white rounded-xl shadow p-6">
                <div class="flex items-center justify-between mb-3">
                    <div>
                        <h3 class="text-lg font-bold">${f.title}</h3>
                        <p class="text-sm text-slate-500">${f.createdAt} | ${f.fromRole.toUpperCase()} ${f.fromUserId}</p>
                    </div>
                    <span class="px-3 py-1 rounded-full text-xs bg-slate-100">${f.status}</span>
                </div>
                <p class="text-slate-700 mb-4">${f.content}</p>

                <form action="${pageContext.request.contextPath}/admin/feedback" method="post" class="grid grid-cols-1 md:grid-cols-4 gap-3">
                    <input type="hidden" name="id" value="${f.id}" />
                    <select name="status" class="px-3 py-2 border rounded-lg">
                        <option value="pending" ${f.status == 'pending' ? 'selected' : ''}>Pending</option>
                        <option value="in_progress" ${f.status == 'in_progress' ? 'selected' : ''}>In Progress</option>
                        <option value="resolved" ${f.status == 'resolved' ? 'selected' : ''}>Resolved</option>
                    </select>
                    <input name="reply" value="${f.reply}" placeholder="Reply visible to submitter" class="px-3 py-2 border rounded-lg md:col-span-2" />
                    <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save</button>
                </form>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>
