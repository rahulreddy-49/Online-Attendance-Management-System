package com.attendance.dao;

import com.attendance.model.Subject;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    private static final String SELECT_WITH_JOINS =
        "SELECT s.*, d.name AS department_name, ay.year_label " +
        "FROM subjects s " +
        "LEFT JOIN departments d ON s.department_id = d.id " +
        "LEFT JOIN academic_years ay ON s.academic_year_id = ay.id ";

    public Subject findById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE s.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Subject> findAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY s.code";
        return executeList(sql);
    }

    public List<Subject> findActive() throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE s.is_active = 1 ORDER BY s.code";
        return executeList(sql);
    }

    public List<Subject> findByDepartment(int departmentId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE s.department_id = ? AND s.is_active = 1 ORDER BY s.code";
        List<Subject> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (name, code, department_id, academic_year_id, credits, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, subject.getName());
            ps.setString(2, subject.getCode());
            ps.setInt(3, subject.getDepartmentId());
            ps.setInt(4, subject.getAcademicYearId());
            ps.setInt(5, subject.getCredits());
            ps.setBoolean(6, subject.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, code = ?, department_id = ?, academic_year_id = ?, credits = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getName());
            ps.setString(2, subject.getCode());
            ps.setInt(3, subject.getDepartmentId());
            ps.setInt(4, subject.getAcademicYearId());
            ps.setInt(5, subject.getCredits());
            ps.setBoolean(6, subject.isActive());
            ps.setInt(7, subject.getId());
            ps.executeUpdate();
        }
    }

    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE subjects SET is_active = 0 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean codeExists(String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects WHERE code = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean codeExistsExcluding(String code, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects WHERE code = ? AND id != ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects WHERE is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private List<Subject> executeList(String sql) throws SQLException {
        List<Subject> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Subject mapRow(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setCode(rs.getString("code"));
        s.setDepartmentId(rs.getInt("department_id"));
        s.setAcademicYearId(rs.getInt("academic_year_id"));
        s.setCredits(rs.getInt("credits"));
        s.setActive(rs.getBoolean("is_active"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            s.setDepartmentName(rs.getString("department_name"));
            s.setYearLabel(rs.getString("year_label"));
        } catch (SQLException ignored) {}
        return s;
    }
}
