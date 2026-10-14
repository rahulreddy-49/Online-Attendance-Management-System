package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/subjects/*")
public class SubjectServlet extends HttpServlet {

    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AcademicYearDAO yearDAO = new AcademicYearDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            loadDropdowns(req);

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                List<Subject> subjects = subjectDAO.findAll();
                req.setAttribute("subjects", subjects);
                HttpSession session = req.getSession();
                if (session.getAttribute("success") != null) {
                    req.setAttribute("success", session.getAttribute("success"));
                    session.removeAttribute("success");
                }
                req.getRequestDispatcher("/WEB-INF/views/admin/subjects.jsp").forward(req, resp);
            } else if (pathInfo.equals("/add")) {
                req.getRequestDispatcher("/WEB-INF/views/admin/subject-form.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/edit/")) {
                int id = Integer.parseInt(pathInfo.substring(6));
                Subject subject = subjectDAO.findById(id);
                if (subject == null) {
                    req.setAttribute("error", "Subject not found.");
                    resp.sendRedirect(req.getContextPath() + "/admin/subjects/list");
                    return;
                }
                req.setAttribute("subject", subject);
                req.getRequestDispatcher("/WEB-INF/views/admin/subject-form.jsp").forward(req, resp);
            } else if (pathInfo.startsWith("/deactivate/")) {
                int id = Integer.parseInt(pathInfo.substring(12));
                subjectDAO.deactivate(id);
                req.getSession().setAttribute("success", "Subject deactivated successfully.");
                resp.sendRedirect(req.getContextPath() + "/admin/subjects/list");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/subjects/list");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/subjects.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            loadDropdowns(req);

            String name = req.getParameter("name");
            String code = req.getParameter("code");
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            int yearId = Integer.parseInt(req.getParameter("academicYearId"));
            int credits = Integer.parseInt(req.getParameter("credits"));

            if (name == null || name.trim().isEmpty() || code == null || code.trim().isEmpty()) {
                req.setAttribute("error", "Subject name and code are required.");
                req.getRequestDispatcher("/WEB-INF/views/admin/subject-form.jsp").forward(req, resp);
                return;
            }

            if (pathInfo != null && pathInfo.startsWith("/edit/")) {
                int id = Integer.parseInt(pathInfo.substring(6));
                if (subjectDAO.codeExistsExcluding(code.trim(), id)) {
                    req.setAttribute("error", "Subject code already exists.");
                    req.setAttribute("subject", subjectDAO.findById(id));
                    req.getRequestDispatcher("/WEB-INF/views/admin/subject-form.jsp").forward(req, resp);
                    return;
                }
                Subject subject = new Subject();
                subject.setId(id);
                subject.setName(name.trim());
                subject.setCode(code.trim().toUpperCase());
                subject.setDepartmentId(departmentId);
                subject.setAcademicYearId(yearId);
                subject.setCredits(credits);
                subject.setActive(true);
                subjectDAO.update(subject);
                req.getSession().setAttribute("success", "Subject updated successfully.");
            } else {
                if (subjectDAO.codeExists(code.trim())) {
                    req.setAttribute("error", "Subject code already exists.");
                    req.getRequestDispatcher("/WEB-INF/views/admin/subject-form.jsp").forward(req, resp);
                    return;
                }
                Subject subject = new Subject();
                subject.setName(name.trim());
                subject.setCode(code.trim().toUpperCase());
                subject.setDepartmentId(departmentId);
                subject.setAcademicYearId(yearId);
                subject.setCredits(credits);
                subject.setActive(true);
                subjectDAO.insert(subject);
                req.getSession().setAttribute("success", "Subject added successfully.");
            }

            resp.sendRedirect(req.getContextPath() + "/admin/subjects/list");
        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/subject-form.jsp").forward(req, resp);
        }
    }

    private void loadDropdowns(HttpServletRequest req) throws Exception {
        req.setAttribute("departments", departmentDAO.findActive());
        req.setAttribute("academicYears", yearDAO.findActive());
    }
}
