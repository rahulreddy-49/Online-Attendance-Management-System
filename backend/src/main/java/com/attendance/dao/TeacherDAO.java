package com.attendance.dao;

import com.attendance.model.Teacher;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    private static final String SELECT_WITH_JOINS =
        "SELECT t.*, u.full_name, u.email, u.username, d.name AS department_name " +
        "FROM teachers t " +
        "JOIN users u ON t.user_id = u.id " +
        "LEFT JOIN departments d ON t.department_id = d.id ";

    public Teacher findById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE t.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Teacher findByUserId(int userId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE t.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Teacher> findAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY u.full_name";
        List<Teacher> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Teacher> findActive() throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE t.is_active = 1 ORDER BY u.full_name";
        List<Teacher> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(Teacher teacher) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            return insert(conn, teacher);
        }
    }

    public int insert(Connection conn, Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teachers (user_id, employee_id, department_id, phone, qualification, is_active) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, teacher.getUserId());
            ps.setString(2, teacher.getEmployeeId());
            ps.setInt(3, teacher.getDepartmentId());
            ps.setString(4, teacher.getPhone());
            ps.setString(5, teacher.getQualification());
            ps.setBoolean(6, teacher.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(Teacher teacher) throws SQLException {
        String sql = "UPDATE teachers SET employee_id = ?, department_id = ?, phone = ?, qualification = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacher.getEmployeeId());
            ps.setInt(2, teacher.getDepartmentId());
            ps.setString(3, teacher.getPhone());
            ps.setString(4, teacher.getQualification());
            ps.setBoolean(5, teacher.isActive());
            ps.setInt(6, teacher.getId());
            ps.executeUpdate();
        }
    }

    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE teachers SET is_active = 0 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean employeeIdExists(String employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM teachers WHERE employee_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean employeeIdExistsExcluding(String employeeId, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM teachers WHERE employee_id = ? AND id != ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM teachers WHERE is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setEmployeeId(rs.getString("employee_id"));
        t.setDepartmentId(rs.getInt("department_id"));
        t.setPhone(rs.getString("phone"));
        t.setQualification(rs.getString("qualification"));
        t.setActive(rs.getBoolean("is_active"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        t.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            t.setFullName(rs.getString("full_name"));
            t.setEmail(rs.getString("email"));
            t.setUsername(rs.getString("username"));
            t.setDepartmentName(rs.getString("department_name"));
        } catch (SQLException ignored) {}
        return t;
    }
}
