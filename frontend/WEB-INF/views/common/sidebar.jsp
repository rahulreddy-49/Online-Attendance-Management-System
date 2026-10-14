<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="sidebar">
    <div class="sidebar-header">
        <div class="brand-icon">✓</div>
        <div class="brand-text">
            <h2>Attendify</h2>
            <span>Attendance Pro</span>
        </div>
    </div>

    <nav class="sidebar-nav">
        <c:choose>
            <c:when test="${sessionScope.user != null && sessionScope.user.role == 'ADMIN'}">
                <div class="nav-section-title">Administration</div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item ${activeMenu == 'dashboard' ? 'active' : ''}">
                    <span class="nav-icon">📊</span>
                    <span>Dashboard</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/students" class="nav-item ${activeMenu == 'students' ? 'active' : ''}">
                    <span class="nav-icon">🎓</span>
                    <span>Students</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/teachers" class="nav-item ${activeMenu == 'teachers' ? 'active' : ''}">
                    <span class="nav-icon">👨‍🏫</span>
                    <span>Faculty & Teachers</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/subjects" class="nav-item ${activeMenu == 'subjects' ? 'active' : ''}">
                    <span class="nav-icon">📚</span>
                    <span>Subjects</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/assignments" class="nav-item ${activeMenu == 'assignments' ? 'active' : ''}">
                    <span class="nav-icon">📋</span>
                    <span>Class Assignments</span>
                </a>

                <div class="nav-section-title">Analytics & Reports</div>
                <a href="${pageContext.request.contextPath}/admin/reports" class="nav-item ${activeMenu == 'reports' ? 'active' : ''}">
                    <span class="nav-icon">📈</span>
                    <span>Attendance Reports</span>
                </a>
            </c:when>
            <c:otherwise>
                <div class="nav-section-title">Faculty Portal</div>
                <a href="${pageContext.request.contextPath}/teacher/dashboard" class="nav-item ${activeMenu == 'dashboard' ? 'active' : ''}">
                    <span class="nav-icon">📊</span>
                    <span>My Dashboard</span>
                </a>
                <a href="${pageContext.request.contextPath}/teacher/attendance/mark" class="nav-item ${activeMenu == 'attendance' ? 'active' : ''}">
                    <span class="nav-icon">✍️</span>
                    <span>Mark Attendance</span>
                </a>
                <a href="${pageContext.request.contextPath}/teacher/attendance/history" class="nav-item ${activeMenu == 'history' ? 'active' : ''}">
                    <span class="nav-icon">🕒</span>
                    <span>Attendance History</span>
                </a>
                <a href="${pageContext.request.contextPath}/teacher/reports" class="nav-item ${activeMenu == 'reports' ? 'active' : ''}">
                    <span class="nav-icon">📑</span>
                    <span>Class Reports</span>
                </a>
            </c:otherwise>
        </c:choose>
    </nav>

    <div class="sidebar-footer">
        <div class="user-badge">
            <div class="user-avatar">
                ${sessionScope.userName != null ? sessionScope.userName.substring(0, 1).toUpperCase() : 'U'}
            </div>
            <div class="user-info">
                <div class="user-name">${sessionScope.userName != null ? sessionScope.userName : 'User'}</div>
                <div class="user-role">${sessionScope.userRole != null ? sessionScope.userRole : 'Guest'}</div>
            </div>
        </div>
    </div>
</aside>
