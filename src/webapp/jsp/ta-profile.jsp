<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>My Profile - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
    <div class="max-w-2xl mx-auto bg-white p-10 rounded-xl shadow-lg">
        <div class="mb-6">
            <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800 font-medium">&larr; Back to Dashboard</a>
        </div>
        <h2 class="text-3xl font-bold mb-8">My Profile</h2>

        <c:if test="${param.success == 'true'}">
            <div class="mb-6 p-3 rounded-lg bg-green-100 text-green-800">Your profile changes have been saved successfully.</div>
        </c:if>
        <c:if test="${param.mustComplete == 'true'}">
            <div class="mb-6 p-3 rounded-lg bg-amber-100 text-amber-800">Please complete My Profile first. Other TA functions are locked until this is done.</div>
        </c:if>
        <c:if test="${param.error == 'email'}">
            <div class="mb-6 p-3 rounded-lg bg-red-100 text-red-800">Email must end with @qmul.ac.uk.</div>
        </c:if>
        <c:if test="${param.error == 'password'}">
            <div class="mb-6 p-3 rounded-lg bg-red-100 text-red-800">Password must include uppercase, lowercase and number (at least 8 chars).</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/ta/profile" method="post" class="space-y-6">
            <div>
                <label class="block text-sm font-medium text-slate-700">Student ID (Read-only)</label>
                <input type="text" value="${user.id}" disabled class="w-full px-4 py-2 border rounded-lg bg-slate-50 text-slate-500">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Full Name</label>
                <input type="text" name="name" value="${user.name}" required class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Email Address</label>
                <input type="email" name="email" value="${user.email}" required class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">New Password (Leave blank to keep current)</label>
                <input type="password" name="password" class="w-full px-4 py-2 border rounded-lg">
            </div>
            
            <div class="pt-6 border-t border-slate-100 flex flex-col gap-4">
                <button type="submit" class="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-bold">Save Changes</button>
            </div>
        </form>
    </div>
</body>
</html>
