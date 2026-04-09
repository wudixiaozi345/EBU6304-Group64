<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Manage Courses - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
<div class="max-w-6xl mx-auto">
    <div class="flex items-center justify-between mb-8">
        <h2 class="text-3xl font-bold">Manage Courses</h2>
        <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800">Back to Dashboard</a>
    </div>

    <c:if test="${param.success == 'created'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Course created successfully.</div>
    </c:if>
    <c:if test="${param.success == 'statusUpdated'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Course status updated successfully.</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Course deleted successfully (related positions and applications were removed).</div>
    </c:if>
    <c:if test="${param.error == 'duplicate'}">
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Course ID already exists.</div>
    </c:if>
    <c:if test="${param.error == 'missing'}">
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Please complete all fields.</div>
    </c:if>
    <c:if test="${param.error == 'notfound'}">
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Course not found.</div>
    </c:if>
    <c:if test="${param.error == 'import'}">
        <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Import failed. Please upload a valid .xlsx template file.</div>
    </c:if>
    <c:if test="${param.success == 'imported'}">
        <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Import completed: imported ${param.imported}, duplicate ${param.duplicate}, invalid ${param.invalid}.</div>
    </c:if>

    <div class="bg-white p-8 rounded-xl shadow-lg mb-10">
        <h3 class="text-xl font-bold mb-6">Import Courses (Excel .xlsx)</h3>
        <p class="text-sm text-slate-500 mb-4">Template columns in first row: course_id, name, credits</p>
        <form action="${pageContext.request.contextPath}/admin/courses/import" method="post" enctype="multipart/form-data" class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <input type="hidden" name="action" value="preview" />
            <input type="file" name="file" accept=".xlsx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" required class="px-4 py-2 border rounded-lg" />
            <input name="semester" placeholder="Semester (e.g. 2026-Spring)" class="px-4 py-2 border rounded-lg" />
            <select name="moId" class="px-4 py-2 border rounded-lg">
                <option value="">No default MO</option>
                <c:forEach var="mo" items="${moUsers}">
                    <option value="${mo.id}">${mo.name} (${mo.id})</option>
                </c:forEach>
            </select>
            <button type="submit" class="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700 transition-colors">Import Excel</button>
        </form>
    </div>

    <c:if test="${not empty previewRows}">
        <div class="bg-white p-8 rounded-xl shadow-lg mb-10">
            <h3 class="text-xl font-bold mb-4">Import Preview</h3>
            <p class="text-sm text-slate-600 mb-3">Valid rows: ${previewImported}, Duplicate: ${previewDuplicate}, Invalid: ${previewInvalid}</p>
            <div class="overflow-x-auto mb-4">
                <table class="w-full text-left border">
                    <thead class="bg-slate-100">
                    <tr>
                        <th class="px-4 py-2">Course ID</th>
                        <th class="px-4 py-2">Name</th>
                        <th class="px-4 py-2">Credits</th>
                        <th class="px-4 py-2">MO</th>
                        <th class="px-4 py-2">Semester</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${previewRows}">
                        <tr class="border-t">
                            <td class="px-4 py-2">${row.id}</td>
                            <td class="px-4 py-2">${row.name}</td>
                            <td class="px-4 py-2">${row.credits}</td>
                            <td class="px-4 py-2">${row.moId}</td>
                            <td class="px-4 py-2">${row.semester}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <form action="${pageContext.request.contextPath}/admin/courses/import" method="post">
                <input type="hidden" name="action" value="confirm" />
                <button type="submit" class="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition-colors">Confirm Import</button>
            </form>
        </div>
    </c:if>

    <div class="bg-white p-8 rounded-xl shadow-lg mb-10">
        <h3 class="text-xl font-bold mb-6">Create New Course</h3>
        <form action="${pageContext.request.contextPath}/admin/courses" method="post" class="grid grid-cols-1 md:grid-cols-5 gap-4">
            <input name="courseId" placeholder="Course ID" required class="px-4 py-2 border rounded-lg" />
            <input name="name" placeholder="Course Name" required class="px-4 py-2 border rounded-lg" />
            <input name="credits" placeholder="Credits" required class="px-4 py-2 border rounded-lg" />
            <select name="moId" required class="px-4 py-2 border rounded-lg">
                <option value="">Select MO</option>
                <c:forEach var="mo" items="${moUsers}">
                    <option value="${mo.id}">${mo.name} (${mo.id})</option>
                </c:forEach>
            </select>
            <input name="semester" placeholder="Semester" required class="px-4 py-2 border rounded-lg" />
            <div class="md:col-span-5 flex justify-end">
                <button type="submit" class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors">Create Course</button>
            </div>
        </form>
    </div>

    <div class="bg-white rounded-xl shadow-lg overflow-hidden">
        <table class="w-full text-left">
            <thead class="bg-slate-900 text-white">
            <tr>
                <th class="px-6 py-4">Course ID</th>
                <th class="px-6 py-4">Name</th>
                <th class="px-6 py-4">Credits</th>
                <th class="px-6 py-4">MO Staff ID</th>
                <th class="px-6 py-4">Semester</th>
                <th class="px-6 py-4">Status</th>
                <th class="px-6 py-4">Actions</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
            <c:forEach var="course" items="${courses}">
                <tr>
                    <td class="px-6 py-4 font-medium">${course.id}</td>
                    <td class="px-6 py-4">${course.name}</td>
                    <td class="px-6 py-4">${course.credits}</td>
                    <td class="px-6 py-4">${course.moId}</td>
                    <td class="px-6 py-4">${course.semester}</td>
                    <td class="px-6 py-4">
                        <span class="px-2 py-1 rounded-full text-xs font-semibold ${course.status == 'active' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">${course.status}</span>
                    </td>
                    <td class="px-6 py-4">
                        <form action="${pageContext.request.contextPath}/admin/courses" method="post" class="inline">
                            <input type="hidden" name="action" value="toggleStatus" />
                            <input type="hidden" name="courseId" value="${course.id}" />
                            <button type="submit" class="text-sm font-semibold ${course.status == 'active' ? 'text-red-600 hover:text-red-800' : 'text-green-600 hover:text-green-800'}">${course.status == 'active' ? 'Disable' : 'Enable'}</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/courses" method="post" class="inline ml-3" onsubmit="return confirm('Delete this course and all related positions/applications?');">
                            <input type="hidden" name="action" value="deleteCourse" />
                            <input type="hidden" name="courseId" value="${course.id}" />
                            <button type="submit" class="text-sm font-semibold text-red-600 hover:text-red-800">Delete</button>
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
