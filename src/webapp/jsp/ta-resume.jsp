<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>My TA Resume - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
    <div class="max-w-4xl mx-auto bg-white p-10 rounded-xl shadow-lg">
        <div class="mb-6">
            <a href="${pageContext.request.contextPath}/dashboard" class="text-blue-600 hover:text-blue-800 font-medium">&larr; Back to Dashboard</a>
        </div>
        <h2 class="text-3xl font-bold mb-8">My TA Resume</h2>
        <c:if test="${param.success == 'true'}">
            <div class="bg-green-100 text-green-800 p-3 rounded mb-4">Resume saved successfully!</div>
        </c:if>
        <c:if test="${param.incomplete == 'true'}">
            <div class="bg-red-100 text-red-800 p-3 rounded mb-4">Please complete your resume before applying for positions.</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/ta/resume" method="post" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label class="block text-sm font-medium text-slate-700">Full Name</label>
                    <input type="text" name="name" value="${resume.name}" required class="w-full px-4 py-2 border rounded-lg">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Email Address</label>
                    <input type="email" name="email" value="${resume.email}" required class="w-full px-4 py-2 border rounded-lg">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Major / Program</label>
                    <input type="text" name="major" value="${resume.major}" required class="w-full px-4 py-2 border rounded-lg">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Current Grade / Year</label>
                    <input type="text" name="grade" value="${resume.grade}" required class="w-full px-4 py-2 border rounded-lg">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">GPA</label>
                    <input type="text" name="gpa" value="${resume.gpa}" required class="w-full px-4 py-2 border rounded-lg">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">English Score</label>
                    <input type="text" name="englishScore" value="${resume.englishScore}" class="w-full px-4 py-2 border rounded-lg">
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Available Work Hours (per week)</label>
                    <input type="number" name="workHours" value="${resume.workHours}" required class="w-full px-4 py-2 border rounded-lg">
                </div>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Technical Skills</label>
                <input type="text" name="skills" value="${resume.skills}" required class="w-full px-4 py-2 border rounded-lg">
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Related Courses</label>
                <input type="text" name="relatedCourses" value="${resume.relatedCourses}" class="w-full px-4 py-2 border rounded-lg">
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Awards & Honors</label>
                <textarea name="awards" class="w-full px-4 py-2 border rounded-lg h-24">${resume.awards}</textarea>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Research & Projects</label>
                <textarea name="projects" class="w-full px-4 py-2 border rounded-lg h-32">${resume.projects}</textarea>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Previous TA Experience</label>
                <textarea name="experience" class="w-full px-4 py-2 border rounded-lg h-32">${resume.experience}</textarea>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Competency Description</label>
                <textarea name="competency" required class="w-full px-4 py-2 border rounded-lg h-40">${resume.competency}</textarea>
            </div>

            <button type="submit" class="w-full py-4 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors text-lg font-bold">Save Resume</button>
        </form>
    </div>
</body>
</html>
