package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int totalStudents = studentDAO.countActive();
            int totalTeachers = teacherDAO.countActive();
            int totalSubjects = subjectDAO.countActive();
            int todaySessions = attendanceDAO.countTodaySessions();
            Map<String, Object> overallStats = attendanceDAO.getOverallStats();
            List<AttendanceSession> recentSessions = attendanceDAO.findRecentSessions(10);

            req.setAttribute("totalStudents", totalStudents);
            req.setAttribute("totalTeachers", totalTeachers);
            req.setAttribute("totalSubjects", totalSubjects);
            req.setAttribute("todaySessions", todaySessions);
            req.setAttribute("overallStats", overallStats);
            req.setAttribute("recentSessions", recentSessions);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to load dashboard data: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
