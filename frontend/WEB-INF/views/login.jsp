<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - Online Attendance Management System</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-body">
    <div class="login-card">
        <div class="login-header">
            <div class="logo-badge">✓</div>
            <h1>Attendify Pro</h1>
            <p>Institutional Online Attendance System</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <span>⚠️</span>
                <span>${error}</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="POST">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" class="form-control"
                       value="${username != null ? username : ''}" placeholder="Enter your username" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your secure password" required>
            </div>

            <div style="margin-top: 1.5rem;">
                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1rem;">
                    Sign In to Portal
                </button>
            </div>
        </form>

        <div class="demo-credentials">
            <h4>Demo Access Accounts</h4>
            <div style="margin-bottom: 0.4rem;">
                <strong>Admin:</strong> <code>admin</code> / <code>admin123</code>
            </div>
            <div>
                <strong>Teacher:</strong> <code>teacher1</code> / <code>teacher123</code>
            </div>
        </div>
    </div>
</body>
</html>
