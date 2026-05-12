<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Dashboard - BUPT Recruit</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .gradient-bg {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .card-hover {
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .card-hover:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body class="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen">
    <!-- 左侧导航栏 -->
    <div class="w-80 bg-white shadow-xl h-screen fixed left-0 top-0 overflow-y-auto">
        <div class="gradient-bg p-6">
            <h1 class="text-3xl font-bold text-white mb-2 flex items-center">
                <i class="fas fa-university mr-3"></i>
                BUPT Recruit
            </h1>
            <p class="text-blue-100 text-sm">Teaching Assistant System</p>
        </div>

        <c:choose>
            <%-- TA/学生角色 --%>
            <c:when test="${user.role == 'ta'}">
                <nav class="p-6 space-y-3">
                    <div class="text-gray-500 text-sm font-semibold uppercase tracking-wide mb-4">Student Dashboard</div>
                          <a href="${pageContext.request.contextPath}/ta/profile"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-user-circle text-blue-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">My Profile</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/ta-jobs"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-briefcase text-purple-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Apply for Jobs</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/ta-apps"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-list-check text-orange-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">My Applications</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/ta/feedback"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-comment-dots text-teal-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Feedback to Admin</span>
                    </a>
                    <hr class="my-6 border-gray-200">
                    <a href="${pageContext.request.contextPath}/logout"
                       class="flex items-center p-4 rounded-xl hover:bg-red-50 text-red-600 transition-all duration-200 group">
                        <i class="fas fa-sign-out-alt mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Logout</span>
                    </a>
                </nav>
            </c:when>

            <%-- Admin角色 --%>
            <c:when test="${user.role == 'admin'}">
                <nav class="p-6 space-y-3">
                    <div class="text-gray-500 text-sm font-semibold uppercase tracking-wide mb-4">Admin Dashboard</div>
                    <a href="${pageContext.request.contextPath}/admin/mos"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-users-cog text-blue-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">User Management</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/positions"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-chart-line text-green-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Position Monitoring</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/courses"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-book text-emerald-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Course Management</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/settings"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-cog text-purple-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">System Settings</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/feedback"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-comments text-amber-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Feedback Management</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/profile"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-home text-gray-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Home</span>
                    </a>
                    <hr class="my-6 border-gray-200">
                    <a href="${pageContext.request.contextPath}/logout"
                       class="flex items-center p-4 rounded-xl hover:bg-red-50 text-red-600 transition-all duration-200 group">
                        <i class="fas fa-sign-out-alt mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Logout</span>
                    </a>
                </nav>
            </c:when>

            <%-- MO角色 --%>
            <c:when test="${user.role == 'mo'}">
                <nav class="p-6 space-y-3">
                    <div class="text-gray-500 text-sm font-semibold uppercase tracking-wide mb-4">MO Dashboard</div>
                    <a href="${pageContext.request.contextPath}/mo/positions"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-briefcase text-blue-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Position Management</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/mo/review"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-check-double text-green-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Application Review</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/mo/courses"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-chalkboard-teacher text-purple-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">My Courses</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/mo/feedback"
                       class="flex items-center p-4 rounded-xl hover:bg-blue-50 transition-all duration-200 group">
                        <i class="fas fa-comment-dots text-teal-500 mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Feedback to Admin</span>
                    </a>
                    <hr class="my-6 border-gray-200">
                    <a href="${pageContext.request.contextPath}/logout"
                       class="flex items-center p-4 rounded-xl hover:bg-red-50 text-red-600 transition-all duration-200 group">
                        <i class="fas fa-sign-out-alt mr-4 text-lg group-hover:scale-110 transition-transform"></i>
                        <span class="font-medium">Logout</span>
                    </a>
                </nav>
            </c:when>
        </c:choose>
    </div>

    <!-- 右侧内容 -->
    <div class="flex-1 p-10 ml-80">
        <div class="mb-8">
            <h2 class="text-5xl font-bold text-gray-800 mb-2">
                Welcome back, <span class="text-blue-600">${user.name}</span>!
            </h2>
            <p class="text-gray-600 text-lg">Here's what's happening in your BUPT Recruit dashboard today.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <!-- 系统状态卡片 -->
            <div class="bg-white p-8 rounded-2xl shadow-lg card-hover border-l-4 border-green-500">
                <div class="flex items-center justify-between mb-6">
                    <div class="p-3 bg-green-100 rounded-full">
                        <i class="fas fa-server text-green-600 text-2xl"></i>
                    </div>
                    <div class="text-right">
                        <p class="text-sm text-gray-500">System Status</p>
                        <p class="text-3xl font-bold text-green-600">Online</p>
                    </div>
                </div>
                <div class="text-gray-600">
                    <p class="text-sm">All systems are running smoothly. No issues detected.</p>
                </div>
            </div>

            <!-- 快速统计卡片 -->
            <c:if test="${user.role == 'ta'}">
                <div class="bg-white p-8 rounded-2xl shadow-lg card-hover border-l-4 border-blue-500">
                    <div class="flex items-center justify-between mb-6">
                        <div class="p-3 bg-blue-100 rounded-full">
                            <i class="fas fa-briefcase text-blue-600 text-2xl"></i>
                        </div>
                        <div class="text-right">
                            <p class="text-sm text-gray-500">Active Applications</p>
                            <p class="text-3xl font-bold text-blue-600">--</p>
                        </div>
                    </div>
                    <div class="text-gray-600">
                        <p class="text-sm">Check your application status and apply for new positions.</p>
                    </div>
                </div>
            </c:if>

            <c:if test="${user.role == 'mo'}">
                <div class="bg-white p-8 rounded-2xl shadow-lg card-hover border-l-4 border-purple-500">
                    <div class="flex items-center justify-between mb-6">
                        <div class="p-3 bg-purple-100 rounded-full">
                            <i class="fas fa-users text-purple-600 text-2xl"></i>
                        </div>
                        <div class="text-right">
                            <p class="text-sm text-gray-500">Pending Reviews</p>
                            <p class="text-3xl font-bold text-purple-600">${moPendingReviews}</p>
                        </div>
                    </div>
                    <div class="text-gray-600">
                        <p class="text-sm">Review TA applications for your courses.</p>
                    </div>
                </div>

                <div class="bg-white p-8 rounded-2xl shadow-lg card-hover border-l-4 border-indigo-500">
                    <div class="flex items-center justify-between mb-6">
                        <div class="p-3 bg-indigo-100 rounded-full">
                            <i class="fas fa-briefcase text-indigo-600 text-2xl"></i>
                        </div>
                        <div class="text-right">
                            <p class="text-sm text-gray-500">Your Open Positions</p>
                            <p class="text-3xl font-bold text-indigo-600">${moTotalPositions}</p>
                        </div>
                    </div>
                    <div class="text-gray-600">
                        <p class="text-sm">Create, edit, delete your positions and keep them in sync with TA job list.</p>
                    </div>
                </div>
            </c:if>

            <c:if test="${user.role == 'admin'}">
                <div class="bg-white p-8 rounded-2xl shadow-lg card-hover border-l-4 border-orange-500">
                    <div class="flex items-center justify-between mb-6">
                        <div class="p-3 bg-orange-100 rounded-full">
                            <i class="fas fa-cogs text-orange-600 text-2xl"></i>
                        </div>
                        <div class="text-right">
                            <p class="text-sm text-gray-500">System Management</p>
                            <p class="text-3xl font-bold text-orange-600">Active</p>
                        </div>
                    </div>
                    <div class="text-gray-600">
                        <p class="text-sm">Manage users, courses, and system settings.</p>
                    </div>
                </div>
            </c:if>

            <!-- 快速操作卡片 -->
            <div class="bg-white p-8 rounded-2xl shadow-lg card-hover border-l-4 border-indigo-500">
                <div class="flex items-center justify-between mb-6">
                    <div class="p-3 bg-indigo-100 rounded-full">
                        <i class="fas fa-rocket text-indigo-600 text-2xl"></i>
                    </div>
                    <div class="text-right">
                        <p class="text-sm text-gray-500">Quick Actions</p>
                        <p class="text-lg font-bold text-indigo-600">Get Started</p>
                    </div>
                </div>
                <div class="space-y-2">
                    <c:if test="${user.role == 'ta'}">
                        <a href="${pageContext.request.contextPath}/ta-jobs" class="block text-sm text-indigo-600 hover:text-indigo-800 font-medium">
                            <i class="fas fa-plus mr-2"></i>Apply for a new position
                        </a>
                        <a href="${pageContext.request.contextPath}/ta-apps" class="block text-sm text-indigo-600 hover:text-indigo-800 font-medium">
                            <i class="fas fa-eye mr-2"></i>View my applications
                        </a>
                    </c:if>
                    <c:if test="${user.role == 'mo'}">
                        <a href="${pageContext.request.contextPath}/mo/review" class="block text-sm text-indigo-600 hover:text-indigo-800 font-medium">
                            <i class="fas fa-clipboard-check mr-2"></i>Review applications
                        </a>
                        <a href="${pageContext.request.contextPath}/mo/positions" class="block text-sm text-indigo-600 hover:text-indigo-800 font-medium">
                            <i class="fas fa-briefcase mr-2"></i>Manage positions
                        </a>
                    </c:if>
                    <c:if test="${user.role == 'admin'}">
                        <a href="${pageContext.request.contextPath}/admin/courses" class="block text-sm text-indigo-600 hover:text-indigo-800 font-medium">
                            <i class="fas fa-book-open mr-2"></i>Manage courses
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/positions" class="block text-sm text-indigo-600 hover:text-indigo-800 font-medium">
                            <i class="fas fa-book mr-2"></i>Manage positions
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</body>
</html>