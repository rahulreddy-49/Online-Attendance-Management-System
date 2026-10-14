<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Attendance Management System'}</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- Main Style -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
    <div class="main-wrapper">
        <header class="topbar">
            <div class="topbar-left">
                <button type="button" id="sidebarToggle" class="btn btn-secondary btn-sm" style="display:none;" aria-label="Toggle Menu">☰</button>
                <h1 class="page-title">${pageTitle != null ? pageTitle : 'Dashboard'}</h1>
            </div>
            <div class="topbar-right">
                <c:if test="${sessionScope.user != null}">
                    <c:choose>
                        <c:when test="${sessionScope.user.role == 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin/students/add" class="quick-action-btn">+ New Student</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/teacher/attendance/mark" class="quick-action-btn">Take Attendance</a>
                        </c:otherwise>
                    </c:choose>
                    <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Log Out</a>
                </c:if>
            </div>
        </header>
        <main class="content-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger">
                    <span>⚠️</span>
                    <span>${error}</span>
                </div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <span>✅</span>
                    <span>${success}</span>
                </div>
            </c:if>
