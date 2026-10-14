<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Curriculum & Subjects" />
<c:set var="activeMenu" value="subjects" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card">
    <div class="card-header">
        <div>
            <h2>Subjects & Courses</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.2rem;">Define institutional curriculum, subject codes, and course credits</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/admin/subjects/add" class="btn btn-primary btn-sm">+ Add Subject</a>
        </div>
    </div>

    <div style="padding: 1rem 1.75rem; border-bottom: 1px solid var(--border-color); background: #fbfcfe;">
        <input type="text" data-table-search="subjectsTable" class="form-control" placeholder="Search by subject name or code..." style="max-width: 380px;">
    </div>

    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="modern-table" id="subjectsTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Subject Code</th>
                        <th>Subject Name</th>
                        <th>Department</th>
                        <th>Academic Year</th>
                        <th>Credits</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty subjects}">
                            <c:forEach var="sub" items="${subjects}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td><span class="badge badge-role">${sub.code}</span></td>
                                    <td><strong>${sub.name}</strong></td>
                                    <td>${sub.departmentName}</td>
                                    <td>${sub.academicYearLabel}</td>
                                    <td>${sub.credits} Credits</td>
                                    <td>
                                        <span class="badge ${sub.active ? 'badge-active' : 'badge-inactive'}">
                                            ${sub.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 0.4rem;">
                                            <a href="${pageContext.request.contextPath}/admin/subjects/edit/${sub.id}" class="btn btn-secondary btn-sm">Edit</a>
                                            <c:if test="${sub.active}">
                                                <a href="${pageContext.request.contextPath}/admin/subjects/deactivate/${sub.id}"
                                                   onclick="return confirmAction('Deactivate subject ${sub.code}?')"
                                                   class="btn btn-danger btn-sm" style="padding: 0.3rem 0.6rem; font-size: 0.75rem;">Deactivate</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    No subjects found. Click "+ Add Subject" to create one.
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
