<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <html>

        <head>
            <title>Review Applications - BUPT Recruit</title>
            <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
        </head>

        <body class="bg-slate-50 p-10">
            <div class="max-w-6xl mx-auto">
                <div class="flex justify-between items-center mb-4">
                    <h2 class="text-3xl font-bold">Review TA Applications</h2>
                    <div class="flex items-center gap-3">
                        <a href="${pageContext.request.contextPath}/mo/review/export?status=${filterStatus}&sort=${sortOrder}&sortBy=${sortBy}"
                            class="px-4 py-2 rounded-lg bg-green-600 text-white hover:bg-green-700 border border-green-700 text-sm font-medium">Export
                            Candidates (Excel)</a>
                        <a href="${pageContext.request.contextPath}/dashboard"
                            class="text-blue-600 hover:text-blue-800">Back to Dashboard</a>
                    </div>
                </div>
                <div class="mb-2 text-gray-500 text-sm">Total applications for you: ${totalCount}</div>
                <div class="mb-6 flex flex-wrap items-center justify-between gap-2">
                    <div class="flex gap-2">
                        <a href="review?status=all&sort=${sortOrder}"
                            class="px-3 py-1 rounded-lg ${filterStatus == 'all' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">All
                            (${pendingCount + acceptedCount + rejectedCount + waitlistCount})</a>
                        <a href="review?status=pending&sort=${sortOrder}"
                            class="px-3 py-1 rounded-lg ${filterStatus == 'pending' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">Pending
                            (${pendingCount})</a>
                        <a href="review?status=accepted&sort=${sortOrder}"
                            class="px-3 py-1 rounded-lg ${filterStatus == 'accepted' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">Accepted
                            (${acceptedCount})</a>
                        <a href="review?status=rejected&sort=${sortOrder}"
                            class="px-3 py-1 rounded-lg ${filterStatus == 'rejected' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">Rejected
                            (${rejectedCount})</a>
                        <a href="review?status=waitlist&sort=${sortOrder}"
                            class="px-3 py-1 rounded-lg ${filterStatus == 'waitlist' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">Waitlist
                            (${waitlistCount})</a>
                    </div>
                    <div class="flex items-center gap-2 text-sm">
                        <span>Sort by:</span>
                        <a href="review?status=${filterStatus}&sort=${sortOrder}&sortBy=time"
                            class="px-2 py-1 rounded-lg ${sortBy == 'time' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">Apply
                            time</a>
                        <a href="review?status=${filterStatus}&sort=${sortOrder}&sortBy=gpa"
                            class="px-2 py-1 rounded-lg ${sortBy == 'gpa' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">GPA</a>
                        <a href="review?status=${filterStatus}&sort=${sortOrder}&sortBy=major"
                            class="px-2 py-1 rounded-lg ${sortBy == 'major' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-700'}">Major</a>
                        <span>Sort by apply time:</span>
                        <a href="review?status=${filterStatus}&sort=desc&sortBy=${sortBy}"
                            class="px-2 py-1 rounded-lg ${sortOrder == 'desc' ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-700'}">Desc</a>
                        <a href="review?status=${filterStatus}&sort=asc&sortBy=${sortBy}"
                            class="px-2 py-1 rounded-lg ${sortOrder == 'asc' ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-700'}">Asc</a>
                    </div>
                </div>

                <c:if test="${param.error == 'vacanciesExceeded'}">
                    <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Cannot accept more applicants: the position
                        has reached its vacancies limit.</div>
                </c:if>
                <c:if test="${param.error == 'forbidden'}">
                    <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">You can only review applications for your
                        own courses.</div>
                </c:if>
                <c:if test="${param.error == 'invalidApp'}">
                    <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Invalid application request.</div>
                </c:if>
                <c:if test="${param.error == 'reasonTooShort'}">
                    <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-800">Reject reason must be at least 10
                        characters.</div>
                </c:if>

                <c:if test="${empty apps}">
                    <div class="mb-4 p-4 rounded-lg bg-yellow-100 text-yellow-800">No applications found for your
                        positions yet.</div>
                </c:if>
                <div class="grid gap-6">
                    <c:forEach var="app" items="${apps}">
                        <div class="bg-white p-6 rounded-xl shadow-md flex justify-between items-start">
                            <div class="space-y-2">
                                <h4 class="font-bold text-xl">TA ${app.studentId} - ${app.positionTitle}</h4>
                                <p class="text-sm text-slate-600">Course: ${app.courseName} (${app.courseId})</p>
                                <p class="text-xs text-slate-400 italic">Applied on: ${app.createdAt}</p>
                            </div>

                            <div class="flex flex-col gap-3 items-end">
                                <button onclick="showDetails('${app.id}')"
                                    class="px-4 py-2 border rounded-lg hover:bg-slate-50">View Details</button>
                                <c:if test="${app.status == 'pending'}">
                                    <form action="review" method="post" class="flex gap-2">
                                        <input type="hidden" name="appId" value="${app.id}">
                                        <input type="text" name="reason" placeholder="Reason (optional)"
                                            class="px-3 py-1 border rounded-lg text-sm">
                                        <button type="submit" name="status" value="accepted"
                                            class="px-4 py-1 bg-green-600 text-white rounded-lg">Accept</button>
                                        <button type="submit" name="status" value="waitlist"
                                            class="px-4 py-1 bg-amber-600 text-white rounded-lg">Waitlist</button>
                                        <button type="submit" name="status" value="rejected"
                                            class="px-4 py-1 bg-red-600 text-white rounded-lg">Reject</button>
                                    </form>
                                </c:if>
                                <c:if test="${app.status != 'pending'}">
                                    <span
                                        class="px-3 py-1 rounded-full text-xs font-bold uppercase ${app.status == 'accepted' ? 'bg-green-100 text-green-700' : (app.status == 'waitlist' ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-700')}">
                                        ${app.status}
                                    </span>
                                </c:if>
                            </div>
                        </div>

                        <div id="resume-${app.id}" class="hidden">
                            <h4 class="text-lg font-semibold">TA ${app.studentId} Resume</h4>
                            <c:choose>
                                <c:when test="${not empty app.resume}">
                                    <p><strong>Name:</strong> ${app.resume.name}</p>
                                    <p><strong>Email:</strong> ${app.resume.email}</p>
                                    <p><strong>Major:</strong> ${app.resume.major}</p>
                                    <p><strong>Grade:</strong> ${app.resume.grade}</p>
                                    <p><strong>GPA:</strong> ${app.resume.gpa}</p>
                                    <p><strong>English Score:</strong> ${app.resume.englishScore}</p>
                                    <p><strong>Skills:</strong> ${app.resume.skills}</p>
                                    <p><strong>Related Courses:</strong> ${app.resume.relatedCourses}</p>
                                    <p><strong>Awards:</strong> ${app.resume.awards}</p>
                                    <p><strong>Projects:</strong> ${app.resume.projects}</p>
                                    <p><strong>Experience:</strong> ${app.resume.experience}</p>
                                    <p><strong>Competency:</strong> ${app.resume.competency}</p>
                                    <p><strong>Work Hours:</strong> ${app.resume.workHours}</p>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-sm text-gray-500">No resume data found for this TA.</p>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${not empty app.resumePdfPath}">
                                <p><strong>PDF:</strong> <a class="text-blue-600 hover:text-blue-800"
                                        href="${pageContext.request.contextPath}/mo/resume-pdf?appId=${app.id}">Download
                                        PDF</a></p>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Details Modal (Simplified) -->
            <div id="detailsModal" class="hidden fixed inset-0 bg-black/50 flex items-center justify-center p-4">
                <div class="bg-white p-8 rounded-xl max-w-2xl w-full max-h-[90vh] overflow-auto">
                    <h3 class="text-2xl font-bold mb-6">Applicant Details</h3>
                    <div id="detailsContent" class="space-y-4">
                        <!-- Content injected via JS -->
                    </div>
                    <button onclick="closeDetails()" class="mt-8 w-full py-2 bg-slate-100 rounded-lg">Close</button>
                </div>
            </div>

            <script>
                function showDetails(appId) {
                    var modal = document.getElementById('detailsModal');
                    var content = document.getElementById('detailsContent');
                    var reserve = document.getElementById('resume-' + appId);
                    if (reserve) {
                        content.innerHTML = reserve.innerHTML;
                    } else {
                        content.innerHTML = '<p class="text-sm text-gray-500">No resume data available.</p>';
                    }
                    modal.classList.remove('hidden');
                }
                function closeDetails() {
                    document.getElementById('detailsModal').classList.add('hidden');
                }
            </script>
        </body>

        </html>