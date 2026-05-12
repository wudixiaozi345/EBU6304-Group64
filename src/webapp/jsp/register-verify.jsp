<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Verify Email - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 flex items-center justify-center h-screen">
<div class="w-full max-w-md p-8 bg-white rounded-xl shadow-lg">
    <h2 class="text-2xl font-bold mb-4 text-center text-blue-600">Email Verification</h2>
    <p class="text-sm text-slate-600 mb-2">We are verifying: ${pendingEmail}</p>
    <div class="mb-6 p-3 rounded-lg bg-amber-50 border border-amber-200 text-amber-800 text-sm">
        <p class="font-semibold">Demo mode only</p>
        <p>No real email is being sent by the current system.</p>
        <p>Your verification code is: ${demoCode}</p>
    </div>

    <form action="${pageContext.request.contextPath}/register/verify-email" method="post" class="space-y-4">
        <div>
            <label class="block text-sm font-medium text-slate-700">Verification Code</label>
            <input type="text" name="code" maxlength="6" required class="w-full px-4 py-2 border rounded-lg" />
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <p class="text-sm text-red-600"><%= request.getAttribute("error") %></p>
        <% } %>

        <button type="submit" class="w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">Verify and Create Account</button>
        <p class="text-center text-sm text-slate-500"><a href="${pageContext.request.contextPath}/register.jsp" class="text-blue-600">Back to Register</a></p>
    </form>
</div>
</body>
</html>
