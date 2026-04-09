<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TA Registration - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 flex items-center justify-center h-screen">
    <div class="w-full max-w-md p-8 bg-white rounded-xl shadow-lg">
        <h2 class="text-2xl font-bold mb-6 text-center text-blue-600">TA Registration</h2>
        <form action="register" method="post" class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-slate-700">Student ID</label>
                <input type="text" name="id" required class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Full Name</label>
                <input type="text" name="name" required class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Email Address (Optional)</label>
                <input type="email" name="email" class="w-full px-4 py-2 border rounded-lg">
            </div>
            <div>
                <label class="block text-sm font-medium text-slate-700">Password</label>
                <input type="password" name="password" required class="w-full px-4 py-2 border rounded-lg">
            </div>
            <% if (request.getAttribute("error") != null) { %>
                <p class="text-sm text-red-600"><%= request.getAttribute("error") %></p>
            <% } %>
            <button type="submit" class="w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">Register</button>
            <p class="text-center text-sm text-slate-500">Already have an account? <a href="login.jsp" class="text-blue-600">Login</a></p>
        </form>
    </div>
</body>
</html>
