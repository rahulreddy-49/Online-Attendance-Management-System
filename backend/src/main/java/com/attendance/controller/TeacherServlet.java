package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.attendance.util.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/admin/teachers/*")
public class TeacherServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            req.setAttribute("departments", departmentDAO.findActive());

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                List<Teacher> teachers = teacherDAO.findAll();
                req.setAttribute("teachers", teachers);
                HttpSession session = req.getSession();
                if (session.getAttribute("success") != null) {
                    req.setAttribute("success", session.getAttribute("success"));
                    session.removeAttribute("success");
                }
                req.getRequestDispatcher("/WEB-INF/views/admin/teachers.jsp").forward(req, resp);
            } else if (pathInfo.equals("/add")) {
                req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/edit/")) {
                int id = Integer.parseInt(pathInfo.substring(6));
                Teacher teacher = teacherDAO.findById(id);
                if (teacher == null) {
                    req.setAttribute("error", "Teacher not found.");
                    resp.sendRedirect(req.getContextPath() + "/admin/teachers/list");
                    return;
                }
                req.setAttribute("teacher", teacher);
                req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/deactivate/")) {
                int id = Integer.parseInt(pathInfo.substring(12));
                teacherDAO.deactivate(id);
                req.getSession().setAttribute("success", "Teacher deactivated successfully.");
                resp.sendRedirect(req.getContextPath() + "/admin/teachers/list");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/teachers/list");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/teachers.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            req.setAttribute("departments", departmentDAO.findActive());
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String employeeId = req.getParameter("employeeId");
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            String phone = req.getParameter("phone");
            String qualification = req.getParameter("qualification");

            if (fullName == null || fullName.trim().isEmpty() ||
                employeeId == null || employeeId.trim().isEmpty()) {
                req.setAttribute("error", "Full name and employee ID are required.");
                req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
                return;
            }

            if (pathInfo != null && pathInfo.startsWith("/edit/")) {
                // Update existing teacher
                int id = Integer.parseInt(pathInfo.substring(6));
                if (teacherDAO.employeeIdExistsExcluding(employeeId.trim(), id)) {
                    req.setAttribute("error", "Employee ID already exists.");
                    req.setAttribute("teacher", teacherDAO.findById(id));
                    req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
                    return;
                }
                Teacher teacher = teacherDAO.findById(id);
                teacher.setEmployeeId(employeeId.trim());
                teacher.setDepartmentId(departmentId);
                teacher.setPhone(phone != null ? phone.trim() : "");
                teacher.setQualification(qualification != null ? qualification.trim() : "");
                teacherDAO.update(teacher);

                // Update user info
                User user = userDAO.findById(teacher.getUserId());
                user.setFullName(fullName.trim());
                user.setEmail(email != null ? email.trim() : "");
                userDAO.update(user);

                req.getSession().setAttribute("success", "Teacher updated successfully.");
            } else {
                // Add new teacher
                String username = req.getParameter("username");
                String password = req.getParameter("password");

                if (username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {
                    req.setAttribute("error", "Username and password are required for new teacher.");
                    req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
                    return;
                }

                if (userDAO.usernameExists(username.trim())) {
                    req.setAttribute("error", "Username already exists.");
                    req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
                    return;
                }

                if (teacherDAO.employeeIdExists(employeeId.trim())) {
                    req.setAttribute("error", "Employee ID already exists.");
                    req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
                    return;
                }

                // Use transaction for user + teacher creation
                Connection conn = null;
                try {
                    conn = DBUtil.getConnection();
                    conn.setAutoCommit(false);

                    String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                    User user = new User();
                    user.setUsername(username.trim());
                    user.setPasswordHash(passwordHash);
                    user.setRole("TEACHER");
                    user.setFullName(fullName.trim());
                    user.setEmail(email != null ? email.trim() : "");
                    user.setActive(true);
                    int userId = userDAO.insert(conn, user);

                    Teacher teacher = new Teacher();
                    teacher.setUserId(userId);
                    teacher.setEmployeeId(employeeId.trim());
                    teacher.setDepartmentId(departmentId);
                    teacher.setPhone(phone != null ? phone.trim() : "");
                    teacher.setQualification(qualification != null ? qualification.trim() : "");
                    teacher.setActive(true);
                    teacherDAO.insert(conn, teacher);

                    conn.commit();
                } catch (Exception e) {
                    if (conn != null) try { conn.rollback(); } catch (Exception ignored) {}
                    throw e;
                } finally {
                    if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
                }

                req.getSession().setAttribute("success", "Teacher added successfully.");
            }

            resp.sendRedirect(req.getContextPath() + "/admin/teachers/list");

        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/teacher-form.jsp").forward(req, resp);
        }
    }
}
