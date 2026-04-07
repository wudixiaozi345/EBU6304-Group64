<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Application Resume - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-slate-50 p-10">
    <div class="max-w-4xl mx-auto bg-white p-10 rounded-xl shadow-lg">
        <div class="mb-6 flex justify-between items-center">
            <a href="${pageContext.request.contextPath}/ta/jobs" class="text-blue-600 hover:text-blue-800 font-medium">&larr; Back to Jobs</a>
            <span class="text-sm text-slate-500">Application Resume</span>
        </div>

        <h2 class="text-3xl font-bold mb-2">Resume for This Application</h2>
        <p class="text-slate-600 mb-1">Position: ${jobTitle}</p>
        <p class="text-slate-600 mb-6">Course: ${courseName}</p>

        <c:if test="${param.resumeIncomplete == 'true'}">
            <div class="bg-red-100 text-red-800 p-3 rounded mb-4">Please complete all resume fields before submitting this application.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/ta/apply" method="post" class="space-y-6">
            <input type="hidden" name="jobId" value="${jobId}" />

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label class="block text-sm font-medium text-slate-700">Full Name</label>
                    <input type="text" name="name" required class="w-full px-4 py-2 border rounded-lg" />
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Email Address</label>
                    <input type="email" name="email" required class="w-full px-4 py-2 border rounded-lg" />
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Major / Program</label>
                    <input type="text" name="major" required class="w-full px-4 py-2 border rounded-lg" />
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Current Grade / Year</label>
                    <input type="text" name="grade" required class="w-full px-4 py-2 border rounded-lg" />
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">GPA</label>
                    <input type="text" name="gpa" required class="w-full px-4 py-2 border rounded-lg" />
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">English Score</label>
                    <input type="text" name="englishScore" required class="w-full px-4 py-2 border rounded-lg" />
                </div>
                <div>
                    <label class="block text-sm font-medium text-slate-700">Available Work Hours (per week)</label>
                    <input type="number" name="workHours" required class="w-full px-4 py-2 border rounded-lg" />
                </div>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Technical Skills</label>
                <input type="text" name="skills" required class="w-full px-4 py-2 border rounded-lg" />
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Related Courses</label>
                <input type="text" name="relatedCourses" required class="w-full px-4 py-2 border rounded-lg" />
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Awards & Honors</label>
                <textarea name="awards" required class="w-full px-4 py-2 border rounded-lg h-24"></textarea>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Research & Projects</label>
                <textarea name="projects" required class="w-full px-4 py-2 border rounded-lg h-32"></textarea>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Previous TA Experience</label>
                <textarea name="experience" required class="w-full px-4 py-2 border rounded-lg h-32"></textarea>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Competency Description</label>
                <textarea name="competency" required class="w-full px-4 py-2 border rounded-lg h-40"></textarea>
            </div>

            <button type="submit" class="w-full py-4 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors text-lg font-bold">Submit Application</button>
        </form>
    </div>
</body>
</html>