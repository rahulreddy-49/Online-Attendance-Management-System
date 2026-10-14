<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Session Attendance Details" />
<c:set var="activeMenu" value="history" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>Attendance Session Details</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">
                Date: <strong>${session.sessionDate}</strong> • Subject: <strong>${session.subjectName} (${session.subjectCode})</strong>
            </p>
        </div>
        <div style="display: flex; gap: 0.5rem;">
            <button onclick="window.print()" class="btn btn-secondary btn-sm">🖨️ Print Sheet</button>
            <a href="${pageContext.request.contextPath}/teacher/attendance/history" class="btn btn-secondary btn-sm">← Back to History</a>
        </div>
    </div>

    <!-- Metadata Banner -->
    <div style="padding: 1.25rem 1.75rem; background: #fbfcfe; border-bottom: 1px solid var(--border-color); display: flex; flex-wrap: wrap; gap: 2rem;">
        <div>
            <span style="font-size: 0.75rem; color: var(--text-muted); font-weight: 600;">CLASS GROUP</span>
            <div style="font-size: 0.95rem; font-weight: 700;">${session.departmentName} - ${session.yearLabel} (Sec ${session.sectionName})</div>
        </div>
        <div>
            <span style="font-size: 0.75rem; color: var(--text-muted); font-weight: 600;">FACULTY</span>
            <div style="font-size: 0.95rem; font-weight: 700;">${session.teacherName}</div>
        </div>
        <div>
            <span style="font-size: 0.75rem; color: var(--text-muted); font-weight: 600;">TOTAL ROSTER</span>
            <div style="font-size: 0.95rem; font-weight: 700;">${session.totalStudents} Students</div>
        </div>
        <div>
            <span style="font-size: 0.75rem; color: var(--success); font-weight: 600;">PRESENT</span>
            <div style="font-size: 0.95rem; font-weight: 700; color: var(--success);">${session.presentCount}</div>
        </div>
        <div>
            <span style="font-size: 0.75rem; color: var(--danger); font-weight: 600;">ABSENT</span>
            <div style="font-size: 0.95rem; font-weight: 700; color: var(--danger);">${session.absentCount}</div>
        </div>
        <div>
            <span style="font-size: 0.75rem; color: var(--primary); font-weight: 600;">ATTENDANCE RATE</span>
            <c:set var="pct" value="${session.totalStudents > 0 ? (session.presentCount * 100.0 / session.totalStudents) : 0}" />
            <div style="font-size: 0.95rem; font-weight: 800; color: var(--primary);"><fmt:formatNumber value="${pct}" maxFractionDigits="1" />%</div>
        </div>
    </div>

    <!-- Records Table -->
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Roll Number</th>
                        <th>Student Name</th>
                        <th>Status</th>
                        <th>Remarks</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="rec" items="${records}" varStatus="loop">
                        <tr>
                            <td>${loop.index + 1}</td>
                            <td><strong>${rec.rollNumber}</strong></td>
                            <td>${rec.studentName}</td>
                            <td>
                                <span class="badge ${rec.status == 'PRESENT' ? 'badge-present' : 'badge-absent'}">
                                    ${rec.status}
                                </span>
                            </td>
                            <td>${not empty rec.remarks ? rec.remarks : '-'}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
