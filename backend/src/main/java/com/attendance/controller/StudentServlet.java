package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/students/*")
public class StudentServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AcademicYearDAO yearDAO = new AcademicYearDAO();
    private final SectionDAO sectionDAO = new SectionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            loadDropdowns(req);

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                listStudents(req, resp);
            } else if (pathInfo.equals("/add")) {
                req.getRequestDispatcher("/WEB-INF/views/admin/student-form.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/edit/")) {
                int id = Integer.parseInt(pathInfo.substring(6));
                Student student = studentDAO.findById(id);
                if (student == null) {
                    req.setAttribute("error", "Student not found.");
                    listStudents(req, resp);
                    return;
                }
                req.setAttribute("student", student);
                req.getRequestDispatcher("/WEB-INF/views/admin/student-form.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/view/")) {
                int id = Integer.parseInt(pathInfo.substring(6));
                Student student = studentDAO.findById(id);
                if (student == null) {
                    req.setAttribute("error", "Student not found.");
                    listStudents(req, resp);
                    return;
                }
                // Get attendance summary
                AttendanceDAO attendanceDAO = new AttendanceDAO();
                var summary = attendanceDAO.getStudentAttendanceSummary(id, null, null, null);
                req.setAttribute("student", student);
                req.setAttribute("attendanceSummary", summary);
                req.getRequestDispatcher("/WEB-INF/views/admin/student-view.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/deactivate/")) {
                int id = Integer.parseInt(pathInfo.substring(12));
                studentDAO.deactivate(id);
                req.getSession().setAttribute("success", "Student deactivated successfully.");
                resp.sendRedirect(req.getContextPath() + "/admin/students/list");
            } else if (pathInfo.startsWith("/activate/")) {
                int id = Integer.parseInt(pathInfo.substring(10));
                studentDAO.activate(id);
                req.getSession().setAttribute("success", "Student activated successfully.");
                resp.sendRedirect(req.getContextPath() + "/admin/students/list");
            } else {
                listStudents(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid student ID.");
            listStudents(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            listStudents(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            loadDropdowns(req);

            String rollNumber = req.getParameter("rollNumber");
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            int yearId = Integer.parseInt(req.getParameter("academicYearId"));
            int sectionId = Integer.parseInt(req.getParameter("sectionId"));

            // Validation
            if (rollNumber == null || rollNumber.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty()) {
                req.setAttribute("error", "Roll number and full name are required.");
                req.getRequestDispatcher("/WEB-INF/views/admin/student-form.jsp").forward(req, resp);
                return;
            }

            if (pathInfo != null && pathInfo.startsWith("/edit/")) {
                // Update
                int id = Integer.parseInt(pathInfo.substring(6));
                if (studentDAO.rollNumberExistsExcluding(rollNumber.trim(), id)) {
                    req.setAttribute("error", "Roll number already exists for another student.");
                    Student s = studentDAO.findById(id);
                    req.setAttribute("student", s);
                    req.getRequestDispatcher("/WEB-INF/views/admin/student-form.jsp").forward(req, resp);
                    return;
                }
                Student student = new Student();
                student.setId(id);
                student.setRollNumber(rollNumber.trim());
                student.setFullName(fullName.trim());
                student.setEmail(email != null ? email.trim() : "");
                student.setPhone(phone != null ? phone.trim() : "");
                student.setDepartmentId(departmentId);
                student.setAcademicYearId(yearId);
                student.setSectionId(sectionId);
                student.setActive(true);
                studentDAO.update(student);
                req.getSession().setAttribute("success", "Student updated successfully.");
            } else {
                // Add
                if (studentDAO.rollNumberExists(rollNumber.trim())) {
                    req.setAttribute("error", "Roll number already exists.");
                    req.getRequestDispatcher("/WEB-INF/views/admin/student-form.jsp").forward(req, resp);
                    return;
                }
                Student student = new Student();
                student.setRollNumber(rollNumber.trim());
                student.setFullName(fullName.trim());
                student.setEmail(email != null ? email.trim() : "");
                student.setPhone(phone != null ? phone.trim() : "");
                student.setDepartmentId(departmentId);
                student.setAcademicYearId(yearId);
                student.setSectionId(sectionId);
                student.setActive(true);
                studentDAO.insert(student);
                req.getSession().setAttribute("success", "Student added successfully.");
            }

            resp.sendRedirect(req.getContextPath() + "/admin/students/list");

        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/student-form.jsp").forward(req, resp);
        }
    }

    private void listStudents(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Integer deptId = parseIntParam(req, "departmentId");
            Integer yearId = parseIntParam(req, "yearId");
            Integer sectionId = parseIntParam(req, "sectionId");
            String search = req.getParameter("search");

            List<Student> students = studentDAO.findByFilters(deptId, yearId, sectionId, search);
            req.setAttribute("students", students);

            // Pass back flash messages
            HttpSession session = req.getSession();
            if (session.getAttribute("success") != null) {
                req.setAttribute("success", session.getAttribute("success"));
                session.removeAttribute("success");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load students: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/students.jsp").forward(req, resp);
    }

    private void loadDropdowns(HttpServletRequest req) throws Exception {
        req.setAttribute("departments", departmentDAO.findActive());
        req.setAttribute("academicYears", yearDAO.findActive());
        req.setAttribute("sections", sectionDAO.findActive());
    }

    private Integer parseIntParam(HttpServletRequest req, String name) {
        String val = req.getParameter(name);
        if (val != null && !val.trim().isEmpty()) {
            try { return Integer.parseInt(val); } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
