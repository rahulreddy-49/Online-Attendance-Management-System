package com.attendance.dao;

import com.attendance.model.Section;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public Section findById(int id) throws SQLException {
        String sql = "SELECT * FROM sections WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Section> findAll() throws SQLException {
        String sql = "SELECT * FROM sections ORDER BY name";
        return executeList(sql);
    }

    public List<Section> findActive() throws SQLException {
        String sql = "SELECT * FROM sections WHERE is_active = 1 ORDER BY name";
        return executeList(sql);
    }

    public int insert(Section section) throws SQLException {
        String sql = "INSERT INTO sections (name, is_active) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, section.getName());
            ps.setBoolean(2, section.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(Section section) throws SQLException {
        String sql = "UPDATE sections SET name = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, section.getName());
            ps.setBoolean(2, section.isActive());
            ps.setInt(3, section.getId());
            ps.executeUpdate();
        }
    }

    private List<Section> executeList(String sql) throws SQLException {
        List<Section> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Section mapRow(ResultSet rs) throws SQLException {
        Section s = new Section();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setActive(rs.getBoolean("is_active"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
