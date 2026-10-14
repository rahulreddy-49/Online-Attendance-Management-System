<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Record Class Attendance" />
<c:set var="activeMenu" value="attendance" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>Record Attendance Session</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">Select class section and session date to load registered students</p>
        </div>
        <a href="${pageContext.request.contextPath}/teacher/attendance/history" class="btn btn-secondary btn-sm">Attendance History</a>
    </div>

    <!-- Selection Bar -->
    <div style="padding: 1.5rem 1.75rem; border-bottom: 1px solid var(--border-color); background: #fbfcfe;">
        <div style="display: flex; flex-wrap: wrap; gap: 1rem; align-items: flex-end;">
            <div style="flex: 2; min-width: 250px;">
                <label for="assignmentSelect" style="font-size: 0.8rem; font-weight: 600; color: var(--text-main); margin-bottom: 0.4rem; display: block;">
                    Select Assigned Class *
                </label>
                <select id="assignmentSelect" class="form-control" required>
                    <option value="">-- Choose Class / Subject --</option>
                    <c:forEach var="a" items="${assignments}">
                        <option value="${a.id}" ${param.assignmentId == a.id ? 'selected' : ''}>
                            ${a.subjectName} (${a.subjectCode}) - ${a.yearLabel} Sec ${a.sectionName} [${a.departmentName}]
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div style="flex: 1; min-width: 170px;">
                <label for="sessionDate" style="font-size: 0.8rem; font-weight: 600; color: var(--text-main); margin-bottom: 0.4rem; display: block;">
                    Session Date *
                </label>
                <input type="date" id="sessionDate" class="form-control" value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>" required>
            </div>

            <div>
                <button type="button" id="loadStudentsBtn" class="btn btn-primary" style="height: 42px;">
                    Fetch Students
                </button>
            </div>
        </div>
    </div>

    <!-- Empty State -->
    <div id="emptyState" style="text-align: center; padding: 4rem 1.5rem;">
        <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">✍️</div>
        <h3 style="font-size: 1.2rem; color: var(--text-main); margin-bottom: 0.35rem;">Attendance Sheet Ready</h3>
        <p style="color: var(--text-muted); font-size: 0.9rem; max-width: 450px; margin: 0 auto;">
            Select a class from the dropdown above and click "Fetch Students" to open the interactive attendance roll sheet.
        </p>
    </div>

    <!-- Attendance Sheet Container (initially hidden until students are loaded) -->
    <div id="attendanceSheetContainer" style="display: none;">
        <!-- Existing Session Warning Banner -->
        <div id="existingSessionBanner" class="alert alert-warning" style="display: none; margin: 1.5rem 1.75rem 0 1.75rem;">
            <span>ℹ️</span>
            <span>Attendance for this date was already recorded previously. Submitting now will safely update the records.</span>
        </div>

        <!-- Live Tally & Quick Actions -->
        <div style="padding: 1.25rem 1.75rem; border-bottom: 1px solid var(--border-color); display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 1rem; background: white;">
            <!-- Live Tally -->
            <div style="display: flex; gap: 1.5rem; align-items: center;">
                <div>
                    <span style="font-size: 0.75rem; color: var(--text-muted); font-weight: 600;">TOTAL</span>
                    <div id="tallyTotal" style="font-size: 1.3rem; font-weight: 800; font-family: var(--font-heading);">0</div>
                </div>
                <div>
                    <span style="font-size: 0.75rem; color: var(--success); font-weight: 600;">PRESENT</span>
                    <div id="tallyPresent" style="font-size: 1.3rem; font-weight: 800; color: var(--success); font-family: var(--font-heading);">0</div>
                </div>
                <div>
                    <span style="font-size: 0.75rem; color: var(--danger); font-weight: 600;">ABSENT</span>
                    <div id="tallyAbsent" style="font-size: 1.3rem; font-weight: 800; color: var(--danger); font-family: var(--font-heading);">0</div>
                </div>
                <div>
                    <span style="font-size: 0.75rem; color: var(--primary); font-weight: 600;">TURNOUT</span>
                    <div id="tallyRate" style="font-size: 1.3rem; font-weight: 800; color: var(--primary); font-family: var(--font-heading);">0%</div>
                </div>
            </div>

            <!-- Batch Buttons -->
            <div style="display: flex; gap: 0.5rem;">
                <button type="button" onclick="markAllPresent()" class="btn btn-secondary btn-sm" style="color: var(--success); font-weight: 700;">
                    ✓ Mark All Present
                </button>
                <button type="button" onclick="markAllAbsent()" class="btn btn-secondary btn-sm" style="color: var(--danger); font-weight: 700;">
                    ✕ Mark All Absent
                </button>
            </div>
        </div>

        <!-- Attendance Form -->
        <form id="attendanceForm">
            <div class="table-responsive">
                <table class="modern-table">
                    <thead>
                        <tr>
                            <th style="width: 60px;">#</th>
                            <th style="width: 160px;">Roll Number</th>
                            <th>Student Name</th>
                            <th style="width: 200px;">Attendance Status</th>
                            <th>Remarks (Optional)</th>
                        </tr>
                    </thead>
                    <tbody id="studentTableBody">
                        <!-- Populated dynamically via attendance.js -->
                    </tbody>
                </table>
            </div>

            <div style="padding: 1.5rem 1.75rem; background: #f8fafc; border-top: 1px solid var(--border-color); display: flex; justify-content: flex-end;">
                <button type="submit" id="saveAttendanceBtn" class="btn btn-primary" style="padding: 0.8rem 2rem; font-size: 1rem;">
                    Save Attendance
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
