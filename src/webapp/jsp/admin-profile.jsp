<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Admin Profile - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-blue-50 min-h-screen">
<div class="container mx-auto py-10 px-4">
    <div class="max-w-3xl mx-auto bg-white rounded-xl shadow-lg p-8">
        <h1 class="text-3xl font-bold mb-4">Admin Profile</h1>
        <p class="text-sm text-gray-600 mb-6">Update your personal information here.</p>

        <c:if test="${not empty message}">
            <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/profile" method="post" class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-gray-700">Admin ID</label>
                <input type="text" name="id" value="${admin.id}" readonly class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2 bg-gray-100">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700">Name</label>
                <input type="text" name="name" value="${admin.name}" required class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700">Email</label>
                <input type="email" name="email" value="${admin.email}" required class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700">New Password</label>
                <input type="password" name="password" placeholder="Leave blank to keep current" class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            </div>
            <div class="flex justify-between items-center pt-4 border-t">
                <a href="${pageContext.request.contextPath}/dashboard" class="text-indigo-600 hover:text-indigo-800">Back to Dashboard</a>
                <button type="submit" class="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700">Save Changes</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
