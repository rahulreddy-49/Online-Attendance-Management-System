<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Faculty Dashboard" />
<c:set var="activeMenu" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Welcome Banner -->
<div class="card" style="background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%); color: white; margin-bottom: 2rem;">
    <div class="card-body" style="display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 1.5rem;">
        <div>
            <div style="font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.08em; opacity: 0.85;">Faculty Member Portal</div>
            <h2 style="font-size: 1.8rem; color: white; margin-top: 0.2rem;">Welcome back, ${teacher.fullName}!</h2>
            <div style="font-size: 0.9rem; opacity: 0.9; margin-top: 0.35rem;">
                Employee ID: <strong>${teacher.employeeId}</strong> • Department of <strong>${teacher.departmentName}</strong>
            </div>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/teacher/attendance/mark" class="btn btn-secondary" style="background: white; color: var(--primary); font-weight: 700;">
                ✍️ Take Attendance Now
            </a>
        </div>
    </div>
</div>

<!-- Stats Grid -->
<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-content">
            <h3>Assigned Classes</h3>
            <div class="stat-number">${assignments != null ? assignments.size() : 0}</div>
            <div class="stat-subtext">Subject Sections</div>
        </div>
        <div class="stat-icon">📚</div>
    </div>

    <div class="stat-card">
        <div class="stat-content">
            <h3>Today's Sessions</h3>
            <div class="stat-number">${todaySessions != null ? todaySessions : 0}</div>
            <div class="stat-subtext">Completed Today</div>
        </div>
        <div class="stat-icon emerald">🕒</div>
    </div>

    <div class="stat-card">
        <div class="stat-content">
            <h3>Total Sessions</h3>
            <div class="stat-number">${totalSessions != null ? totalSessions : 0}</div>
            <div class="stat-subtext">Lifetime Recorded</div>
        </div>
        <div class="stat-icon cyan">📊</div>
    </div>
</div>

<!-- Assigned Classes List -->
<div class="card">
    <div class="card-header">
        <h2>My Assigned Classes</h2>
        <span class="badge badge-role">Current Semester</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table">
                <thead>
                    <tr>
                        <th>Subject Code</th>
                        <th>Subject Title</th>
                        <th>Academic Year</th>
                        <th>Class Section</th>
                        <th>Department</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty assignments}">
                            <c:forEach var="a" items="${assignments}">
                                <tr>
                                    <td><span class="badge badge-role">${a.subjectCode}</span></td>
                                    <td><strong>${a.subjectName}</strong></td>
                                    <td>${a.academicYearLabel}</td>
                                    <td>Section ${a.sectionName}</td>
                                    <td>${a.departmentName}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/teacher/attendance/mark?assignmentId=${a.id}" class="btn btn-primary btn-sm">
                                            Mark Attendance
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    You have no assigned classes at the moment. Please contact the administrator.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Recent Sessions -->
<div class="card">
    <div class="card-header">
        <h2>Recent Attendance Submissions</h2>
        <a href="${pageContext.request.contextPath}/teacher/attendance/history" class="btn btn-secondary btn-sm">View All History</a>
    </div>
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Subject</th>
                        <th>Class</th>
                        <th>Present</th>
                        <th>Absent</th>
                        <th>Turnout</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty recentSessions}">
                            <c:forEach var="s" items="${recentSessions}">
                                <tr>
                                    <td><strong>${s.sessionDate}</strong></td>
                                    <td>${s.subjectName} (${s.subjectCode})</td>
                                    <td>${s.yearLabel} - Section ${s.sectionName}</td>
                                    <td><span class="badge badge-present">${s.presentCount}</span></td>
                                    <td><span class="badge badge-absent">${s.absentCount}</span></td>
                                    <td>
                                        <c:set var="pct" value="${s.totalStudents > 0 ? (s.presentCount * 100.0 / s.totalStudents) : 0}" />
                                        <span class="percentage-pill ${pct >= 75 ? 'percentage-good' : (pct >= 60 ? 'percentage-warning' : 'percentage-danger')}">
                                            <fmt:formatNumber value="${pct}" maxFractionDigits="1" />%
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/teacher/attendance/view/${s.id}" class="btn btn-secondary btn-sm">Review</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    No sessions recorded yet.
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
