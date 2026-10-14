package com.attendance.filter;

import com.attendance.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Authentication filter that protects all pages except login and static assets.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow access to login, logout, root landing, static assets, and error pages
        if (path.equals("/login") || path.equals("/login.jsp") ||
            path.equals("/logout") || path.equals("/") || path.equals("/index.jsp") ||
            path.startsWith("/assets/") || path.equals("/favicon.ico") ||
            path.equals("/error.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Check session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Admin-only paths
        if (path.startsWith("/admin/")) {
            if (!user.isAdmin()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }

        // Teacher-only paths
        if (path.startsWith("/teacher/")) {
            if (!user.isTeacher()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
