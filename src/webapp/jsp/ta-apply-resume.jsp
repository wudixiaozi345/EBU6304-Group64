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
        <c:if test="${param.uploadError == 'true'}">
            <div class="bg-red-100 text-red-800 p-3 rounded mb-4">Resume PDF must be a valid .pdf file and <= 10MB.</div>
        </c:if>
        <c:if test="${param.pdfRequired == 'true'}">
            <div class="bg-red-100 text-red-800 p-3 rounded mb-4">Please upload a PDF resume when PDF mode is selected.</div>
        </c:if>
        <c:if test="${param.timeRequired == 'true'}">
            <div class="bg-red-100 text-red-800 p-3 rounded mb-4">Please select an available time slot.</div>
        </c:if>
        <c:if test="${param.draftSaved == 'true'}">
            <div class="bg-green-100 text-green-800 p-3 rounded mb-4">Draft saved successfully. It will be kept for up to 7 days.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/ta/apply" method="post" enctype="multipart/form-data" class="space-y-6">
            <input type="hidden" name="jobId" value="${jobId}" />
            <input type="hidden" name="existingPdfPath" value="${draft.resumePdfPath}" />
            <input type="hidden" id="formAction" name="action" value="submit" />

            <div class="p-4 border rounded-lg bg-slate-50">
                <label class="block text-sm font-medium text-slate-700 mb-2">Resume Submission Mode</label>
                <div class="flex flex-wrap gap-6">
                    <label class="inline-flex items-center gap-2">
                        <input type="radio" name="resumeMode" value="online" ${empty draft.mode || draft.mode == 'online' ? 'checked' : ''} />
                        <span>Fill Online Resume</span>
                    </label>
                    <label class="inline-flex items-center gap-2">
                        <input type="radio" name="resumeMode" value="pdf" ${draft.mode == 'pdf' ? 'checked' : ''} />
                        <span>Upload PDF (No online fields required)</span>
                    </label>
                </div>
                <p class="text-xs text-slate-500 mt-2">Both modes support Save Draft (expires in 7 days). Available time slot is always required.</p>
            </div>

            <div id="onlineResumeFields" class="space-y-6 ${draft.mode == 'pdf' ? 'hidden' : ''}">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label class="block text-sm font-medium text-slate-700">Full Name</label>
                        <input type="text" name="name" value="${draftResume.name}" class="w-full px-4 py-2 border rounded-lg online-input" />
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700">Email Address</label>
                        <input type="email" name="email" value="${draftResume.email}" class="w-full px-4 py-2 border rounded-lg online-input" />
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700">Major / Program</label>
                        <input type="text" name="major" value="${draftResume.major}" class="w-full px-4 py-2 border rounded-lg online-input" />
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700">Current Grade / Year</label>
                        <input type="text" name="grade" value="${draftResume.grade}" class="w-full px-4 py-2 border rounded-lg online-input" />
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700">GPA</label>
                        <input type="text" name="gpa" value="${draftResume.gpa}" class="w-full px-4 py-2 border rounded-lg online-input" />
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700">English Score</label>
                        <input type="text" name="englishScore" value="${draftResume.englishScore}" class="w-full px-4 py-2 border rounded-lg online-input" />
                    </div>
                </div>

                <div>
                    <label class="block text-sm font-medium text-slate-700">Technical Skills</label>
                    <input type="text" name="skills" value="${draftResume.skills}" class="w-full px-4 py-2 border rounded-lg online-input" />
                </div>

                <div>
                    <label class="block text-sm font-medium text-slate-700">Related Courses</label>
                    <input type="text" name="relatedCourses" value="${draftResume.relatedCourses}" class="w-full px-4 py-2 border rounded-lg online-input" />
                </div>

                <div>
                    <label class="block text-sm font-medium text-slate-700">Awards & Honors</label>
                    <textarea name="awards" class="w-full px-4 py-2 border rounded-lg h-24 online-input">${draftResume.awards}</textarea>
                </div>

                <div>
                    <label class="block text-sm font-medium text-slate-700">Research & Projects</label>
                    <textarea name="projects" class="w-full px-4 py-2 border rounded-lg h-32 online-input">${draftResume.projects}</textarea>
                </div>

                <div>
                    <label class="block text-sm font-medium text-slate-700">Previous TA Experience</label>
                    <textarea name="experience" class="w-full px-4 py-2 border rounded-lg h-32 online-input">${draftResume.experience}</textarea>
                </div>

                <div>
                    <label class="block text-sm font-medium text-slate-700">Competency Description</label>
                    <textarea name="competency" class="w-full px-4 py-2 border rounded-lg h-40 online-input">${draftResume.competency}</textarea>
                </div>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Available Time Slot</label>
                <p class="text-xs text-slate-500 mb-1">You must choose one slot. If none is chosen, browser required validation will block submit.</p>
                <select name="workHours" required class="w-full px-4 py-2 border rounded-lg bg-white">
                    <option value="">Select a time slot</option>
                    <option value="Weekday Morning" ${draftResume.workHours == 'Weekday Morning' ? 'selected' : ''}>Weekday Morning</option>
                    <option value="Weekday Afternoon" ${draftResume.workHours == 'Weekday Afternoon' ? 'selected' : ''}>Weekday Afternoon</option>
                    <option value="Weekday Evening" ${draftResume.workHours == 'Weekday Evening' ? 'selected' : ''}>Weekday Evening</option>
                    <option value="Weekend Morning" ${draftResume.workHours == 'Weekend Morning' ? 'selected' : ''}>Weekend Morning</option>
                    <option value="Weekend Afternoon" ${draftResume.workHours == 'Weekend Afternoon' ? 'selected' : ''}>Weekend Afternoon</option>
                </select>
            </div>

            <div>
                <label class="block text-sm font-medium text-slate-700">Upload PDF Resume (optional, max 10MB)</label>
                <input type="file" name="resumePdf" accept="application/pdf,.pdf" class="w-full px-4 py-2 border rounded-lg bg-white" />
                <c:if test="${not empty draft.resumePdfPath}">
                    <p class="text-xs text-slate-500 mt-1">Draft contains an uploaded PDF and can be submitted directly in PDF mode.</p>
                </c:if>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <button type="button" onclick="submitAsDraft(this.form)" style="width:100%; padding:16px 20px; background:#374151; color:#ffffff; border:1px solid #1f2937; border-radius:12px; font-size:28px; font-weight:700; line-height:1; cursor:pointer;">Save Draft (7 days)</button>
                <button type="submit" onclick="document.getElementById('formAction').value='submit'" class="w-full py-4 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors text-lg font-bold">Submit Application</button>
            </div>
        </form>
    </div>

    <script>
        (function () {
            function updateOnlineRequired() {
                var mode = document.querySelector('input[name="resumeMode"]:checked');
                var isOnline = !mode || mode.value === 'online';
                var fields = document.querySelectorAll('.online-input');
                var onlineSection = document.getElementById('onlineResumeFields');
                fields.forEach(function (el) {
                    if (isOnline) {
                        el.setAttribute('required', 'required');
                    } else {
                        el.removeAttribute('required');
                    }
                });
                if (onlineSection) {
                    onlineSection.classList.toggle('hidden', !isOnline);
                }
            }

            var radios = document.querySelectorAll('input[name="resumeMode"]');
            radios.forEach(function (r) { r.addEventListener('change', updateOnlineRequired); });
            updateOnlineRequired();
        })();

        function submitAsDraft(form) {
            var actionInput = document.getElementById('formAction');
            if (actionInput) {
                actionInput.value = 'saveDraft';
            }
            // Direct submit bypasses HTML required validation for draft save.
            form.submit();
        }
    </script>
</body>
</html>
