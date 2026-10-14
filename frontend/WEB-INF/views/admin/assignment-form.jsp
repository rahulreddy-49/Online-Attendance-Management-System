<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Assign Teacher to Subject" />
<c:set var="activeMenu" value="assignments" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card" style="max-width: 750px; margin: 0 auto;">
    <div class="card-header">
        <h2>Assign Faculty Teacher to Class Section</h2>
        <a href="${pageContext.request.contextPath}/admin/assignments" class="btn btn-secondary btn-sm">← Back to List</a>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/assignments/add" method="POST">
            <div class="form-group">
                <label for="teacherId">Select Teacher *</label>
                <select id="teacherId" name="teacherId" class="form-control" required>
                    <option value="">-- Choose Faculty Member --</option>
                    <c:forEach var="t" items="${teachers}">
                        <option value="${t.id}">${t.fullName} (${t.employeeId} - ${t.departmentName})</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="subjectId">Select Subject *</label>
                <select id="subjectId" name="subjectId" class="form-control" required>
                    <option value="">-- Choose Curriculum Subject --</option>
                    <c:forEach var="sub" items="${subjects}">
                        <option value="${sub.id}">${sub.name} (${sub.code})</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-grid">
                <div class="form-group">
                    <label for="academicYearId">Academic Year *</label>
                    <select id="academicYearId" name="academicYearId" class="form-control" required>
                        <option value="">Select Year</option>
                        <c:forEach var="y" items="${academicYears}">
                            <option value="${y.id}">${y.yearLabel}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="sectionId">Class Section *</label>
                    <select id="sectionId" name="sectionId" class="form-control" required>
                        <option value="">Select Section</option>
                        <c:forEach var="s" items="${sections}">
                            <option value="${s.id}">Section ${s.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Assignment</button>
                <a href="${pageContext.request.contextPath}/admin/assignments" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
