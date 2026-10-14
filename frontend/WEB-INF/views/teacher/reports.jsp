<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Class Attendance Reports" />
<c:set var="activeMenu" value="reports" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>My Classes Attendance Analytics</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">Calculate student attendance percentages and identify student shortage (< 75%)</p>
        </div>
        <c:if test="${not empty reportData}">
            <div style="display: flex; gap: 0.5rem;">
                <button onclick="window.print()" class="btn btn-secondary btn-sm">🖨️ Print Report</button>
                <a href="${pageContext.request.contextPath}/teacher/reports?departmentId=${selectedDept}&academicYearId=${selectedYear}&sectionId=${selectedSec}&subjectId=${selectedSub != null ? selectedSub : ''}&fromDate=${selectedFrom != null ? selectedFrom : ''}&toDate=${selectedTo != null ? selectedTo : ''}&export=csv" class="btn btn-primary btn-sm">
                    📥 Export CSV
                </a>
            </div>
        </c:if>
    </div>

    <!-- Filter Form -->
    <div style="padding: 1.5rem 1.75rem; border-bottom: 1px solid var(--border-color); background: #fbfcfe;">
        <form action="${pageContext.request.contextPath}/teacher/reports" method="GET" style="display: flex; flex-wrap: wrap; gap: 1rem; align-items: flex-end;">
            <div style="min-width: 180px; flex: 1;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Department *</label>
                <select name="departmentId" class="form-control" required>
                    <option value="">Select Department</option>
                    <c:forEach var="d" items="${departments}">
                        <option value="${d.id}" ${selectedDept == d.id ? 'selected' : ''}>${d.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div style="min-width: 140px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Academic Year *</label>
                <select name="academicYearId" class="form-control" required>
                    <option value="">Select Year</option>
                    <c:forEach var="y" items="${academicYears}">
                        <option value="${y.id}" ${selectedYear == y.id ? 'selected' : ''}>${y.yearLabel}</option>
                    </c:forEach>
                </select>
            </div>

            <div style="min-width: 120px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Section *</label>
                <select name="sectionId" class="form-control" required>
                    <option value="">Select Section</option>
                    <c:forEach var="s" items="${sections}">
                        <option value="${s.id}" ${selectedSec == s.id ? 'selected' : ''}>Section ${s.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div style="min-width: 180px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Subject (Optional)</label>
                <select name="subjectId" class="form-control">
                    <option value="">All Assigned Subjects</option>
                    <c:forEach var="sub" items="${subjects}">
                        <option value="${sub.id}" ${selectedSub == sub.id ? 'selected' : ''}>${sub.name} (${sub.code})</option>
                    </c:forEach>
                </select>
            </div>

            <div style="min-width: 140px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">From Date</label>
                <input type="date" name="fromDate" value="${selectedFrom}" class="form-control">
            </div>

            <div style="min-width: 140px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">To Date</label>
                <input type="date" name="toDate" value="${selectedTo}" class="form-control">
            </div>

            <button type="submit" class="btn btn-primary" style="height: 42px;">View Report</button>
        </form>
    </div>

    <!-- Results Table -->
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty reportData}">
                <div style="padding: 1rem 1.75rem; background: #f8fafc; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center;">
                    <div style="font-size: 0.88rem; color: var(--text-muted);">
                        Showing attendance for <strong>${reportData.size()}</strong> students. Threshold requirement: <strong>75%</strong>
                    </div>
                    <div>
                        <input type="text" data-table-search="teacherReportTable" class="form-control" placeholder="Search student in report..." style="width: 250px; padding: 0.4rem 0.8rem; font-size: 0.85rem;">
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="modern-table" id="teacherReportTable">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Roll Number</th>
                                <th>Student Name</th>
                                <th>Total Sessions</th>
                                <th>Present Count</th>
                                <th>Absent Count</th>
                                <th>Attendance %</th>
                                <th>Eligibility Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="row" items="${reportData}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><strong>${row.rollNumber}</strong></td>
                                    <td>${row.fullName}</td>
                                    <td>${row.totalSessions}</td>
                                    <td><span class="badge badge-present">${row.presentCount}</span></td>
                                    <td><span class="badge badge-absent">${row.absentCount}</span></td>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 0.75rem;">
                                            <div style="flex: 1; max-width: 100px; height: 8px; background: #e2e8f0; border-radius: var(--radius-pill); overflow: hidden;">
                                                <div style="width: ${row.percentage}%; height: 100%; background: ${row.percentage >= 75 ? 'var(--success)' : (row.percentage >= 65 ? 'var(--warning)' : 'var(--danger)')};"></div>
                                            </div>
                                            <span style="font-weight: 700;">${row.percentage}%</span>
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${row.percentage >= 75}">
                                                <span class="percentage-pill percentage-good">Eligible (≥75%)</span>
                                            </c:when>
                                            <c:when test="${row.percentage >= 65}">
                                                <span class="percentage-pill percentage-warning">Shortage Alert</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="percentage-pill percentage-danger">Defaulter (<65%)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; padding: 3.5rem 1.5rem;">
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">📑</div>
                    <h3 style="font-size: 1.15rem; color: var(--text-main); margin-bottom: 0.35rem;">Class Attendance Breakdown</h3>
                    <p style="color: var(--text-muted); font-size: 0.88rem; max-width: 450px; margin: 0 auto;">
                        Select your Department, Academic Year, and Class Section above and click "View Report".
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
