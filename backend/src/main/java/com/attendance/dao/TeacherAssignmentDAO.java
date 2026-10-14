package com.attendance.dao;

import com.attendance.model.TeacherAssignment;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherAssignmentDAO {

    private static final String SELECT_WITH_JOINS =
        "SELECT tsa.*, u.full_name AS teacher_name, sub.name AS subject_name, sub.code AS subject_code, " +
        "sec.name AS section_name, ay.year_label, d.name AS department_name, sub.department_id " +
        "FROM teacher_subject_assignments tsa " +
        "JOIN teachers t ON tsa.teacher_id = t.id " +
        "JOIN users u ON t.user_id = u.id " +
        "JOIN subjects sub ON tsa.subject_id = sub.id " +
        "LEFT JOIN sections sec ON tsa.section_id = sec.id " +
        "LEFT JOIN academic_years ay ON tsa.academic_year_id = ay.id " +
        "LEFT JOIN departments d ON sub.department_id = d.id ";

    public TeacherAssignment findById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE tsa.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<TeacherAssignment> findAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY u.full_name, sub.code";
        return executeList(sql);
    }

    public List<TeacherAssignment> findByTeacherId(int teacherId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE tsa.teacher_id = ? AND tsa.is_active = 1 ORDER BY sub.code";
        List<TeacherAssignment> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(TeacherAssignment ta) throws SQLException {
        String sql = "INSERT INTO teacher_subject_assignments (teacher_id, subject_id, section_id, academic_year_id, is_active) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ta.getTeacherId());
            ps.setInt(2, ta.getSubjectId());
            ps.setInt(3, ta.getSectionId());
            ps.setInt(4, ta.getAcademicYearId());
            ps.setBoolean(5, ta.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(TeacherAssignment ta) throws SQLException {
        String sql = "UPDATE teacher_subject_assignments SET teacher_id = ?, subject_id = ?, section_id = ?, " +
            "academic_year_id = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ta.getTeacherId());
            ps.setInt(2, ta.getSubjectId());
            ps.setInt(3, ta.getSectionId());
            ps.setInt(4, ta.getAcademicYearId());
            ps.setBoolean(5, ta.isActive());
            ps.setInt(6, ta.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM teacher_subject_assignments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Check if a teacher is assigned to a specific subject/section/year combination.
     */
    public boolean isTeacherAssigned(int teacherId, int subjectId, int sectionId, int yearId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM teacher_subject_assignments " +
            "WHERE teacher_id = ? AND subject_id = ? AND section_id = ? AND academic_year_id = ? AND is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, subjectId);
            ps.setInt(3, sectionId);
            ps.setInt(4, yearId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private List<TeacherAssignment> executeList(String sql) throws SQLException {
        List<TeacherAssignment> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private TeacherAssignment mapRow(ResultSet rs) throws SQLException {
        TeacherAssignment ta = new TeacherAssignment();
        ta.setId(rs.getInt("id"));
        ta.setTeacherId(rs.getInt("teacher_id"));
        ta.setSubjectId(rs.getInt("subject_id"));
        ta.setSectionId(rs.getInt("section_id"));
        ta.setAcademicYearId(rs.getInt("academic_year_id"));
        ta.setActive(rs.getBoolean("is_active"));
        ta.setCreatedAt(rs.getTimestamp("created_at"));
        ta.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            ta.setTeacherName(rs.getString("teacher_name"));
            ta.setSubjectName(rs.getString("subject_name"));
            ta.setSubjectCode(rs.getString("subject_code"));
            ta.setSectionName(rs.getString("section_name"));
            ta.setYearLabel(rs.getString("year_label"));
            ta.setDepartmentName(rs.getString("department_name"));
            ta.setDepartmentId(rs.getInt("department_id"));
        } catch (SQLException ignored) {}
        return ta;
    }
}
