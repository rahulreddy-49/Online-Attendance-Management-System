<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${teacher != null ? 'Edit Teacher' : 'Register Faculty Teacher'}" />
<c:set var="activeMenu" value="teachers" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <div class="card-header">
        <h2>${teacher != null ? 'Edit Faculty Details' : 'Register New Faculty Member'}</h2>
        <a href="${pageContext.request.contextPath}/admin/teachers" class="btn btn-secondary btn-sm">← Back to List</a>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/teachers/${teacher != null ? 'edit/'.concat(teacher.id) : 'add'}" method="POST">
            <div class="form-grid">
                <div class="form-group">
                    <label for="fullName">Full Name *</label>
                    <input type="text" id="fullName" name="fullName" class="form-control"
                           value="${teacher != null ? teacher.fullName : ''}" placeholder="e.g. Dr. Ramesh Kumar" required>
                </div>

                <div class="form-group">
                    <label for="employeeId">Employee ID *</label>
                    <input type="text" id="employeeId" name="employeeId" class="form-control"
                           value="${teacher != null ? teacher.employeeId : ''}" placeholder="e.g. TCH-001" required>
                </div>
            </div>

            <c:if test="${teacher == null}">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="username">Login Username *</label>
                        <input type="text" id="username" name="username" class="form-control"
                               placeholder="e.g. ramesh.kumar" required>
                    </div>

                    <div class="form-group">
                        <label for="password">Login Password *</label>
                        <input type="password" id="password" name="password" class="form-control"
                               placeholder="Minimum 6 characters" required>
                    </div>
                </div>
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="departmentId">Department *</label>
                    <select id="departmentId" name="departmentId" class="form-control" required>
                        <option value="">Select Department</option>
                        <c:forEach var="d" items="${departments}">
                            <option value="${d.id}" ${teacher != null && teacher.departmentId == d.id ? 'selected' : ''}>
                                ${d.name} (${d.code})
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="qualification">Qualification / Title</label>
                    <input type="text" id="qualification" name="qualification" class="form-control"
                           value="${teacher != null ? teacher.qualification : ''}" placeholder="e.g. M.Tech, Ph.D">
                </div>
            </div>

            <div class="form-grid">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control"
                           value="${teacher != null ? teacher.email : ''}" placeholder="e.g. teacher@institution.edu">
                </div>

                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone" class="form-control"
                           value="${teacher != null ? teacher.phone : ''}" placeholder="e.g. +91 9876543210">
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    ${teacher != null ? 'Save Changes' : 'Register Teacher'}
                </button>
                <a href="${pageContext.request.contextPath}/admin/teachers" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
