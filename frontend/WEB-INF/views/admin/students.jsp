<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Student Management" />
<c:set var="activeMenu" value="students" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>Enrolled Students</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">View, filter, and manage institutional students</p>
        </div>
        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/admin/students/add" class="btn btn-primary btn-sm">+ Register Student</a>
        </div>
    </div>

    <!-- Filter Bar -->
    <div style="padding: 1.25rem 1.75rem; border-bottom: 1px solid var(--border-color); background: #fbfcfe;">
        <form action="${pageContext.request.contextPath}/admin/students/list" method="GET" style="display: flex; flex-wrap: wrap; gap: 1rem; align-items: flex-end;">
            <div style="flex: 1; min-width: 180px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Quick Search</label>
                <input type="text" data-table-search="studentsTable" class="form-control" placeholder="Search by roll no or name...">
            </div>

            <div style="min-width: 160px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Department</label>
                <select name="departmentId" class="form-control">
                    <option value="">All Departments</option>
                    <c:forEach var="d" items="${departments}">
                        <option value="${d.id}" ${param.departmentId == d.id ? 'selected' : ''}>${d.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div style="min-width: 140px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Year</label>
                <select name="academicYearId" class="form-control">
                    <option value="">All Years</option>
                    <c:forEach var="y" items="${academicYears}">
                        <option value="${y.id}" ${param.academicYearId == y.id ? 'selected' : ''}>${y.yearLabel}</option>
                    </c:forEach>
                </select>
            </div>

            <div style="min-width: 120px;">
                <label style="font-size: 0.75rem; font-weight: 600; color: var(--text-muted);">Section</label>
                <select name="sectionId" class="form-control">
                    <option value="">All Sections</option>
                    <c:forEach var="s" items="${sections}">
                        <option value="${s.id}" ${param.sectionId == s.id ? 'selected' : ''}>Section ${s.name}</option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="btn btn-secondary btn-sm" style="height: 38px;">Apply Filter</button>
            <a href="${pageContext.request.contextPath}/admin/students/list" class="btn btn-secondary btn-sm" style="height: 38px;">Reset</a>
        </form>
    </div>

    <!-- Student Table -->
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table" id="studentsTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Roll Number</th>
                        <th>Full Name</th>
                        <th>Department</th>
                        <th>Year</th>
                        <th>Section</th>
                        <th>Email / Contact</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty students}">
                            <c:forEach var="s" items="${students}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><strong>${s.rollNumber}</strong></td>
                                    <td>
                                        <div style="font-weight: 600;">${s.fullName}</div>
                                        <c:if test="${not empty s.gender}">
                                            <small style="color: var(--text-muted);">${s.gender}</small>
                                        </c:if>
                                    </td>
                                    <td>${s.departmentName}</td>
                                    <td>${s.academicYearLabel}</td>
                                    <td>Section ${s.sectionName}</td>
                                    <td>
                                        <div>${not empty s.email ? s.email : '-'}</div>
                                        <small style="color: var(--text-muted);">${not empty s.phone ? s.phone : ''}</small>
                                    </td>
                                    <td>
                                        <span class="badge ${s.active ? 'badge-active' : 'badge-inactive'}">
                                            ${s.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 0.4rem;">
                                            <a href="${pageContext.request.contextPath}/admin/students/edit?id=${s.id}" class="btn btn-secondary btn-sm">Edit</a>
                                            <c:if test="${s.active}">
                                                <a href="${pageContext.request.contextPath}/admin/students/delete?id=${s.id}"
                                                   onclick="return confirmAction('Deactivate student ${s.rollNumber}?')"
                                                   class="btn btn-danger btn-sm" style="padding: 0.3rem 0.6rem; font-size: 0.75rem;">Deactivate</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="9" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    No students found matching your criteria.
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
