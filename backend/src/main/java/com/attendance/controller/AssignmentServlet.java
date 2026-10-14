package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/assignments/*")
public class AssignmentServlet extends HttpServlet {

    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final AcademicYearDAO yearDAO = new AcademicYearDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            loadDropdowns(req);

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                List<TeacherAssignment> assignments = assignmentDAO.findAll();
                req.setAttribute("assignments", assignments);
                HttpSession session = req.getSession();
                if (session.getAttribute("success") != null) {
                    req.setAttribute("success", session.getAttribute("success"));
                    session.removeAttribute("success");
                }
                req.getRequestDispatcher("/WEB-INF/views/admin/assignments.jsp").forward(req, resp);
            } else if (pathInfo.equals("/add")) {
                req.getRequestDispatcher("/WEB-INF/views/admin/assignment-form.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/delete/")) {
                int id = Integer.parseInt(pathInfo.substring(8));
                assignmentDAO.delete(id);
                req.getSession().setAttribute("success", "Assignment removed successfully.");
                resp.sendRedirect(req.getContextPath() + "/admin/assignments/list");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/assignments/list");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/assignments.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            loadDropdowns(req);

            int teacherId = Integer.parseInt(req.getParameter("teacherId"));
            int subjectId = Integer.parseInt(req.getParameter("subjectId"));
            int sectionId = Integer.parseInt(req.getParameter("sectionId"));
            int yearId = Integer.parseInt(req.getParameter("academicYearId"));

            if (assignmentDAO.isTeacherAssigned(teacherId, subjectId, sectionId, yearId)) {
                req.setAttribute("error", "This assignment already exists.");
                req.getRequestDispatcher("/WEB-INF/views/admin/assignment-form.jsp").forward(req, resp);
                return;
            }

            TeacherAssignment ta = new TeacherAssignment();
            ta.setTeacherId(teacherId);
            ta.setSubjectId(subjectId);
            ta.setSectionId(sectionId);
            ta.setAcademicYearId(yearId);
            ta.setActive(true);
            assignmentDAO.insert(ta);

            req.getSession().setAttribute("success", "Assignment created successfully.");
            resp.sendRedirect(req.getContextPath() + "/admin/assignments/list");
        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/assignment-form.jsp").forward(req, resp);
        }
    }

    private void loadDropdowns(HttpServletRequest req) throws Exception {
        req.setAttribute("teachers", teacherDAO.findActive());
        req.setAttribute("subjects", subjectDAO.findActive());
        req.setAttribute("sections", sectionDAO.findActive());
        req.setAttribute("academicYears", yearDAO.findActive());
    }
}
