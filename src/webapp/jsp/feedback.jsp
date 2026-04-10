<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Feedback - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
<div class="max-w-5xl mx-auto">
    <div class="flex items-center justify-between mb-8">
        <h2 class="text-3xl font-bold">Feedback to Admin</h2>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800">Back to Dashboard</a>
    </div>

    <c:if test="${param.success == 'created'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Feedback submitted successfully.</div>
    </c:if>
    <c:if test="${param.error == 'missing'}">
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Please fill both title and content.</div>
    </c:if>

    <div class="bg-white p-6 rounded-xl shadow mb-6">
        <h3 class="text-xl font-semibold mb-4">Submit New Feedback</h3>
        <form action="" method="post" class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-slate-700">Title</label>
                <input name="title" required class="w-full px-4 py-2 border rounded-lg" />
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Content</label>
                <textarea name="content" required class="w-full px-4 py-2 border rounded-lg h-28"></textarea>
            </div>
            <button type="submit" class="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Submit</button>
        </form>
    </div>

    <div class="bg-white rounded-xl shadow overflow-hidden">
        <table class="w-full text-left">
            <thead class="bg-slate-900 text-white">
            <tr>
                <th class="px-4 py-3">Time</th>
                <th class="px-4 py-3">Title</th>
                <th class="px-4 py-3">Status</th>
                <th class="px-4 py-3">Reply</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
            <c:forEach var="f" items="${feedbacks}">
                <tr>
                    <td class="px-4 py-3 text-sm text-slate-500">${f.createdAt}</td>
                    <td class="px-4 py-3">
                        <p class="font-semibold">${f.title}</p>
                        <p class="text-sm text-slate-600 mt-1">${f.content}</p>
                    </td>
                    <td class="px-4 py-3">${f.status}</td>
                    <td class="px-4 py-3 text-sm text-slate-700">${empty f.reply ? '-' : f.reply}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
