package com.attendance.dao;

import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceSession;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {

    private static final String SESSION_SELECT =
        "SELECT ats.*, sub.name AS subject_name, sub.code AS subject_code, " +
        "u.full_name AS teacher_name, sec.name AS section_name, " +
        "ay.year_label, d.name AS department_name " +
        "FROM attendance_sessions ats " +
        "JOIN subjects sub ON ats.subject_id = sub.id " +
        "JOIN teachers t ON ats.teacher_id = t.id " +
        "JOIN users u ON t.user_id = u.id " +
        "LEFT JOIN sections sec ON ats.section_id = sec.id " +
        "LEFT JOIN academic_years ay ON ats.academic_year_id = ay.id " +
        "LEFT JOIN departments d ON ats.department_id = d.id ";

    // ========== SESSION OPERATIONS ==========

    public AttendanceSession findSessionById(int id) throws SQLException {
        String sql = SESSION_SELECT + "WHERE ats.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSession(rs);
            }
        }
        return null;
    }

    public AttendanceSession findSession(int subjectId, int sectionId, int yearId, int deptId, Date date) throws SQLException {
        String sql = SESSION_SELECT +
            "WHERE ats.subject_id = ? AND ats.section_id = ? AND ats.academic_year_id = ? " +
            "AND ats.department_id = ? AND ats.session_date = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setInt(2, sectionId);
            ps.setInt(3, yearId);
            ps.setInt(4, deptId);
            ps.setDate(5, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSession(rs);
            }
        }
        return null;
    }

    public List<AttendanceSession> findRecentSessions(int limit) throws SQLException {
        String sql = SESSION_SELECT + "ORDER BY ats.session_date DESC, ats.created_at DESC LIMIT ?";
        List<AttendanceSession> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        }
        return list;
    }

    public List<AttendanceSession> findSessionsByTeacher(int teacherId) throws SQLException {
        String sql = SESSION_SELECT + "WHERE ats.teacher_id = ? ORDER BY ats.session_date DESC, ats.created_at DESC";
        List<AttendanceSession> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        }
        return list;
    }

    public List<AttendanceSession> findSessionsByFilters(Integer subjectId, Integer departmentId,
            Integer sectionId, Integer yearId, Date fromDate, Date toDate) throws SQLException {
        StringBuilder sql = new StringBuilder(SESSION_SELECT + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (subjectId != null && subjectId > 0) {
            sql.append("AND ats.subject_id = ? ");
            params.add(subjectId);
        }
        if (departmentId != null && departmentId > 0) {
            sql.append("AND ats.department_id = ? ");
            params.add(departmentId);
        }
        if (sectionId != null && sectionId > 0) {
            sql.append("AND ats.section_id = ? ");
            params.add(sectionId);
        }
        if (yearId != null && yearId > 0) {
            sql.append("AND ats.academic_year_id = ? ");
            params.add(yearId);
        }
        if (fromDate != null) {
            sql.append("AND ats.session_date >= ? ");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append("AND ats.session_date <= ? ");
            params.add(toDate);
        }
        sql.append("ORDER BY ats.session_date DESC");

        List<AttendanceSession> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);
                else if (param instanceof Date) ps.setDate(i + 1, (Date) param);
                else ps.setString(i + 1, (String) param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        }
        return list;
    }

    /**
     * Save a complete attendance session with all records in a single transaction.
     */
    public int saveAttendance(AttendanceSession session, List<AttendanceRecord> records) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Check for existing session
            String checkSql = "SELECT id FROM attendance_sessions WHERE subject_id = ? AND section_id = ? " +
                "AND academic_year_id = ? AND department_id = ? AND session_date = ?";
            int existingSessionId = -1;
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, session.getSubjectId());
                ps.setInt(2, session.getSectionId());
                ps.setInt(3, session.getAcademicYearId());
                ps.setInt(4, session.getDepartmentId());
                ps.setDate(5, session.getSessionDate());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) existingSessionId = rs.getInt(1);
                }
            }

            int sessionId;
            if (existingSessionId > 0) {
                // Update existing session
                String updateSql = "UPDATE attendance_sessions SET total_students = ?, present_count = ?, " +
                    "absent_count = ?, updated_by = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, session.getTotalStudents());
                    ps.setInt(2, session.getPresentCount());
                    ps.setInt(3, session.getAbsentCount());
                    ps.setInt(4, session.getCreatedBy());
                    ps.setInt(5, existingSessionId);
                    ps.executeUpdate();
                }
                sessionId = existingSessionId;

                // Delete existing records for re-insertion
                String deleteSql = "DELETE FROM attendance_records WHERE session_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                    ps.setInt(1, sessionId);
                    ps.executeUpdate();
                }
            } else {
                // Insert new session
                String insertSql = "INSERT INTO attendance_sessions (subject_id, teacher_id, section_id, " +
                    "academic_year_id, department_id, session_date, total_students, present_count, " +
                    "absent_count, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, session.getSubjectId());
                    ps.setInt(2, session.getTeacherId());
                    ps.setInt(3, session.getSectionId());
                    ps.setInt(4, session.getAcademicYearId());
                    ps.setInt(5, session.getDepartmentId());
                    ps.setDate(6, session.getSessionDate());
                    ps.setInt(7, session.getTotalStudents());
                    ps.setInt(8, session.getPresentCount());
                    ps.setInt(9, session.getAbsentCount());
                    ps.setInt(10, session.getCreatedBy());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) sessionId = keys.getInt(1);
                        else throw new SQLException("Failed to get session ID");
                    }
                }
            }

            // Insert attendance records
            String recordSql = "INSERT INTO attendance_records (session_id, student_id, status, remarks) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(recordSql)) {
                for (AttendanceRecord record : records) {
                    ps.setInt(1, sessionId);
                    ps.setInt(2, record.getStudentId());
                    ps.setString(3, record.getStatus());
                    ps.setString(4, record.getRemarks());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return sessionId;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // ========== RECORD OPERATIONS ==========

    public List<AttendanceRecord> findRecordsBySession(int sessionId) throws SQLException {
        String sql = "SELECT ar.*, s.full_name AS student_name, s.roll_number " +
            "FROM attendance_records ar " +
            "JOIN students s ON ar.student_id = s.id " +
            "WHERE ar.session_id = ? ORDER BY s.roll_number";
        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRecord(rs));
            }
        }
        return list;
    }

    // ========== REPORT OPERATIONS ==========

    /**
     * Get attendance summary for a specific student.
     * Returns: total_sessions, present_count, absent_count, percentage
     */
    public Map<String, Object> getStudentAttendanceSummary(int studentId, Integer subjectId,
            Date fromDate, Date toDate) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(ar.id) AS total_sessions, " +
            "SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count, " +
            "SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count " +
            "FROM attendance_records ar " +
            "JOIN attendance_sessions ats ON ar.session_id = ats.id " +
            "WHERE ar.student_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(studentId);

        if (subjectId != null && subjectId > 0) {
            sql.append("AND ats.subject_id = ? ");
            params.add(subjectId);
        }
        if (fromDate != null) {
            sql.append("AND ats.session_date >= ? ");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append("AND ats.session_date <= ? ");
            params.add(toDate);
        }

        Map<String, Object> summary = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);
                else if (param instanceof Date) ps.setDate(i + 1, (Date) param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_sessions");
                    int present = rs.getInt("present_count");
                    int absent = rs.getInt("absent_count");
                    double percentage = total > 0 ? (present * 100.0 / total) : 0.0;
                    summary.put("totalSessions", total);
                    summary.put("presentCount", present);
                    summary.put("absentCount", absent);
                    summary.put("percentage", Math.round(percentage * 100.0) / 100.0);
                }
            }
        }
        return summary;
    }

    /**
     * Get attendance report for all students in a class, optionally filtered by subject and date range.
     */
    public List<Map<String, Object>> getClassAttendanceReport(int departmentId, int yearId, int sectionId,
            Integer subjectId, Date fromDate, Date toDate) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT s.id AS student_id, s.roll_number, s.full_name, " +
            "COUNT(ar.id) AS total_sessions, " +
            "SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count, " +
            "SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count " +
            "FROM students s " +
            "LEFT JOIN attendance_records ar ON s.id = ar.student_id " +
            "LEFT JOIN attendance_sessions ats ON ar.session_id = ats.id ");

        List<Object> params = new ArrayList<>();

        // Add subject filter to the join condition
        if (subjectId != null && subjectId > 0) {
            sql.append("AND ats.subject_id = ? ");
            params.add(subjectId);
        }
        if (fromDate != null) {
            sql.append("AND ats.session_date >= ? ");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append("AND ats.session_date <= ? ");
            params.add(toDate);
        }

        sql.append("WHERE s.department_id = ? AND s.academic_year_id = ? AND s.section_id = ? AND s.is_active = 1 ");
        params.add(departmentId);
        params.add(yearId);
        params.add(sectionId);

        sql.append("GROUP BY s.id, s.roll_number, s.full_name ORDER BY s.roll_number");

        List<Map<String, Object>> report = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);
                else if (param instanceof Date) ps.setDate(i + 1, (Date) param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getInt("student_id"));
                    row.put("rollNumber", rs.getString("roll_number"));
                    row.put("fullName", rs.getString("full_name"));
                    int total = rs.getInt("total_sessions");
                    int present = rs.getInt("present_count");
                    int absent = rs.getInt("absent_count");
                    double percentage = total > 0 ? (present * 100.0 / total) : 0.0;
                    row.put("totalSessions", total);
                    row.put("presentCount", present);
                    row.put("absentCount", absent);
                    row.put("percentage", Math.round(percentage * 100.0) / 100.0);
                    report.add(row);
                }
            }
        }
        return report;
    }

    /**
     * Get students below a configurable attendance threshold.
     */
    public List<Map<String, Object>> getStudentsBelowThreshold(double threshold,
            Integer departmentId, Integer yearId, Integer sectionId) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT s.id AS student_id, s.roll_number, s.full_name, d.name AS department_name, " +
            "ay.year_label, sec.name AS section_name, " +
            "COUNT(ar.id) AS total_sessions, " +
            "SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count " +
            "FROM students s " +
            "LEFT JOIN attendance_records ar ON s.id = ar.student_id " +
            "LEFT JOIN attendance_sessions ats ON ar.session_id = ats.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "LEFT JOIN academic_years ay ON s.academic_year_id = ay.id " +
            "LEFT JOIN sections sec ON s.section_id = sec.id " +
            "WHERE s.is_active = 1 ");
        List<Object> params = new ArrayList<>();

        if (departmentId != null && departmentId > 0) {
            sql.append("AND s.department_id = ? ");
            params.add(departmentId);
        }
        if (yearId != null && yearId > 0) {
            sql.append("AND s.academic_year_id = ? ");
            params.add(yearId);
        }
        if (sectionId != null && sectionId > 0) {
            sql.append("AND s.section_id = ? ");
            params.add(sectionId);
        }

        sql.append("GROUP BY s.id, s.roll_number, s.full_name, d.name, ay.year_label, sec.name ");
        sql.append("HAVING COUNT(ar.id) > 0 AND (SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(ar.id)) < ? ");
        params.add(threshold);
        sql.append("ORDER BY (SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(ar.id))");

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);
                else if (param instanceof Double) ps.setDouble(i + 1, (Double) param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getInt("student_id"));
                    row.put("rollNumber", rs.getString("roll_number"));
                    row.put("fullName", rs.getString("full_name"));
                    row.put("departmentName", rs.getString("department_name"));
                    row.put("yearLabel", rs.getString("year_label"));
                    row.put("sectionName", rs.getString("section_name"));
                    int total = rs.getInt("total_sessions");
                    int present = rs.getInt("present_count");
                    double percentage = (present * 100.0 / total);
                    row.put("totalSessions", total);
                    row.put("presentCount", present);
                    row.put("percentage", Math.round(percentage * 100.0) / 100.0);
                    result.add(row);
                }
            }
        }
        return result;
    }

    // ========== DASHBOARD STATS ==========

    public int countTodaySessions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance_sessions WHERE session_date = CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countTodaySessionsByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance_sessions WHERE session_date = CURDATE() AND teacher_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public Map<String, Object> getOverallStats() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT ats.id) AS total_sessions, " +
            "SUM(ats.present_count) AS total_present, " +
            "SUM(ats.total_students) AS total_records " +
            "FROM attendance_sessions ats";
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int totalSessions = rs.getInt("total_sessions");
                long totalPresent = rs.getLong("total_present");
                long totalRecords = rs.getLong("total_records");
                double overallPercentage = totalRecords > 0 ? (totalPresent * 100.0 / totalRecords) : 0.0;
                stats.put("totalSessions", totalSessions);
                stats.put("totalPresent", totalPresent);
                stats.put("totalRecords", totalRecords);
                stats.put("overallPercentage", Math.round(overallPercentage * 100.0) / 100.0);
            }
        }
        return stats;
    }

    public int countTotalSessionsByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance_sessions WHERE teacher_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // ========== MAPPERS ==========

    private AttendanceSession mapSession(ResultSet rs) throws SQLException {
        AttendanceSession s = new AttendanceSession();
        s.setId(rs.getInt("id"));
        s.setSubjectId(rs.getInt("subject_id"));
        s.setTeacherId(rs.getInt("teacher_id"));
        s.setSectionId(rs.getInt("section_id"));
        s.setAcademicYearId(rs.getInt("academic_year_id"));
        s.setDepartmentId(rs.getInt("department_id"));
        s.setSessionDate(rs.getDate("session_date"));
        s.setTotalStudents(rs.getInt("total_students"));
        s.setPresentCount(rs.getInt("present_count"));
        s.setAbsentCount(rs.getInt("absent_count"));
        s.setCreatedBy(rs.getInt("created_by"));
        s.setUpdatedBy((Integer) rs.getObject("updated_by"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            s.setSubjectName(rs.getString("subject_name"));
            s.setSubjectCode(rs.getString("subject_code"));
            s.setTeacherName(rs.getString("teacher_name"));
            s.setSectionName(rs.getString("section_name"));
            s.setYearLabel(rs.getString("year_label"));
            s.setDepartmentName(rs.getString("department_name"));
        } catch (SQLException ignored) {}
        return s;
    }

    private AttendanceRecord mapRecord(ResultSet rs) throws SQLException {
        AttendanceRecord r = new AttendanceRecord();
        r.setId(rs.getInt("id"));
        r.setSessionId(rs.getInt("session_id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStatus(rs.getString("status"));
        r.setRemarks(rs.getString("remarks"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            r.setStudentName(rs.getString("student_name"));
            r.setRollNumber(rs.getString("roll_number"));
        } catch (SQLException ignored) {}
        return r;
    }
}
