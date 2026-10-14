<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Online Attendance Management System</title>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;700&family=Plus+Jakarta+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-body">
    <div class="login-card" style="text-align: center;">
        <div class="stat-icon amber" style="margin: 0 auto 1.5rem auto; width: 64px; height: 64px; font-size: 2rem;">
            ⚠️
        </div>
        <h1 style="font-size: 1.6rem; margin-bottom: 0.5rem;">System Notice</h1>
        <p style="color: var(--text-muted); margin-bottom: 1.5rem;">
            <c:choose>
                <c:when test="${not empty error}">
                    ${error}
                </c:when>
                <c:when test="${not empty message}">
                    ${message}
                </c:when>
                <c:otherwise>
                    An unexpected issue occurred or the requested page could not be located.
                </c:otherwise>
            </c:choose>
        </p>

        <div style="display: flex; gap: 0.75rem; justify-content: center;">
            <a href="javascript:history.back()" class="btn btn-secondary">Go Back</a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Return Home</a>
        </div>
    </div>
</body>
</html>
