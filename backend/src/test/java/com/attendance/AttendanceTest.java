package com.attendance;

import com.attendance.model.*;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AttendanceTest {

    @Test
    public void testPasswordHashing() {
        String rawPassword = "password123";
        String hash = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
        assertNotNull(hash);

        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hash);
        assertTrue(result.verified);

        BCrypt.Result wrongResult = BCrypt.verifyer().verify("wrongPassword".toCharArray(), hash);
        assertFalse(wrongResult.verified);
    }

    @Test
    public void testUserModel() {
        User user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setRole("ADMIN");
        user.setFullName("System Administrator");
        user.setActive(true);

        assertTrue(user.isAdmin());
        assertFalse(user.isTeacher());
        assertEquals("admin", user.getUsername());
        assertEquals("System Administrator", user.getFullName());
    }

    @Test
    public void testStudentModel() {
        Student student = new Student();
        student.setId(101);
        student.setRollNumber("CS2026-001");
        student.setFullName("Rahul Verma");
        student.setDepartmentId(1);
        student.setAcademicYearId(1);
        student.setSectionId(1);
        student.setActive(true);

        assertEquals("CS2026-001", student.getRollNumber());
        assertEquals("Rahul Verma", student.getFullName());
        assertTrue(student.isActive());
    }

    @Test
    public void testAttendanceCalculation() {
        int totalSessions = 20;
        int presentCount = 17;
        double percentage = (presentCount * 100.0) / totalSessions;
        double rounded = Math.round(percentage * 100.0) / 100.0;

        assertEquals(85.0, rounded);
        assertTrue(rounded >= 75.0); // Above 75% attendance criterion
    }

    @Test
    public void testAttendanceSessionModel() {
        AttendanceSession session = new AttendanceSession();
        session.setSubjectId(5);
        session.setTeacherId(2);
        session.setSectionId(1);
        session.setAcademicYearId(2);
        session.setDepartmentId(1);
        session.setSessionDate(Date.valueOf("2026-10-14"));
        session.setTotalStudents(40);
        session.setPresentCount(36);
        session.setAbsentCount(4);

        assertEquals(40, session.getTotalStudents());
        assertEquals(36, session.getPresentCount());
        assertEquals(4, session.getAbsentCount());
        assertEquals(Date.valueOf("2026-10-14"), session.getSessionDate());
    }
}
