package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.*;

@WebServlet("/teacher/attendance/*")
public class AttendanceServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            User user = (User) req.getSession().getAttribute("user");
            Teacher teacher = teacherDAO.findByUserId(user.getId());
            if (teacher == null) {
                req.setAttribute("error", "Teacher profile not found.");
                req.getRequestDispatcher("/WEB-INF/views/teacher/attendance.jsp").forward(req, resp);
                return;
            }
            req.setAttribute("teacher", teacher);

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/mark")) {
                // Show attendance marking form
                List<TeacherAssignment> assignments = assignmentDAO.findByTeacherId(teacher.getId());
                req.setAttribute("assignments", assignments);
                req.getRequestDispatcher("/WEB-INF/views/teacher/attendance.jsp").forward(req, resp);

            } else if (pathInfo.equals("/load-students")) {
                // AJAX endpoint: load students for a specific class
                loadStudentsForClass(req, resp, teacher);

            } else if (pathInfo.startsWith("/view/")) {
                // View a saved attendance session
                int sessionId = Integer.parseInt(pathInfo.substring(6));
                AttendanceSession session = attendanceDAO.findSessionById(sessionId);
                if (session == null) {
                    req.setAttribute("error", "Session not found.");
                    resp.sendRedirect(req.getContextPath() + "/teacher/dashboard");
                    return;
                }
                List<AttendanceRecord> records = attendanceDAO.findRecordsBySession(sessionId);
                req.setAttribute("session", session);
                req.setAttribute("records", records);
                req.getRequestDispatcher("/WEB-INF/views/teacher/attendance-view.jsp").forward(req, resp);

            } else if (pathInfo.equals("/history")) {
                // Show attendance history for teacher
                List<AttendanceSession> sessions = attendanceDAO.findSessionsByTeacher(teacher.getId());
                req.setAttribute("sessions", sessions);
                req.getRequestDispatcher("/WEB-INF/views/teacher/attendance-history.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/teacher/attendance.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            User user = (User) req.getSession().getAttribute("user");
            Teacher teacher = teacherDAO.findByUserId(user.getId());
            if (teacher == null) {
                out.print(gson.toJson(Map.of("success", false, "message", "Teacher profile not found.")));
                return;
            }

            if (pathInfo != null && pathInfo.equals("/save")) {
                saveAttendance(req, resp, teacher, user);
            } else {
                out.print(gson.toJson(Map.of("success", false, "message", "Invalid action.")));
            }
        } catch (Exception e) {
            out.print(gson.toJson(Map.of("success", false, "message", "Error: " + e.getMessage())));
        }
    }

    private void loadStudentsForClass(HttpServletRequest req, HttpServletResponse resp, Teacher teacher)
            throws Exception {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
        String dateStr = req.getParameter("date");

        TeacherAssignment assignment = assignmentDAO.findById(assignmentId);
        if (assignment == null || assignment.getTeacherId() != teacher.getId()) {
            out.print(gson.toJson(Map.of("success", false, "message", "Invalid assignment.")));
            return;
        }

        List<Student> students = studentDAO.findByClassGroup(
            assignment.getDepartmentId(),
            assignment.getAcademicYearId(),
            assignment.getSectionId()
        );

        // Check if attendance already exists for this date
        Date sessionDate = Date.valueOf(dateStr);
        AttendanceSession existingSession = attendanceDAO.findSession(
            assignment.getSubjectId(), assignment.getSectionId(),
            assignment.getAcademicYearId(), assignment.getDepartmentId(), sessionDate
        );

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("students", students);
        result.put("existingSession", existingSession != null);

        if (existingSession != null) {
            List<AttendanceRecord> existingRecords = attendanceDAO.findRecordsBySession(existingSession.getId());
            result.put("existingRecords", existingRecords);
            result.put("sessionId", existingSession.getId());
        }

        out.print(gson.toJson(result));
    }

    private void saveAttendance(HttpServletRequest req, HttpServletResponse resp, Teacher teacher, User user)
            throws Exception {
        PrintWriter out = resp.getWriter();

        int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
        String dateStr = req.getParameter("date");
        String[] studentIds = req.getParameterValues("studentId[]");
        String[] statuses = req.getParameterValues("status[]");

        if (studentIds == null || statuses == null || studentIds.length != statuses.length) {
            out.print(gson.toJson(Map.of("success", false, "message", "Invalid attendance data.")));
            return;
        }

        TeacherAssignment assignment = assignmentDAO.findById(assignmentId);
        if (assignment == null || assignment.getTeacherId() != teacher.getId()) {
            out.print(gson.toJson(Map.of("success", false, "message", "You are not authorized for this class.")));
            return;
        }

        Date sessionDate = Date.valueOf(dateStr);

        // Build session
        int presentCount = 0;
        int absentCount = 0;
        List<AttendanceRecord> records = new ArrayList<>();

        for (int i = 0; i < studentIds.length; i++) {
            String status = statuses[i].toUpperCase();
            if (!status.equals("PRESENT") && !status.equals("ABSENT")) {
                out.print(gson.toJson(Map.of("success", false, "message", "Invalid status value.")));
                return;
            }
            if (status.equals("PRESENT")) presentCount++;
            else absentCount++;

            AttendanceRecord record = new AttendanceRecord();
            record.setStudentId(Integer.parseInt(studentIds[i]));
            record.setStatus(status);
            records.add(record);
        }

        AttendanceSession session = new AttendanceSession();
        session.setSubjectId(assignment.getSubjectId());
        session.setTeacherId(teacher.getId());
        session.setSectionId(assignment.getSectionId());
        session.setAcademicYearId(assignment.getAcademicYearId());
        session.setDepartmentId(assignment.getDepartmentId());
        session.setSessionDate(sessionDate);
        session.setTotalStudents(studentIds.length);
        session.setPresentCount(presentCount);
        session.setAbsentCount(absentCount);
        session.setCreatedBy(user.getId());

        int sessionId = attendanceDAO.saveAttendance(session, records);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Attendance saved successfully!");
        result.put("sessionId", sessionId);
        result.put("presentCount", presentCount);
        result.put("absentCount", absentCount);
        result.put("totalStudents", studentIds.length);
        double pct = studentIds.length > 0 ? (presentCount * 100.0 / studentIds.length) : 0.0;
        result.put("percentage", Math.round(pct * 100.0) / 100.0);

        out.print(gson.toJson(result));
    }
}
