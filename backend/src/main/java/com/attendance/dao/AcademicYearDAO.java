package com.attendance.dao;

import com.attendance.model.AcademicYear;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcademicYearDAO {

    public AcademicYear findById(int id) throws SQLException {
        String sql = "SELECT * FROM academic_years WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<AcademicYear> findAll() throws SQLException {
        String sql = "SELECT * FROM academic_years ORDER BY sort_order";
        return executeList(sql);
    }

    public List<AcademicYear> findActive() throws SQLException {
        String sql = "SELECT * FROM academic_years WHERE is_active = 1 ORDER BY sort_order";
        return executeList(sql);
    }

    public int insert(AcademicYear ay) throws SQLException {
        String sql = "INSERT INTO academic_years (year_label, sort_order, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ay.getYearLabel());
            ps.setInt(2, ay.getSortOrder());
            ps.setBoolean(3, ay.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(AcademicYear ay) throws SQLException {
        String sql = "UPDATE academic_years SET year_label = ?, sort_order = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ay.getYearLabel());
            ps.setInt(2, ay.getSortOrder());
            ps.setBoolean(3, ay.isActive());
            ps.setInt(4, ay.getId());
            ps.executeUpdate();
        }
    }

    private List<AcademicYear> executeList(String sql) throws SQLException {
        List<AcademicYear> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private AcademicYear mapRow(ResultSet rs) throws SQLException {
        AcademicYear ay = new AcademicYear();
        ay.setId(rs.getInt("id"));
        ay.setYearLabel(rs.getString("year_label"));
        ay.setSortOrder(rs.getInt("sort_order"));
        ay.setActive(rs.getBoolean("is_active"));
        ay.setCreatedAt(rs.getTimestamp("created_at"));
        ay.setUpdatedAt(rs.getTimestamp("updated_at"));
        return ay;
    }
}
