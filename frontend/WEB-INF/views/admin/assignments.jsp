<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Faculty Class Assignments" />
<c:set var="activeMenu" value="assignments" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>Teacher - Subject Assignments</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">Map faculty teachers to their assigned curriculum subjects and class sections</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/admin/assignments/add" class="btn btn-primary btn-sm">+ Assign Teacher</a>
        </div>
    </div>

    <div style="padding: 1rem 1.75rem; border-bottom: 1px solid var(--border-color); background: #fbfcfe;">
        <input type="text" data-table-search="assignmentsTable" class="form-control" placeholder="Search by teacher name or subject..." style="max-width: 380px;">
    </div>

    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table" id="assignmentsTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Teacher Name</th>
                        <th>Subject</th>
                        <th>Subject Code</th>
                        <th>Academic Year</th>
                        <th>Section</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty assignments}">
                            <c:forEach var="a" items="${assignments}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><strong>${a.teacherName}</strong></td>
                                    <td>${a.subjectName}</td>
                                    <td><span class="badge badge-role">${a.subjectCode}</span></td>
                                    <td>${a.academicYearLabel}</td>
                                    <td>Section ${a.sectionName}</td>
                                    <td>
                                        <span class="badge ${a.active ? 'badge-active' : 'badge-inactive'}">
                                            ${a.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/assignments/delete/${a.id}"
                                           onclick="return confirmAction('Remove this teaching assignment?')"
                                           class="btn btn-danger btn-sm" style="padding: 0.3rem 0.6rem; font-size: 0.75rem;">
                                            Remove
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    No teacher-class assignments recorded yet. Click "+ Assign Teacher" above to assign faculty.
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
