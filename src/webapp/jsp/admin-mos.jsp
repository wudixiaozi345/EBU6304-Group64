<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Admin User Management - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-8">
<div class="max-w-6xl mx-auto">
    <div class="flex items-center justify-between mb-6">
        <h2 class="text-4xl font-bold">Admin User Management</h2>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-indigo-600 hover:text-indigo-800 font-semibold">&larr; Back to Dashboard</a>
    </div>

    <section class="bg-white p-6 rounded-xl shadow-lg mb-8">
        <h3 class="text-2xl font-semibold mb-4">Add New MO</h3>
        <form action="${pageContext.request.contextPath}/admin/mos" method="post" class="grid gap-3 grid-cols-1 md:grid-cols-5">
            <input type="hidden" name="action" value="addMO">
            <input type="text" name="id" placeholder="Staff ID" required class="px-4 py-2 border rounded-lg">
            <input type="text" name="name" placeholder="Full Name" required class="px-4 py-2 border rounded-lg">
            <input type="email" name="email" placeholder="Email" required class="px-4 py-2 border rounded-lg">
            <input type="password" name="password" placeholder="Password (default mo123)" class="px-4 py-2 border rounded-lg">
            <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Add MO</button>
        </form>
    </section>

    <section class="grid grid-cols-1 xl:grid-cols-2 gap-6">
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="px-6 py-4 border-b">
                <h3 class="text-xl font-bold">MO Accounts</h3>
                <p class="text-sm text-gray-500">View and set MO status.</p>
            </div>
            <div class="p-4 overflow-x-auto">
                <table class="min-w-full text-sm">
                    <thead class="bg-slate-900 text-white">
                    <tr>
                        <th class="px-3 py-2">Staff ID</th>
                        <th class="px-3 py-2">Name</th>
                        <th class="px-3 py-2">Email</th>
                        <th class="px-3 py-2">Status</th>
                        <th class="px-3 py-2">Action</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-100">
                    <c:forEach var="mo" items="${mos}">
                        <tr>
                            <td class="px-3 py-2">${mo.id}</td>
                            <td class="px-3 py-2">${mo.name}</td>
                            <td class="px-3 py-2">${mo.email}</td>
                            <td class="px-3 py-2">
                                <span class="px-2 py-1 rounded-full text-xs font-semibold ${mo.status == 'active' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">${mo.status}</span>
                            </td>
                            <td class="px-3 py-2">
                                <form action="${pageContext.request.contextPath}/admin/mos" method="post" class="inline">
                                    <input type="hidden" name="action" value="toggleStatus" />
                                    <input type="hidden" name="role" value="mo" />
                                    <input type="hidden" name="id" value="${mo.id}" />
                                    <button type="submit" class="text-sm font-semibold ${mo.status == 'active' ? 'text-red-600 hover:text-red-800' : 'text-green-600 hover:text-green-800'}">${mo.status == 'active' ? 'Disable' : 'Enable'}</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="px-6 py-4 border-b">
                <h3 class="text-xl font-bold">TA Accounts</h3>
                <p class="text-sm text-gray-500">View and set TA status.</p>
            </div>
            <div class="p-4 overflow-x-auto">
                <table class="min-w-full text-sm">
                    <thead class="bg-slate-900 text-white">
                    <tr>
                        <th class="px-3 py-2">Student ID</th>
                        <th class="px-3 py-2">Name</th>
                        <th class="px-3 py-2">Email</th>
                        <th class="px-3 py-2">Status</th>
                        <th class="px-3 py-2">Action</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-100">
                    <c:forEach var="ta" items="${tas}">
                        <tr>
                            <td class="px-3 py-2">${ta.id}</td>
                            <td class="px-3 py-2">${ta.name}</td>
                            <td class="px-3 py-2">${ta.email}</td>
                            <td class="px-3 py-2">
                                <span class="px-2 py-1 rounded-full text-xs font-semibold ${ta.status == 'active' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">${ta.status}</span>
                            </td>
                            <td class="px-3 py-2">
                                <form action="${pageContext.request.contextPath}/admin/mos" method="post" class="inline">
                                    <input type="hidden" name="action" value="toggleStatus" />
                                    <input type="hidden" name="role" value="ta" />
                                    <input type="hidden" name="id" value="${ta.id}" />
                                    <button type="submit" class="text-sm font-semibold ${ta.status == 'active' ? 'text-red-600 hover:text-red-800' : 'text-green-600 hover:text-green-800'}">${ta.status == 'active' ? 'Disable' : 'Enable'}</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</div>
</body>
</html>
