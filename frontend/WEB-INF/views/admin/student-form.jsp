<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${student != null ? 'Edit Student' : 'Register New Student'}" />
<c:set var="activeMenu" value="students" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <div class="card-header">
        <h2>${student != null ? 'Edit Student Information' : 'Register New Student'}</h2>
        <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-secondary btn-sm">← Back to List</a>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/students/${student != null ? 'update' : 'add'}" method="POST">
            <c:if test="${student != null}">
                <input type="hidden" name="id" value="${student.id}">
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="rollNumber">Roll / Registration Number *</label>
                    <input type="text" id="rollNumber" name="rollNumber" class="form-control"
                           value="${student != null ? student.rollNumber : ''}" placeholder="e.g. CS2026-001" required>
                </div>

                <div class="form-group">
                    <label for="fullName">Full Name *</label>
                    <input type="text" id="fullName" name="fullName" class="form-control"
                           value="${student != null ? student.fullName : ''}" placeholder="e.g. Rahul Sharma" required>
                </div>
            </div>

            <div class="form-grid">
                <div class="form-group">
                    <label for="departmentId">Department *</label>
                    <select id="departmentId" name="departmentId" class="form-control" required>
                        <option value="">Select Department</option>
                        <c:forEach var="d" items="${departments}">
                            <option value="${d.id}" ${student != null && student.departmentId == d.id ? 'selected' : ''}>
                                ${d.name} (${d.code})
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="academicYearId">Academic Year *</label>
                    <select id="academicYearId" name="academicYearId" class="form-control" required>
                        <option value="">Select Year</option>
                        <c:forEach var="y" items="${academicYears}">
                            <option value="${y.id}" ${student != null && student.academicYearId == y.id ? 'selected' : ''}>
                                ${y.yearLabel}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="sectionId">Section *</label>
                    <select id="sectionId" name="sectionId" class="form-control" required>
                        <option value="">Select Section</option>
                        <c:forEach var="s" items="${sections}">
                            <option value="${s.id}" ${student != null && student.sectionId == s.id ? 'selected' : ''}>
                                Section ${s.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-grid">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control"
                           value="${student != null ? student.email : ''}" placeholder="e.g. student@institution.edu">
                </div>

                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone" class="form-control"
                           value="${student != null ? student.phone : ''}" placeholder="e.g. +91 9876543210">
                </div>

                <div class="form-group">
                    <label for="gender">Gender</label>
                    <select id="gender" name="gender" class="form-control">
                        <option value="">Select Gender</option>
                        <option value="Male" ${student != null && student.gender == 'Male' ? 'selected' : ''}>Male</option>
                        <option value="Female" ${student != null && student.gender == 'Female' ? 'selected' : ''}>Female</option>
                        <option value="Other" ${student != null && student.gender == 'Other' ? 'selected' : ''}>Other</option>
                    </select>
                </div>
            </div>

            <c:if test="${student != null}">
                <div class="form-group" style="margin-top: 1rem;">
                    <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                        <input type="checkbox" name="isActive" value="true" ${student.active ? 'checked' : ''}>
                        <span style="font-size: 0.9rem; font-weight: 600;">Account is Active</span>
                    </label>
                </div>
            </c:if>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    ${student != null ? 'Save Changes' : 'Register Student'}
                </button>
                <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
