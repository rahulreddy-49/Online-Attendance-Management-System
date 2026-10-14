package com.attendance.dao;

import com.attendance.model.Student;
import com.attendance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String SELECT_WITH_JOINS =
        "SELECT s.*, d.name AS department_name, ay.year_label, sec.name AS section_name " +
        "FROM students s " +
        "LEFT JOIN departments d ON s.department_id = d.id " +
        "LEFT JOIN academic_years ay ON s.academic_year_id = ay.id " +
        "LEFT JOIN sections sec ON s.section_id = sec.id ";

    public Student findById(int id) throws SQLException {
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

    public Student findByRollNumber(String rollNumber) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE s.roll_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Student> findAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY s.roll_number";
        return executeQuery(sql);
    }

    public List<Student> findActive() throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE s.is_active = 1 ORDER BY s.roll_number";
        return executeQuery(sql);
    }

    public List<Student> findByFilters(Integer departmentId, Integer yearId, Integer sectionId, String search) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_WITH_JOINS + "WHERE s.is_active = 1 ");
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
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (s.full_name LIKE ? OR s.roll_number LIKE ?) ");
            params.add("%" + search.trim() + "%");
            params.add("%" + search.trim() + "%");
        }
        sql.append("ORDER BY s.roll_number");

        List<Student> students = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else {
                    ps.setString(i + 1, (String) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRow(rs));
                }
            }
        }
        return students;
    }

    public List<Student> findByClassGroup(int departmentId, int yearId, int sectionId) throws SQLException {
        String sql = SELECT_WITH_JOINS +
            "WHERE s.is_active = 1 AND s.department_id = ? AND s.academic_year_id = ? AND s.section_id = ? " +
            "ORDER BY s.roll_number";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setInt(2, yearId);
            ps.setInt(3, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRow(rs));
                }
            }
        }
        return students;
    }

    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (roll_number, full_name, email, phone, department_id, academic_year_id, section_id, is_active) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStudentParams(ps, student);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET roll_number = ?, full_name = ?, email = ?, phone = ?, " +
            "department_id = ?, academic_year_id = ?, section_id = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setStudentParams(ps, student);
            ps.setInt(9, student.getId());
            ps.executeUpdate();
        }
    }

    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE students SET is_active = 0 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void activate(int id) throws SQLException {
        String sql = "UPDATE students SET is_active = 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean rollNumberExists(String rollNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE roll_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean rollNumberExistsExcluding(String rollNumber, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE roll_number = ? AND id != ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private List<Student> executeQuery(String sql) throws SQLException {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        }
        return students;
    }

    private void setStudentParams(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.getRollNumber());
        ps.setString(2, s.getFullName());
        ps.setString(3, s.getEmail());
        ps.setString(4, s.getPhone());
        ps.setInt(5, s.getDepartmentId());
        ps.setInt(6, s.getAcademicYearId());
        ps.setInt(7, s.getSectionId());
        ps.setBoolean(8, s.isActive());
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setDepartmentId(rs.getInt("department_id"));
        s.setAcademicYearId(rs.getInt("academic_year_id"));
        s.setSectionId(rs.getInt("section_id"));
        s.setActive(rs.getBoolean("is_active"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            s.setDepartmentName(rs.getString("department_name"));
            s.setYearLabel(rs.getString("year_label"));
            s.setSectionName(rs.getString("section_name"));
        } catch (SQLException ignored) {
            // Columns might not exist in all queries
        }
        return s;
    }
}
