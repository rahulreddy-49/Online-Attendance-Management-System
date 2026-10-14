<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (session != null && session.getAttribute("user") != null) {
        com.attendance.model.User user = (com.attendance.model.User) session.getAttribute("user");
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
