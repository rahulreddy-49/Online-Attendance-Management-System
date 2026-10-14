package com.attendance.dao;

import com.attendance.model.Department;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public Department findById(int id) throws SQLException {
        String sql = "SELECT * FROM departments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Department> findAll() throws SQLException {
        String sql = "SELECT * FROM departments ORDER BY name";
        return executeList(sql);
    }

    public List<Department> findActive() throws SQLException {
        String sql = "SELECT * FROM departments WHERE is_active = 1 ORDER BY name";
        return executeList(sql);
    }

    public int insert(Department dept) throws SQLException {
        String sql = "INSERT INTO departments (name, code, description, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDescription());
            ps.setBoolean(4, dept.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(Department dept) throws SQLException {
        String sql = "UPDATE departments SET name = ?, code = ?, description = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDescription());
            ps.setBoolean(4, dept.isActive());
            ps.setInt(5, dept.getId());
            ps.executeUpdate();
        }
    }

    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE departments SET is_active = 0 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private List<Department> executeList(String sql) throws SQLException {
        List<Department> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        d.setCode(rs.getString("code"));
        d.setDescription(rs.getString("description"));
        d.setActive(rs.getBoolean("is_active"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));
        return d;
    }
}
