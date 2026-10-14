package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/teacher/dashboard")
public class TeacherDashboardServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            Teacher teacher = teacherDAO.findByUserId(user.getId());

            if (teacher == null) {
                req.setAttribute("error", "Teacher profile not found. Please contact administrator.");
                req.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("teacher", teacher);

            List<TeacherAssignment> assignments = assignmentDAO.findByTeacherId(teacher.getId());
            req.setAttribute("assignments", assignments);

            int todaySessions = attendanceDAO.countTodaySessionsByTeacher(teacher.getId());
            int totalSessions = attendanceDAO.countTotalSessionsByTeacher(teacher.getId());
            req.setAttribute("todaySessions", todaySessions);
            req.setAttribute("totalSessions", totalSessions);

            List<AttendanceSession> recentSessions = attendanceDAO.findSessionsByTeacher(teacher.getId());
            // Limit to 10 recent
            if (recentSessions.size() > 10) {
                recentSessions = recentSessions.subList(0, 10);
            }
            req.setAttribute("recentSessions", recentSessions);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to load dashboard: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(req, resp);
    }
}
