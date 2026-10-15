-- ============================================================
-- Seed Data for Development
-- WARNING: This is sample data for development only.
-- Do NOT use these credentials in production.
-- ============================================================

USE attendance_db;

-- ============================================================
-- Default Admin Account
-- Username: admin  |  Password: admin123 (BCrypt hashed)
-- ============================================================
INSERT INTO users (username, password_hash, role, full_name, email, is_active)
VALUES ('admin', '$2a$12$RLwlVNnSYmH/euKjf9a7lumf3yNAJJBgkYiONAYP7fhAZ7tAHO.xS', 'ADMIN', 'System Administrator', 'admin@college.edu', 1)
ON DUPLICATE KEY UPDATE username = username;

-- ============================================================
-- Sample Departments
-- ============================================================
INSERT INTO departments (name, code, description) VALUES
    ('Computer Science & Engineering', 'CSE', 'Department of Computer Science and Engineering'),
    ('Electronics & Communication Engineering', 'ECE', 'Department of Electronics and Communication'),
    ('Electrical & Electronics Engineering', 'EEE', 'Department of Electrical and Electronics'),
    ('Mechanical Engineering', 'ME', 'Department of Mechanical Engineering'),
    ('Civil Engineering', 'CE', 'Department of Civil Engineering')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================================
-- Academic Years
-- ============================================================
INSERT INTO academic_years (year_label, sort_order) VALUES
    ('1st Year', 1),
    ('2nd Year', 2),
    ('3rd Year', 3),
    ('4th Year', 4)
ON DUPLICATE KEY UPDATE year_label = VALUES(year_label);

-- ============================================================
-- Sections
-- ============================================================
INSERT INTO sections (name) VALUES
    ('A'), ('B'), ('C')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================================
-- Sample Teacher Account
-- Username: teacher1  |  Password: teacher123 (BCrypt hashed)
-- ============================================================
INSERT INTO users (username, password_hash, role, full_name, email, is_active)
VALUES ('teacher1', '$2a$12$GrFueUypVE12BNQvYnFxhuF9ssQttNfatFLwn9uBupH5b3zWTloh2', 'TEACHER', 'Dr. Rajesh Kumar', 'rajesh@college.edu', 1)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO teachers (user_id, employee_id, department_id, phone, qualification)
SELECT u.id, 'EMP001', d.id, '9876543210', 'Ph.D. in Computer Science'
FROM users u, departments d
WHERE u.username = 'teacher1' AND d.code = 'CSE'
ON DUPLICATE KEY UPDATE employee_id = VALUES(employee_id);

-- ============================================================
-- Sample Subjects
-- ============================================================
INSERT INTO subjects (name, code, department_id, academic_year_id, credits) VALUES
    ('Data Structures', 'CS201', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='2nd Year'), 4),
    ('Database Management Systems', 'CS301', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='3rd Year'), 4),
    ('Operating Systems', 'CS302', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='3rd Year'), 3),
    ('Digital Electronics', 'EC201', (SELECT id FROM departments WHERE code='ECE'), (SELECT id FROM academic_years WHERE year_label='2nd Year'), 4),
    ('Engineering Mathematics', 'MA101', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='1st Year'), 4)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================================
-- Sample Teacher-Subject Assignments
-- ============================================================
INSERT INTO teacher_subject_assignments (teacher_id, subject_id, section_id, academic_year_id)
SELECT t.id, s.id, sec.id, s.academic_year_id
FROM teachers t, subjects s, sections sec
WHERE t.employee_id = 'EMP001'
  AND s.code IN ('CS201', 'CS301')
  AND sec.name = 'A'
ON DUPLICATE KEY UPDATE is_active = 1;

-- ============================================================
-- Sample Students
-- ============================================================
INSERT INTO students (roll_number, full_name, email, phone, department_id, academic_year_id, section_id) VALUES
    ('CSE2024001', 'Aarav Sharma', 'aarav@student.edu', '9000000001', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='2nd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024002', 'Priya Patel', 'priya@student.edu', '9000000002', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='2nd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024003', 'Rohan Gupta', 'rohan@student.edu', '9000000003', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='2nd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024004', 'Sneha Reddy', 'sneha@student.edu', '9000000004', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='2nd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024005', 'Vikram Singh', 'vikram@student.edu', '9000000005', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='2nd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024006', 'Ananya Iyer', 'ananya@student.edu', '9000000006', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='3rd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024007', 'Karthik Nair', 'karthik@student.edu', '9000000007', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='3rd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024008', 'Divya Menon', 'divya@student.edu', '9000000008', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='3rd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024009', 'Arjun Das', 'arjun@student.edu', '9000000009', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='3rd Year'), (SELECT id FROM sections WHERE name='A')),
    ('CSE2024010', 'Meera Joshi', 'meera@student.edu', '9000000010', (SELECT id FROM departments WHERE code='CSE'), (SELECT id FROM academic_years WHERE year_label='3rd Year'), (SELECT id FROM sections WHERE name='A'))
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);
