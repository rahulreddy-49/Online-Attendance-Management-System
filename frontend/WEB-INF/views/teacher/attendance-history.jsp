<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Attendance History" />
<c:set var="activeMenu" value="history" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>My Conducted Attendance Sessions</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">Complete log of attendance records submitted by you</p>
        </div>
        <a href="${pageContext.request.contextPath}/teacher/attendance/mark" class="btn btn-primary btn-sm">+ Mark New Session</a>
    </div>

    <div style="padding: 1rem 1.75rem; border-bottom: 1px solid var(--border-color); background: #fbfcfe;">
        <input type="text" data-table-search="sessionsTable" class="form-control" placeholder="Search by subject, class, or date..." style="max-width: 380px;">
    </div>

    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table" id="sessionsTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Session Date</th>
                        <th>Subject</th>
                        <th>Class Section</th>
                        <th>Total Students</th>
                        <th>Present</th>
                        <th>Absent</th>
                        <th>Turnout</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty sessions}">
                            <c:forEach var="s" items="${sessions}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><strong>${s.sessionDate}</strong></td>
                                    <td>
                                        <div><strong>${s.subjectName}</strong></div>
                                        <small style="color: var(--text-muted);">${s.subjectCode}</small>
                                    </td>
                                    <td>${s.yearLabel} - Section ${s.sectionName}</td>
                                    <td>${s.totalStudents}</td>
                                    <td><span class="badge badge-present">${s.presentCount}</span></td>
                                    <td><span class="badge badge-absent">${s.absentCount}</span></td>
                                    <td>
                                        <c:set var="pct" value="${s.totalStudents > 0 ? (s.presentCount * 100.0 / s.totalStudents) : 0}" />
                                        <span class="percentage-pill ${pct >= 75 ? 'percentage-good' : (pct >= 60 ? 'percentage-warning' : 'percentage-danger')}">
                                            <fmt:formatNumber value="${pct}" maxFractionDigits="1" />%
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/teacher/attendance/view/${s.id}" class="btn btn-secondary btn-sm">
                                            View Details
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="9" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    No attendance sessions conducted yet.
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
