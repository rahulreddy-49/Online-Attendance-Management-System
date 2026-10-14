package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@WebServlet({"/admin/reports", "/teacher/reports"})
public class ReportServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AcademicYearDAO yearDAO = new AcademicYearDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            req.setAttribute("departments", departmentDAO.findActive());
            req.setAttribute("academicYears", yearDAO.findActive());
            req.setAttribute("sections", sectionDAO.findActive());
            req.setAttribute("subjects", subjectDAO.findActive());

            // If teacher, filter available subjects/classes
            if (user != null && user.isTeacher()) {
                Teacher teacher = teacherDAO.findByUserId(user.getId());
                if (teacher != null) {
                    List<TeacherAssignment> assignments = assignmentDAO.findByTeacherId(teacher.getId());
                    req.setAttribute("assignments", assignments);
                }
            }

            String deptParam = req.getParameter("departmentId");
            String yearParam = req.getParameter("academicYearId");
            String secParam = req.getParameter("sectionId");
            String subParam = req.getParameter("subjectId");
            String fromParam = req.getParameter("fromDate");
            String toParam = req.getParameter("toDate");
            String export = req.getParameter("export");

            if (deptParam != null && !deptParam.isEmpty() &&
                yearParam != null && !yearParam.isEmpty() &&
                secParam != null && !secParam.isEmpty()) {

                int deptId = Integer.parseInt(deptParam);
                int yearId = Integer.parseInt(yearParam);
                int secId = Integer.parseInt(secParam);
                Integer subId = (subParam != null && !subParam.isEmpty()) ? Integer.parseInt(subParam) : null;
                Date fromDate = (fromParam != null && !fromParam.isEmpty()) ? Date.valueOf(fromParam) : null;
                Date toDate = (toParam != null && !toParam.isEmpty()) ? Date.valueOf(toParam) : null;

                List<Map<String, Object>> reportData = attendanceDAO.getClassAttendanceReport(
                    deptId, yearId, secId, subId, fromDate, toDate
                );

                if ("csv".equalsIgnoreCase(export)) {
                    exportCsv(resp, reportData);
                    return;
                }

                req.setAttribute("reportData", reportData);
                req.setAttribute("selectedDept", deptId);
                req.setAttribute("selectedYear", yearId);
                req.setAttribute("selectedSec", secId);
                req.setAttribute("selectedSub", subId);
                req.setAttribute("selectedFrom", fromParam);
                req.setAttribute("selectedTo", toParam);
            }

            String forwardPath = user != null && user.isTeacher()
                ? "/WEB-INF/views/teacher/reports.jsp"
                : "/WEB-INF/views/admin/reports.jsp";

            req.getRequestDispatcher(forwardPath).forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Error loading attendance report: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    private void exportCsv(HttpServletResponse resp, List<Map<String, Object>> data) throws IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"attendance_report_" + System.currentTimeMillis() + ".csv\"");
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("Roll Number,Student Name,Total Sessions,Present Count,Absent Count,Attendance Percentage");
            for (Map<String, Object> row : data) {
                writer.printf("\"%s\",\"%s\",%s,%s,%s,%s%%%n",
                    row.get("rollNumber"),
                    row.get("fullName"),
                    row.get("totalSessions"),
                    row.get("presentCount"),
                    row.get("absentCount"),
                    row.get("percentage")
                );
            }
        }
    }
}
