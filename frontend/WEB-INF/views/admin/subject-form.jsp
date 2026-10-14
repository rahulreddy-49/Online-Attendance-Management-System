<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${subject != null ? 'Edit Subject' : 'Add New Subject'}" />
<c:set var="activeMenu" value="subjects" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card" style="max-width: 750px; margin: 0 auto;">
    <div class="card-header">
        <h2>${subject != null ? 'Edit Subject Details' : 'Add New Curriculum Subject'}</h2>
        <a href="${pageContext.request.contextPath}/admin/subjects" class="btn btn-secondary btn-sm">← Back to List</a>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/subjects/${subject != null ? 'edit/'.concat(subject.id) : 'add'}" method="POST">
            <div class="form-grid">
                <div class="form-group">
                    <label for="code">Subject Code *</label>
                    <input type="text" id="code" name="code" class="form-control"
                           value="${subject != null ? subject.code : ''}" placeholder="e.g. CS301" required>
                </div>

                <div class="form-group">
                    <label for="name">Subject Name *</label>
                    <input type="text" id="name" name="name" class="form-control"
                           value="${subject != null ? subject.name : ''}" placeholder="e.g. Data Structures and Algorithms" required>
                </div>
            </div>

            <div class="form-grid">
                <div class="form-group">
                    <label for="departmentId">Department *</label>
                    <select id="departmentId" name="departmentId" class="form-control" required>
                        <option value="">Select Department</option>
                        <c:forEach var="d" items="${departments}">
                            <option value="${d.id}" ${subject != null && subject.departmentId == d.id ? 'selected' : ''}>
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
                            <option value="${y.id}" ${subject != null && subject.academicYearId == y.id ? 'selected' : ''}>
                                ${y.yearLabel}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="credits">Credits</label>
                    <input type="number" id="credits" name="credits" class="form-control" min="1" max="10"
                           value="${subject != null ? subject.credits : 3}" required>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    ${subject != null ? 'Save Changes' : 'Create Subject'}
                </button>
                <a href="${pageContext.request.contextPath}/admin/subjects" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
