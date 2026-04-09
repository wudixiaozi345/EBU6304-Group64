<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <html lang="en">

        <head>
            <title>MO Position Management - BUPT Recruit</title>
            <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
        </head>

        <body class="bg-slate-50 p-10">
            <div class="max-w-7xl mx-auto">
                <div class="flex items-center justify-between mb-8">
                    <h2 class="text-3xl font-bold">Position Management</h2>
                    <a href="${pageContext.request.contextPath}/dashboard"
                        class="text-indigo-600 hover:text-indigo-800">Back to Dashboard</a>
                </div>

                <c:if test="${param.success == 'created'}">
                    <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">New position created.</div>
                </c:if>
                <c:if test="${param.success == 'updated'}">
                    <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Position updated.</div>
                </c:if>
                <c:if test="${param.success == 'deleted'}">
                    <div class="mb-4 p-3 rounded-lg bg-green-100 text-green-800">Position deleted.</div>
                </c:if>
                <c:if test="${param.error == 'courseDisabled'}">
                    <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">The selected course is disabled. Please
                        choose an active course.</div>
                </c:if>
                <c:if test="${param.error == 'editClosedOnly'}">
                    <div class="mb-4 p-3 rounded-lg bg-yellow-100 text-yellow-800">Only positions closed by Admin can be
                        edited or deleted.</div>
                </c:if>
                <c:if test="${param.error == 'notfound'}">
                    <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Position not found or access denied.</div>
                </c:if>

                <div class="bg-white p-6 rounded-xl shadow-lg mb-8">
                    <h3 class="text-xl font-semibold mb-4">Create / Edit Position</h3>
                    <form action="${pageContext.request.contextPath}/mo/positions" method="post"
                        class="grid gap-4 grid-cols-1 md:grid-cols-2">
                        <input type="hidden" name="action" value="${not empty editPosition ? 'update' : 'create'}" />
                        <input type="hidden" name="id" value="${editPosition.id}" />

                        <div>
                            <label class="block text-sm font-medium text-gray-700">Course</label>
                            <select name="courseId" required
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2">
                                <option value="">Choose course</option>
                                <c:forEach var="course" items="${courses}">
                                    <option value="${course.id}" ${editPosition.courseId==course.id ? 'selected' : '' }
                                        ${course.status !='active' && editPosition.courseId !=course.id ? 'disabled'
                                        : '' }>${course.name} (${course.id}) - ${course.status}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700">Position Title</label>
                            <input name="title" value="${editPosition.title}" required
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2" />
                        </div>

                        <div class="md:col-span-2">
                            <label class="block text-sm font-medium text-gray-700">Job Description</label>
                            <textarea name="requirements" required
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2">${editPosition.requirements}</textarea>
                        </div>

                        <div class="md:col-span-2">
                            <label class="block text-sm font-medium text-gray-700">Preferred Condition
                                (optional)</label>
                            <input name="preferredCondition" value="${editPosition.preferredCondition}"
                                placeholder="e.g. GPA>=3.0"
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2" />
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700">Minimum GPA (optional)</label>
                            <input name="minGpa" value="${editPosition.minGpa}" placeholder="e.g. 3.2"
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2" />
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700">Minimum English Score
                                (optional)</label>
                            <input name="minEnglishScore" value="${editPosition.minEnglishScore}"
                                placeholder="e.g. 6.5 / 90"
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2" />
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700">Application Deadline</label>
                            <input type="text" inputmode="numeric" pattern="[0-9]{4}-[0-9]{2}-[0-9]{2}"
                                title="Use format YYYY-MM-DD" placeholder="YYYY-MM-DD" name="deadline"
                                value="${editPosition.deadline}"
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2" />
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700">Vacancies</label>
                            <input name="vacancies" value="${editPosition.vacancies}" type="number" min="1"
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2" />
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700">Status</label>
                            <input type="text" value="${empty editPosition ? 'open' : editPosition.status}" readonly
                                class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2 bg-slate-50 text-slate-600" />
                        </div>

                        <div class="md:col-span-2 flex justify-end">
                            <button type="submit"
                                class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700">${not empty
                                editPosition ? 'Save changes' : 'Create position'}</button>
                        </div>
                    </form>
                </div>

                <div class="bg-white p-6 rounded-xl shadow-lg">
                    <h3 class="text-xl font-semibold mb-4">Your Positions</h3>
                    <table class="w-full text-left border-collapse ">
                        <thead>
                            <tr class="bg-slate-100">
                                <th class="px-4 py-3">ID</th>
                                <th class="px-4 py-3">Title</th>
                                <th class="px-4 py-3">Course</th>
                                <th class="px-4 py-3">Deadline</th>
                                <th class="px-4 py-3">Vacancies</th>
                                <th class="px-4 py-3">Status</th>
                                <th class="px-4 py-3">Preferred</th>
                                <th class="px-4 py-3">Hard Criteria</th>
                                <th class="px-4 py-3">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="pos" items="${positions}">
                                <tr class="border-t">
                                    <td class="px-4 py-3">${pos.id}</td>
                                    <td class="px-4 py-3">${pos.title}</td>
                                    <td class="px-4 py-3">${pos.courseName} (${pos.courseId})</td>
                                    <td class="px-4 py-3">${pos.deadline}</td>
                                    <td class="px-4 py-3">${pos.vacancies}</td>
                                    <td class="px-4 py-3">${pos.status}</td>
                                    <td class="px-4 py-3">${pos.preferredCondition}</td>
                                    <td class="px-4 py-3">GPA &gt;= ${empty pos.minGpa ? '-' : pos.minGpa}, English
                                        &gt;= ${empty pos.minEnglishScore ? '-' : pos.minEnglishScore}</td>
                                    <td class="px-4 py-3 space-x-2">
                                        <c:choose>
                                            <c:when test="${pos.status == 'closed'}">
                                                <a href="${pageContext.request.contextPath}/mo/positions?editId=${pos.id}"
                                                    class="text-blue-600 hover:text-blue-800">Edit</a>
                                                <form action="${pageContext.request.contextPath}/mo/positions"
                                                    method="post" style="display:inline"
                                                    onsubmit="return confirm('Are you sure you want to delete this position? This action cannot be undone.');">
                                                    <input type="hidden" name="action" value="delete" />
                                                    <input type="hidden" name="id" value="${pos.id}" />
                                                    <button type="submit"
                                                        class="text-red-600 hover:text-red-800">Delete</button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-slate-400">Locked (open)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </body>

        </html>