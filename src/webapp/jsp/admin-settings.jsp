<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>System Settings - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
<div class="max-w-3xl mx-auto">
    <div class="flex items-center justify-between mb-8">
        <h2 class="text-3xl font-bold">System Basic Configuration</h2>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800">&larr; Back to Dashboard</a>
    </div>

    <div class="bg-white p-8 rounded-xl shadow-lg">
        <form action="${pageContext.request.contextPath}/admin/settings" method="post" class="space-y-6">
            <div>
                <label class="block text-sm font-medium text-slate-700">Application Deadline Format</label>
                <input type="text" name="deadlineFormat" value="${config['application.deadline.format']}" class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Login Failure Limit</label>
                <input type="number" name="failLimit" value="${config['login.fail.limit']}" min="1" class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Lockout Minutes</label>
                <input type="number" name="lockMinutes" value="${config['login.lock.minutes']}" min="1" class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <button type="submit" class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save Settings</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>