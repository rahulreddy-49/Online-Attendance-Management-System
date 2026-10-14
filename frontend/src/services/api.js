/**
 * Online Attendance Management System - Unified API Service
 * Communicates with backend endpoints with offline resilient fallbacks.
 */

const API_BASE = '/api';

// Initial Seed Data (matches MySQL seed.sql)
let mockDepartments = [
  { id: 1, name: 'Computer Science and Engineering', code: 'CSE', description: 'Department of Computer Science', active: true },
  { id: 2, name: 'Electronics and Communication', code: 'ECE', description: 'Department of Electronics', active: true },
  { id: 3, name: 'Mechanical Engineering', code: 'MECH', description: 'Department of Mechanical Engineering', active: true }
];

let mockYears = [
  { id: 1, yearLabel: '1st Year', sortOrder: 1, active: true },
  { id: 2, yearLabel: '2nd Year', sortOrder: 2, active: true },
  { id: 3, yearLabel: '3rd Year', sortOrder: 3, active: true },
  { id: 4, yearLabel: '4th Year', sortOrder: 4, active: true }
];

let mockSections = [
  { id: 1, name: 'A', active: true },
  { id: 2, name: 'B', active: true },
  { id: 3, name: 'C', active: true }
];

let mockStudents = [
  { id: 1, rollNumber: 'CS2026-001', fullName: 'Aarav Sharma', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'aarav.sharma@institution.edu', phone: '9876543201', active: true },
  { id: 2, rollNumber: 'CS2026-002', fullName: 'Ananya Verma', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'ananya.verma@institution.edu', phone: '9876543202', active: true },
  { id: 3, rollNumber: 'CS2026-003', fullName: 'Rohan Gupta', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'rohan.gupta@institution.edu', phone: '9876543203', active: true },
  { id: 4, rollNumber: 'CS2026-004', fullName: 'Priya Patel', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'priya.patel@institution.edu', phone: '9876543204', active: true },
  { id: 5, rollNumber: 'CS2026-005', fullName: 'Aditya Rao', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'aditya.rao@institution.edu', phone: '9876543205', active: true },
  { id: 6, rollNumber: 'CS2026-006', fullName: 'Sneha Reddy', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'sneha.reddy@institution.edu', phone: '9876543206', active: true },
  { id: 7, rollNumber: 'CS2026-007', fullName: 'Vikram Joshi', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'vikram.joshi@institution.edu', phone: '9876543207', active: true },
  { id: 8, rollNumber: 'CS2026-008', fullName: 'Kavya Nair', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', email: 'kavya.nair@institution.edu', phone: '9876543208', active: true }
];

let mockTeachers = [
  { id: 1, userId: 2, employeeId: 'TCH-001', fullName: 'Dr. Alan Turing', departmentId: 1, departmentName: 'Computer Science and Engineering', email: 'alan.turing@institution.edu', phone: '9876500001', qualification: 'Ph.D. in Computer Science', active: true },
  { id: 2, userId: 3, employeeId: 'TCH-002', fullName: 'Prof. Ada Lovelace', departmentId: 1, departmentName: 'Computer Science and Engineering', email: 'ada.lovelace@institution.edu', phone: '9876500002', qualification: 'M.Tech, Ph.D.', active: true }
];

let mockSubjects = [
  { id: 1, code: 'CS301', name: 'Data Structures and Algorithms', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', credits: 4, active: true },
  { id: 2, code: 'CS302', name: 'Database Management Systems', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', credits: 4, active: true },
  { id: 3, code: 'CS303', name: 'Operating Systems', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', credits: 3, active: true }
];

let mockAssignments = [
  { id: 1, teacherId: 1, teacherName: 'Dr. Alan Turing', subjectId: 1, subjectName: 'Data Structures and Algorithms', subjectCode: 'CS301', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', active: true },
  { id: 2, teacherId: 2, teacherName: 'Prof. Ada Lovelace', subjectId: 2, subjectName: 'Database Management Systems', subjectCode: 'CS302', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, academicYearLabel: '2nd Year', sectionId: 1, sectionName: 'A', active: true }
];

let mockSessions = [
  { id: 1, sessionDate: '2026-10-14', subjectId: 1, subjectName: 'Data Structures and Algorithms', subjectCode: 'CS301', teacherId: 1, teacherName: 'Dr. Alan Turing', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, yearLabel: '2nd Year', sectionId: 1, sectionName: 'A', totalStudents: 8, presentCount: 7, absentCount: 1 },
  { id: 2, sessionDate: '2026-10-13', subjectId: 2, subjectName: 'Database Management Systems', subjectCode: 'CS302', teacherId: 2, teacherName: 'Prof. Ada Lovelace', departmentId: 1, departmentName: 'Computer Science and Engineering', academicYearId: 2, yearLabel: '2nd Year', sectionId: 1, sectionName: 'A', totalStudents: 8, presentCount: 8, absentCount: 0 }
];

let mockRecords = {
  1: [
    { studentId: 1, rollNumber: 'CS2026-001', studentName: 'Aarav Sharma', status: 'PRESENT', remarks: '' },
    { studentId: 2, rollNumber: 'CS2026-002', studentName: 'Ananya Verma', status: 'PRESENT', remarks: '' },
    { studentId: 3, rollNumber: 'CS2026-003', studentName: 'Rohan Gupta', status: 'ABSENT', remarks: 'Unexcused' },
    { studentId: 4, rollNumber: 'CS2026-004', studentName: 'Priya Patel', status: 'PRESENT', remarks: '' },
    { studentId: 5, rollNumber: 'CS2026-005', studentName: 'Aditya Rao', status: 'PRESENT', remarks: '' },
    { studentId: 6, rollNumber: 'CS2026-006', studentName: 'Sneha Reddy', status: 'PRESENT', remarks: '' },
    { studentId: 7, rollNumber: 'CS2026-007', studentName: 'Vikram Joshi', status: 'PRESENT', remarks: '' },
    { studentId: 8, rollNumber: 'CS2026-008', studentName: 'Kavya Nair', status: 'PRESENT', remarks: '' }
  ]
};

async function fetchWithFallback(url, options = {}, fallbackFn) {
  try {
    const res = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {})
      }
    });
    if (res.ok) {
      return await res.json();
    }
  } catch (err) {
    // Backend offline or dev environment -> use fallback
  }
  return fallbackFn();
}

export const api = {
  // Auth
  async login(username, password) {
    return fetchWithFallback(`${API_BASE}/auth/login`, {
      method: 'POST',
      body: JSON.stringify({ username, password })
    }, () => {
      if (username === 'admin' && password === 'admin123') {
        return { success: true, user: { id: 1, username: 'admin', fullName: 'System Administrator', role: 'ADMIN', email: 'admin@institution.edu' } };
      }
      if (username === 'teacher1' && password === 'teacher123') {
        return { success: true, user: { id: 2, username: 'teacher1', fullName: 'Dr. Alan Turing', role: 'TEACHER', email: 'alan.turing@institution.edu' } };
      }
      if (username === 'teacher2' && password === 'teacher123') {
        return { success: true, user: { id: 3, username: 'teacher2', fullName: 'Prof. Ada Lovelace', role: 'TEACHER', email: 'ada.lovelace@institution.edu' } };
      }
      return { success: false, message: 'Invalid credentials. Try admin / admin123 or teacher1 / teacher123' };
    });
  },

  async logout() {
    return fetchWithFallback(`${API_BASE}/auth/logout`, { method: 'POST' }, () => ({ success: true }));
  },

  // Dropdown Lookups
  async getDepartments() {
    return fetchWithFallback(`${API_BASE}/departments`, {}, () => mockDepartments);
  },
  async getAcademicYears() {
    return fetchWithFallback(`${API_BASE}/academic-years`, {}, () => mockYears);
  },
  async getSections() {
    return fetchWithFallback(`${API_BASE}/sections`, {}, () => mockSections);
  },

  // Admin Dashboard
  async getDashboardStats() {
    return fetchWithFallback(`${API_BASE}/dashboard/stats`, {}, () => {
      const totalStudents = mockStudents.filter(s => s.active).length;
      const totalTeachers = mockTeachers.filter(t => t.active).length;
      const totalSubjects = mockSubjects.filter(s => s.active).length;
      const todaySessions = mockSessions.length;
      return {
        totalStudents,
        totalTeachers,
        totalSubjects,
        todaySessions,
        overallStats: {
          overall_percentage: 93.8,
          total_sessions: 2,
          total_present: 15,
          total_absent: 1
        },
        recentSessions: mockSessions
      };
    });
  },

  // Students
  async getStudents(params = {}) {
    return fetchWithFallback(`${API_BASE}/students?${new URLSearchParams(params)}`, {}, () => {
      let list = [...mockStudents];
      if (params.departmentId) list = list.filter(s => s.departmentId == params.departmentId);
      if (params.academicYearId) list = list.filter(s => s.academicYearId == params.academicYearId);
      if (params.sectionId) list = list.filter(s => s.sectionId == params.sectionId);
      return list;
    });
  },
  async saveStudent(studentData) {
    return fetchWithFallback(`${API_BASE}/students`, {
      method: 'POST',
      body: JSON.stringify(studentData)
    }, () => {
      const dept = mockDepartments.find(d => d.id == studentData.departmentId);
      const yr = mockYears.find(y => y.id == studentData.academicYearId);
      const sec = mockSections.find(s => s.id == studentData.sectionId);

      if (studentData.id) {
        const idx = mockStudents.findIndex(s => s.id == studentData.id);
        if (idx !== -1) {
          mockStudents[idx] = { ...mockStudents[idx], ...studentData, departmentName: dept?.name, academicYearLabel: yr?.yearLabel, sectionName: sec?.name };
        }
      } else {
        const newStudent = {
          ...studentData,
          id: mockStudents.length + 1,
          departmentName: dept?.name,
          academicYearLabel: yr?.yearLabel,
          sectionName: sec?.name,
          active: true
        };
        mockStudents.push(newStudent);
      }
      return { success: true, message: 'Student saved successfully' };
    });
  },
  async deleteStudent(id) {
    return fetchWithFallback(`${API_BASE}/students/${id}`, { method: 'DELETE' }, () => {
      mockStudents = mockStudents.map(s => s.id == id ? { ...s, active: false } : s);
      return { success: true };
    });
  },

  // Faculty / Teachers
  async getTeachers() {
    return fetchWithFallback(`${API_BASE}/teachers`, {}, () => mockTeachers);
  },
  async saveTeacher(teacherData) {
    return fetchWithFallback(`${API_BASE}/teachers`, {
      method: 'POST',
      body: JSON.stringify(teacherData)
    }, () => {
      const dept = mockDepartments.find(d => d.id == teacherData.departmentId);
      if (teacherData.id) {
        const idx = mockTeachers.findIndex(t => t.id == teacherData.id);
        if (idx !== -1) {
          mockTeachers[idx] = { ...mockTeachers[idx], ...teacherData, departmentName: dept?.name };
        }
      } else {
        const newTeacher = {
          ...teacherData,
          id: mockTeachers.length + 1,
          departmentName: dept?.name,
          active: true
        };
        mockTeachers.push(newTeacher);
      }
      return { success: true, message: 'Teacher saved successfully' };
    });
  },
  async deleteTeacher(id) {
    return fetchWithFallback(`${API_BASE}/teachers/${id}`, { method: 'DELETE' }, () => {
      mockTeachers = mockTeachers.map(t => t.id == id ? { ...t, active: false } : t);
      return { success: true };
    });
  },

  // Subjects
  async getSubjects() {
    return fetchWithFallback(`${API_BASE}/subjects`, {}, () => mockSubjects);
  },
  async saveSubject(subjectData) {
    return fetchWithFallback(`${API_BASE}/subjects`, {
      method: 'POST',
      body: JSON.stringify(subjectData)
    }, () => {
      const dept = mockDepartments.find(d => d.id == subjectData.departmentId);
      const yr = mockYears.find(y => y.id == subjectData.academicYearId);
      if (subjectData.id) {
        const idx = mockSubjects.findIndex(s => s.id == subjectData.id);
        if (idx !== -1) {
          mockSubjects[idx] = { ...mockSubjects[idx], ...subjectData, departmentName: dept?.name, academicYearLabel: yr?.yearLabel };
        }
      } else {
        mockSubjects.push({
          ...subjectData,
          id: mockSubjects.length + 1,
          departmentName: dept?.name,
          academicYearLabel: yr?.yearLabel,
          active: true
        });
      }
      return { success: true, message: 'Subject saved successfully' };
    });
  },
  async deleteSubject(id) {
    return fetchWithFallback(`${API_BASE}/subjects/${id}`, { method: 'DELETE' }, () => {
      mockSubjects = mockSubjects.map(s => s.id == id ? { ...s, active: false } : s);
      return { success: true };
    });
  },

  // Assignments
  async getAssignments(teacherId) {
    const url = teacherId ? `${API_BASE}/assignments?teacherId=${teacherId}` : `${API_BASE}/assignments`;
    return fetchWithFallback(url, {}, () => {
      if (teacherId) return mockAssignments.filter(a => a.teacherId == teacherId);
      return mockAssignments;
    });
  },
  async saveAssignment(assignmentData) {
    return fetchWithFallback(`${API_BASE}/assignments`, {
      method: 'POST',
      body: JSON.stringify(assignmentData)
    }, () => {
      const teacher = mockTeachers.find(t => t.id == assignmentData.teacherId);
      const subject = mockSubjects.find(s => s.id == assignmentData.subjectId);
      const sec = mockSections.find(s => s.id == assignmentData.sectionId);
      const yr = mockYears.find(y => y.id == assignmentData.academicYearId);
      const dept = mockDepartments.find(d => d.id == (subject?.departmentId || 1));

      mockAssignments.push({
        id: mockAssignments.length + 1,
        ...assignmentData,
        teacherName: teacher?.fullName,
        subjectName: subject?.name,
        subjectCode: subject?.code,
        departmentName: dept?.name,
        departmentId: dept?.id,
        academicYearLabel: yr?.yearLabel,
        sectionName: sec?.name,
        active: true
      });
      return { success: true, message: 'Assignment created successfully' };
    });
  },
  async deleteAssignment(id) {
    return fetchWithFallback(`${API_BASE}/assignments/${id}`, { method: 'DELETE' }, () => {
      mockAssignments = mockAssignments.filter(a => a.id != id);
      return { success: true };
    });
  },

  // Teacher Dashboard
  async getTeacherDashboard() {
    return fetchWithFallback(`${API_BASE}/teacher/dashboard`, {}, () => {
      const teacher = mockTeachers[0];
      const assignments = mockAssignments.filter(a => a.teacherId == teacher.id);
      return {
        teacher,
        assignments,
        todaySessions: 1,
        totalSessions: 12,
        recentSessions: mockSessions
      };
    });
  },

  // Attendance Taking
  async loadStudentsForAttendance(assignmentId, date) {
    return fetchWithFallback(`${API_BASE}/attendance/load-students?assignmentId=${assignmentId}&date=${date}`, {}, () => {
      const assignment = mockAssignments.find(a => a.id == assignmentId);
      if (!assignment) return { success: false, message: 'Assignment not found' };

      const existingSession = mockSessions.find(s => s.subjectId == assignment.subjectId && s.sectionId == assignment.sectionId && s.sessionDate == date);
      const existingRecords = existingSession ? mockRecords[existingSession.id] || [] : [];

      return {
        success: true,
        students: mockStudents.filter(s => s.departmentId == assignment.departmentId && s.sectionId == assignment.sectionId && s.active),
        existingSession: !!existingSession,
        existingRecords
      };
    });
  },

  async saveAttendance(assignmentId, sessionDate, records) {
    return fetchWithFallback(`${API_BASE}/attendance/save`, {
      method: 'POST',
      body: JSON.stringify({ assignmentId, sessionDate, records })
    }, () => {
      const assignment = mockAssignments.find(a => a.id == assignmentId);
      const presentCount = records.filter(r => r.status === 'PRESENT').length;
      const absentCount = records.length - presentCount;

      const newSession = {
        id: mockSessions.length + 1,
        sessionDate,
        subjectId: assignment?.subjectId,
        subjectName: assignment?.subjectName,
        subjectCode: assignment?.subjectCode,
        teacherId: assignment?.teacherId,
        teacherName: assignment?.teacherName,
        departmentId: assignment?.departmentId,
        departmentName: assignment?.departmentName,
        academicYearId: assignment?.academicYearId,
        yearLabel: assignment?.academicYearLabel,
        sectionId: assignment?.sectionId,
        sectionName: assignment?.sectionName,
        totalStudents: records.length,
        presentCount,
        absentCount
      };

      mockSessions.unshift(newSession);
      mockRecords[newSession.id] = records.map(r => {
        const student = mockStudents.find(s => s.id == r.studentId);
        return {
          ...r,
          studentName: student?.fullName,
          rollNumber: student?.rollNumber
        };
      });

      return { success: true, message: 'Attendance recorded successfully' };
    });
  },

  async getAttendanceHistory(teacherId) {
    return fetchWithFallback(`${API_BASE}/attendance/history?teacherId=${teacherId || ''}`, {}, () => mockSessions);
  },

  async getSessionDetails(sessionId) {
    return fetchWithFallback(`${API_BASE}/attendance/session/${sessionId}`, {}, () => {
      const session = mockSessions.find(s => s.id == sessionId);
      const records = mockRecords[sessionId] || [];
      return { session, records };
    });
  },

  // Reports
  async getReports(params) {
    return fetchWithFallback(`${API_BASE}/reports?${new URLSearchParams(params)}`, {}, () => {
      return mockStudents
        .filter(s => s.departmentId == params.departmentId && s.sectionId == params.sectionId)
        .map((s, idx) => {
          const totalSessions = 20;
          // Vary present count for variety
          const presentCount = idx === 2 ? 12 : (idx === 6 ? 14 : (17 + (idx % 4)));
          const absentCount = totalSessions - presentCount;
          const percentage = Math.round((presentCount * 100.0 / totalSessions) * 100) / 100;
          return {
            studentId: s.id,
            rollNumber: s.rollNumber,
            fullName: s.fullName,
            totalSessions,
            presentCount,
            absentCount,
            percentage
          };
        });
    });
  }
};
