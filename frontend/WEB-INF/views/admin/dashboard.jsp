<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Administrator Dashboard" />
<c:set var="activeMenu" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Stats Grid -->
<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-content">
            <h3>Total Students</h3>
            <div class="stat-number">${totalStudents != null ? totalStudents : 0}</div>
            <div class="stat-subtext">Active Enrolled</div>
        </div>
        <div class="stat-icon">🎓</div>
    </div>

    <div class="stat-card">
        <div class="stat-content">
            <h3>Faculty Members</h3>
            <div class="stat-number">${totalTeachers != null ? totalTeachers : 0}</div>
            <div class="stat-subtext">Active Teachers</div>
        </div>
        <div class="stat-icon emerald">👨‍🏫</div>
    </div>

    <div class="stat-card">
        <div class="stat-content">
            <h3>Courses / Subjects</h3>
            <div class="stat-number">${totalSubjects != null ? totalSubjects : 0}</div>
            <div class="stat-subtext">Curriculum Modules</div>
        </div>
        <div class="stat-icon cyan">📚</div>
    </div>

    <div class="stat-card">
        <div class="stat-content">
            <h3>Today's Sessions</h3>
            <div class="stat-number">${todaySessions != null ? todaySessions : 0}</div>
            <div class="stat-subtext">Conducted Today</div>
        </div>
        <div class="stat-icon amber">🕒</div>
    </div>
</div>

<!-- Overall Metric Banner -->
<c:if test="${not empty overallStats}">
    <div class="card" style="margin-bottom: 2rem; background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%); color: white;">
        <div class="card-body" style="display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 1.5rem;">
            <div>
                <h3 style="color: #cbd5e1; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em;">Institutional Attendance Rate</h3>
                <div style="font-size: 2.2rem; font-weight: 800; font-family: var(--font-heading); color: #38bdf8;">
                    ${overallStats.overall_percentage}%
                </div>
            </div>
            <div style="display: flex; gap: 2rem; flex-wrap: wrap;">
                <div>
                    <div style="color: #94a3b8; font-size: 0.75rem;">Total Sessions Recorded</div>
                    <div style="font-size: 1.2rem; font-weight: 700;">${overallStats.total_sessions}</div>
                </div>
                <div>
                    <div style="color: #94a3b8; font-size: 0.75rem;">Present Records</div>
                    <div style="font-size: 1.2rem; font-weight: 700; color: #34d399;">${overallStats.total_present}</div>
                </div>
                <div>
                    <div style="color: #94a3b8; font-size: 0.75rem;">Absent Records</div>
                    <div style="font-size: 1.2rem; font-weight: 700; color: #f87171;">${overallStats.total_absent}</div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Quick Actions -->
<div class="card">
    <div class="card-header">
        <h2>Quick Management Actions</h2>
    </div>
    <div class="card-body" style="display: flex; flex-wrap: wrap; gap: 1rem;">
        <a href="${pageContext.request.contextPath}/admin/students/add" class="btn btn-primary">+ Register Student</a>
        <a href="${pageContext.request.contextPath}/admin/teachers/add" class="btn btn-secondary">+ Add Teacher</a>
        <a href="${pageContext.request.contextPath}/admin/subjects/add" class="btn btn-secondary">+ Add Subject</a>
        <a href="${pageContext.request.contextPath}/admin/assignments/add" class="btn btn-secondary">Assign Teacher to Class</a>
        <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">📊 View Attendance Reports</a>
    </div>
</div>

<!-- Recent Sessions Table -->
<div class="card">
    <div class="card-header">
        <h2>Recent Attendance Sessions</h2>
        <span class="badge badge-role">Last 10 Records</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Subject</th>
                        <th>Faculty</th>
                        <th>Class / Section</th>
                        <th>Present</th>
                        <th>Absent</th>
                        <th>Turnout</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty recentSessions}">
                            <c:forEach var="sess" items="${recentSessions}">
                                <tr>
                                    <td><strong>${sess.sessionDate}</strong></td>
                                    <td>
                                        <div><strong>${sess.subjectName}</strong></div>
                                        <small style="color: var(--text-muted);">${sess.subjectCode}</small>
                                    </td>
                                    <td>${sess.teacherName}</td>
                                    <td>${sess.departmentName} - ${sess.yearLabel} (${sess.sectionName})</td>
                                    <td><span class="badge badge-present">${sess.presentCount}</span></td>
                                    <td><span class="badge badge-absent">${sess.absentCount}</span></td>
                                    <td>
                                        <c:set var="pct" value="${sess.totalStudents > 0 ? (sess.presentCount * 100.0 / sess.totalStudents) : 0}" />
                                        <span class="percentage-pill ${pct >= 75 ? 'percentage-good' : (pct >= 60 ? 'percentage-warning' : 'percentage-danger')}">
                                            <fmt:formatNumber value="${pct}" maxFractionDigits="1" />%
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    No attendance sessions recorded yet.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
