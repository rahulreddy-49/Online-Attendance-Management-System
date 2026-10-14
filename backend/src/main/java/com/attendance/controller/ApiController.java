package com.attendance.controller;

import com.attendance.dao.*;
import com.attendance.model.*;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.*;

@WebServlet("/api/*")
public class ApiController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AcademicYearDAO yearDAO = new AcademicYearDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final TeacherAssignmentDAO assignmentDAO = new TeacherAssignmentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setJsonResponse(resp);
        String path = req.getPathInfo();
        if (path == null) path = "";

        try {
            switch (path) {
                case "/auth/me":
                    handleAuthMe(req, resp);
                    break;

                case "/auth/logout":
                    handleLogout(req, resp);
                    break;

                case "/dashboard/stats":
                    handleDashboardStats(req, resp);
                    break;

                case "/students":
                    handleGetStudents(req, resp);
                    break;

                case "/teachers":
                    handleGetTeachers(req, resp);
                    break;

                case "/subjects":
                    handleGetSubjects(req, resp);
                    break;

                case "/assignments":
                    handleGetAssignments(req, resp);
                    break;

                case "/departments":
                    sendJson(resp, departmentDAO.findActive());
                    break;

                case "/academic-years":
                    sendJson(resp, yearDAO.findActive());
                    break;

                case "/sections":
                    sendJson(resp, sectionDAO.findActive());
                    break;

                case "/teacher/dashboard":
                    handleTeacherDashboard(req, resp);
                    break;

                case "/attendance/load-students":
                    handleLoadStudents(req, resp);
                    break;

                case "/attendance/history":
                    handleAttendanceHistory(req, resp);
                    break;

                case "/reports":
                    handleReports(req, resp);
                    break;

                default:
                    if (path.startsWith("/attendance/session/")) {
                        int sessionId = Integer.parseInt(path.substring("/attendance/session/".length()));
                        handleSessionDetails(sessionId, resp);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        sendJson(resp, Map.of("error", "API endpoint not found: " + path));
                    }
                    break;
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJson(resp, Map.of("error", e.getMessage() != null ? e.getMessage() : "Internal Server Error"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setJsonResponse(resp);
        String path = req.getPathInfo();
        if (path == null) path = "";

        try {
            switch (path) {
                case "/auth/login":
                    handleLogin(req, resp);
                    break;

                case "/auth/logout":
                    handleLogout(req, resp);
                    break;

                case "/students":
                    handleSaveStudent(req, resp);
                    break;

                case "/teachers":
                    handleSaveTeacher(req, resp);
                    break;

                case "/subjects":
                    handleSaveSubject(req, resp);
                    break;

                case "/assignments":
                    handleSaveAssignment(req, resp);
                    break;

                case "/attendance/save":
                    handleSaveAttendance(req, resp);
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendJson(resp, Map.of("error", "API endpoint not found: " + path));
                    break;
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJson(resp, Map.of("error", e.getMessage() != null ? e.getMessage() : "Internal Server Error"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setJsonResponse(resp);
        String path = req.getPathInfo();
        if (path == null) path = "";

        try {
            if (path.startsWith("/students/")) {
                int id = Integer.parseInt(path.substring("/students/".length()));
                studentDAO.deactivate(id);
                sendJson(resp, Map.of("success", true, "message", "Student deactivated"));
            } else if (path.startsWith("/teachers/")) {
                int id = Integer.parseInt(path.substring("/teachers/".length()));
                teacherDAO.deactivate(id);
                sendJson(resp, Map.of("success", true, "message", "Teacher deactivated"));
            } else if (path.startsWith("/subjects/")) {
                int id = Integer.parseInt(path.substring("/subjects/".length()));
                subjectDAO.deactivate(id);
                sendJson(resp, Map.of("success", true, "message", "Subject deactivated"));
            } else if (path.startsWith("/assignments/")) {
                int id = Integer.parseInt(path.substring("/assignments/".length()));
                assignmentDAO.delete(id);
                sendJson(resp, Map.of("success", true, "message", "Assignment removed"));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendJson(resp, Map.of("error", "Resource not found"));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJson(resp, Map.of("error", e.getMessage() != null ? e.getMessage() : "Error executing delete"));
        }
    }

    // ========== HANDLERS ==========

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = parseJsonBody(req);
        String username = body.has("username") ? body.get("username").getAsString().trim() : "";
        String password = body.has("password") ? body.get("password").getAsString().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJson(resp, Map.of("success", false, "message", "Username and password required"));
            return;
        }

        User user = userDAO.findByUsername(username);
        if (user == null || !user.isActive()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJson(resp, Map.of("success", false, "message", "Invalid username or password"));
            return;
        }

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPasswordHash());
        if (!result.verified) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJson(resp, Map.of("success", false, "message", "Invalid username or password"));
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userName", user.getFullName());

        userDAO.updateLastLogin(user.getId());

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("fullName", user.getFullName());
        userData.put("role", user.getRole());
        userData.put("email", user.getEmail());

        sendJson(resp, Map.of("success", true, "user", userData));
    }

    private void handleAuthMe(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("fullName", user.getFullName());
            userData.put("role", user.getRole());
            userData.put("email", user.getEmail());
            sendJson(resp, Map.of("loggedIn", true, "user", userData));
        } else {
            sendJson(resp, Map.of("loggedIn", false));
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sendJson(resp, Map.of("success", true, "message", "Logged out successfully"));
    }

    private void handleDashboardStats(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int totalStudents = studentDAO.countActive();
        int totalTeachers = teacherDAO.countActive();
        int totalSubjects = subjectDAO.countActive();
        int todaySessions = attendanceDAO.countTodaySessions();
        Map<String, Object> overallStats = attendanceDAO.getOverallStats();
        List<AttendanceSession> recentSessions = attendanceDAO.findRecentSessions(10);

        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", totalStudents);
        data.put("totalTeachers", totalTeachers);
        data.put("totalSubjects", totalSubjects);
        data.put("todaySessions", todaySessions);
        data.put("overallStats", overallStats);
        data.put("recentSessions", recentSessions);

        sendJson(resp, data);
    }

    private void handleGetStudents(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String dept = req.getParameter("departmentId");
        String yr = req.getParameter("academicYearId");
        String sec = req.getParameter("sectionId");

        List<Student> students;
        if (dept != null && !dept.isEmpty() && yr != null && !yr.isEmpty() && sec != null && !sec.isEmpty()) {
            students = studentDAO.findByClassGroup(Integer.parseInt(dept), Integer.parseInt(yr), Integer.parseInt(sec));
        } else {
            students = studentDAO.findAll();
        }
        sendJson(resp, students);
    }

    private void handleGetTeachers(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        sendJson(resp, teacherDAO.findAll());
    }

    private void handleGetSubjects(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        sendJson(resp, subjectDAO.findAll());
    }

    private void handleGetAssignments(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String teacherId = req.getParameter("teacherId");
        if (teacherId != null && !teacherId.isEmpty()) {
            sendJson(resp, assignmentDAO.findByTeacherId(Integer.parseInt(teacherId)));
        } else {
            sendJson(resp, assignmentDAO.findAll());
        }
    }

    private void handleTeacherDashboard(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = (User) req.getSession().getAttribute("user");
        int teacherId = -1;
        Teacher teacher = null;
        if (user != null) {
            teacher = teacherDAO.findByUserId(user.getId());
            if (teacher != null) teacherId = teacher.getId();
        }

        // Fallback for standalone demo if session not active
        if (teacher == null) {
            List<Teacher> all = teacherDAO.findActive();
            if (!all.isEmpty()) {
                teacher = all.get(0);
                teacherId = teacher.getId();
            }
        }

        Map<String, Object> data = new HashMap<>();
        if (teacher != null) {
            data.put("teacher", teacher);
            data.put("assignments", assignmentDAO.findByTeacherId(teacherId));
            data.put("todaySessions", attendanceDAO.countTodaySessionsByTeacher(teacherId));
            data.put("totalSessions", attendanceDAO.countTotalSessionsByTeacher(teacherId));
            data.put("recentSessions", attendanceDAO.findSessionsByTeacher(teacherId));
        }
        sendJson(resp, data);
    }

    private void handleLoadStudents(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
        String dateStr = req.getParameter("date");

        TeacherAssignment assignment = assignmentDAO.findById(assignmentId);
        if (assignment == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJson(resp, Map.of("success", false, "message", "Invalid class assignment"));
            return;
        }

        List<Student> students = studentDAO.findByClassGroup(
            assignment.getDepartmentId(),
            assignment.getAcademicYearId(),
            assignment.getSectionId()
        );

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
            result.put("existingRecords", attendanceDAO.findRecordsBySession(existingSession.getId()));
            result.put("sessionId", existingSession.getId());
        }

        sendJson(resp, result);
    }

    private void handleSaveAttendance(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = parseJsonBody(req);
        int assignmentId = body.get("assignmentId").getAsInt();
        String sessionDateStr = body.get("sessionDate").getAsString();

        TeacherAssignment assignment = assignmentDAO.findById(assignmentId);
        if (assignment == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJson(resp, Map.of("success", false, "message", "Assignment not found"));
            return;
        }

        Type recordListType = new TypeToken<List<AttendanceRecord>>(){}.getType();
        List<AttendanceRecord> records = gson.fromJson(body.get("records"), recordListType);

        int presentCount = 0;
        int absentCount = 0;
        for (AttendanceRecord r : records) {
            if ("PRESENT".equalsIgnoreCase(r.getStatus())) presentCount++;
            else absentCount++;
        }

        User user = (User) req.getSession().getAttribute("user");
        int createdBy = user != null ? user.getId() : 1;

        AttendanceSession session = new AttendanceSession();
        session.setSubjectId(assignment.getSubjectId());
        session.setTeacherId(assignment.getTeacherId());
        session.setSectionId(assignment.getSectionId());
        session.setAcademicYearId(assignment.getAcademicYearId());
        session.setDepartmentId(assignment.getDepartmentId());
        session.setSessionDate(Date.valueOf(sessionDateStr));
        session.setTotalStudents(records.size());
        session.setPresentCount(presentCount);
        session.setAbsentCount(absentCount);
        session.setCreatedBy(createdBy);

        int sessionId = attendanceDAO.saveAttendance(session, records);
        sendJson(resp, Map.of("success", true, "sessionId", sessionId, "message", "Attendance recorded successfully"));
    }

    private void handleAttendanceHistory(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String teacherIdParam = req.getParameter("teacherId");
        int teacherId = 1;
        if (teacherIdParam != null && !teacherIdParam.isEmpty()) {
            teacherId = Integer.parseInt(teacherIdParam);
        } else {
            User user = (User) req.getSession().getAttribute("user");
            if (user != null) {
                Teacher t = teacherDAO.findByUserId(user.getId());
                if (t != null) teacherId = t.getId();
            }
        }
        sendJson(resp, attendanceDAO.findSessionsByTeacher(teacherId));
    }

    private void handleSessionDetails(int sessionId, HttpServletResponse resp) throws Exception {
        AttendanceSession session = attendanceDAO.findSessionById(sessionId);
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            sendJson(resp, Map.of("error", "Session not found"));
            return;
        }
        List<AttendanceRecord> records = attendanceDAO.findRecordsBySession(sessionId);
        Map<String, Object> data = new HashMap<>();
        data.put("session", session);
        data.put("records", records);
        sendJson(resp, data);
    }

    private void handleReports(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int deptId = Integer.parseInt(req.getParameter("departmentId"));
        int yearId = Integer.parseInt(req.getParameter("academicYearId"));
        int secId = Integer.parseInt(req.getParameter("sectionId"));
        String subParam = req.getParameter("subjectId");
        String fromParam = req.getParameter("fromDate");
        String toParam = req.getParameter("toDate");

        Integer subId = (subParam != null && !subParam.isEmpty()) ? Integer.parseInt(subParam) : null;
        Date fromDate = (fromParam != null && !fromParam.isEmpty()) ? Date.valueOf(fromParam) : null;
        Date toDate = (toParam != null && !toParam.isEmpty()) ? Date.valueOf(toParam) : null;

        List<Map<String, Object>> data = attendanceDAO.getClassAttendanceReport(deptId, yearId, secId, subId, fromDate, toDate);
        sendJson(resp, data);
    }

    private void handleSaveStudent(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = parseJsonBody(req);
        Student s = new Student();
        if (body.has("id") && !body.get("id").isJsonNull()) {
            s.setId(body.get("id").getAsInt());
        }
        s.setRollNumber(body.get("rollNumber").getAsString().trim());
        s.setFullName(body.get("fullName").getAsString().trim());
        s.setDepartmentId(body.get("departmentId").getAsInt());
        s.setAcademicYearId(body.get("academicYearId").getAsInt());
        s.setSectionId(body.get("sectionId").getAsInt());
        s.setEmail(body.has("email") && !body.get("email").isJsonNull() ? body.get("email").getAsString().trim() : "");
        s.setPhone(body.has("phone") && !body.get("phone").isJsonNull() ? body.get("phone").getAsString().trim() : "");
        s.setActive(true);

        if (s.getId() > 0) {
            studentDAO.update(s);
            sendJson(resp, Map.of("success", true, "message", "Student updated"));
        } else {
            int newId = studentDAO.insert(s);
            sendJson(resp, Map.of("success", true, "id", newId, "message", "Student created"));
        }
    }

    private void handleSaveTeacher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = parseJsonBody(req);
        String fullName = body.get("fullName").getAsString().trim();
        String employeeId = body.get("employeeId").getAsString().trim();
        int departmentId = body.get("departmentId").getAsInt();
        String email = body.has("email") && !body.get("email").isJsonNull() ? body.get("email").getAsString().trim() : "";
        String phone = body.has("phone") && !body.get("phone").isJsonNull() ? body.get("phone").getAsString().trim() : "";
        String qualification = body.has("qualification") && !body.get("qualification").isJsonNull() ? body.get("qualification").getAsString().trim() : "";

        if (body.has("id") && !body.get("id").isJsonNull()) {
            int id = body.get("id").getAsInt();
            Teacher t = teacherDAO.findById(id);
            t.setEmployeeId(employeeId);
            t.setDepartmentId(departmentId);
            t.setPhone(phone);
            t.setQualification(qualification);
            teacherDAO.update(t);

            User u = userDAO.findById(t.getUserId());
            u.setFullName(fullName);
            u.setEmail(email);
            userDAO.update(u);
            sendJson(resp, Map.of("success", true, "message", "Teacher updated"));
        } else {
            String username = body.get("username").getAsString().trim();
            String password = body.get("password").getAsString().trim();
            String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

            User u = new User();
            u.setUsername(username);
            u.setPasswordHash(hash);
            u.setRole("TEACHER");
            u.setFullName(fullName);
            u.setEmail(email);
            u.setActive(true);
            int userId = userDAO.insert(u);

            Teacher t = new Teacher();
            t.setUserId(userId);
            t.setEmployeeId(employeeId);
            t.setDepartmentId(departmentId);
            t.setPhone(phone);
            t.setQualification(qualification);
            t.setActive(true);
            int teacherId = teacherDAO.insert(t);
            sendJson(resp, Map.of("success", true, "id", teacherId, "message", "Teacher registered"));
        }
    }

    private void handleSaveSubject(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = parseJsonBody(req);
        Subject s = new Subject();
        if (body.has("id") && !body.get("id").isJsonNull()) {
            s.setId(body.get("id").getAsInt());
        }
        s.setCode(body.get("code").getAsString().trim().toUpperCase());
        s.setName(body.get("name").getAsString().trim());
        s.setDepartmentId(body.get("departmentId").getAsInt());
        s.setAcademicYearId(body.get("academicYearId").getAsInt());
        s.setCredits(body.has("credits") ? body.get("credits").getAsInt() : 3);
        s.setActive(true);

        if (s.getId() > 0) {
            subjectDAO.update(s);
            sendJson(resp, Map.of("success", true, "message", "Subject updated"));
        } else {
            int newId = subjectDAO.insert(s);
            sendJson(resp, Map.of("success", true, "id", newId, "message", "Subject created"));
        }
    }

    private void handleSaveAssignment(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JsonObject body = parseJsonBody(req);
        int teacherId = body.get("teacherId").getAsInt();
        int subjectId = body.get("subjectId").getAsInt();
        int sectionId = body.get("sectionId").getAsInt();
        int yearId = body.get("academicYearId").getAsInt();

        if (assignmentDAO.isTeacherAssigned(teacherId, subjectId, sectionId, yearId)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJson(resp, Map.of("success", false, "message", "Assignment already exists"));
            return;
        }

        TeacherAssignment ta = new TeacherAssignment();
        ta.setTeacherId(teacherId);
        ta.setSubjectId(subjectId);
        ta.setSectionId(sectionId);
        ta.setAcademicYearId(yearId);
        ta.setActive(true);
        int id = assignmentDAO.insert(ta);
        sendJson(resp, Map.of("success", true, "id", id, "message", "Assignment created"));
    }

    // ========== UTILITIES ==========

    private void setJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void sendJson(HttpServletResponse resp, Object obj) throws IOException {
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(obj));
        out.flush();
    }

    private JsonObject parseJsonBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        if (sb.length() == 0) return new JsonObject();
        return gson.fromJson(sb.toString(), JsonObject.class);
    }
}
