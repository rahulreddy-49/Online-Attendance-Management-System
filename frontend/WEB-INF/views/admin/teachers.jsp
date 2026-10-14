<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Faculty & Teacher Management" />
<c:set var="activeMenu" value="teachers" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>Faculty Members</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">Manage teacher credentials, employee profiles, and departments</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/admin/teachers/add" class="btn btn-primary btn-sm">+ Add New Teacher</a>
        </div>
    </div>

    <div style="padding: 1rem 1.75rem; border-bottom: 1px solid var(--border-color); background: #fbfcfe;">
        <input type="text" data-table-search="teachersTable" class="form-control" placeholder="Search teacher by name or employee ID..." style="max-width: 380px;">
    </div>

    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table" id="teachersTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Emp ID</th>
                        <th>Teacher Name</th>
                        <th>Department</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Qualification</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty teachers}">
                            <c:forEach var="t" items="${teachers}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><strong>${t.employeeId}</strong></td>
                                    <td>
                                        <div style="font-weight: 600;">${t.fullName}</div>
                                    </td>
                                    <td>${t.departmentName}</td>
                                    <td>${not empty t.email ? t.email : '-'}</td>
                                    <td>${not empty t.phone ? t.phone : '-'}</td>
                                    <td>${not empty t.qualification ? t.qualification : '-'}</td>
                                    <td>
                                        <span class="badge ${t.active ? 'badge-active' : 'badge-inactive'}">
                                            ${t.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 0.4rem;">
                                            <a href="${pageContext.request.contextPath}/admin/teachers/edit/${t.id}" class="btn btn-secondary btn-sm">Edit</a>
                                            <c:if test="${t.active}">
                                                <a href="${pageContext.request.contextPath}/admin/teachers/delete/${t.id}"
                                                   onclick="return confirmAction('Deactivate teacher ${t.fullName}?')"
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
                                    No teachers registered yet. Click "Add New Teacher" above to register faculty.
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
