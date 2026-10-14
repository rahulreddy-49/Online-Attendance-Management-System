import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';

export const TeacherDashboard = ({ setCurrentView, setSelectedAssignment }) => {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  async function loadTeacherData() {
    setLoading(true);
    const res = await api.getTeacherDashboard();
    setData(res);
    setLoading(false);
  }

  useEffect(() => {
    loadTeacherData();
  }, []);

  const handleStartAttendance = (assignment) => {
    if (setSelectedAssignment) {
      setSelectedAssignment(assignment);
    }
    setCurrentView('mark-attendance');
  };

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading faculty dashboard...</div>;
  }

  const teacher = data?.teacher || { fullName: user?.fullName, employeeId: 'TCH-001', departmentName: 'Computer Science and Engineering' };
  const assignments = data?.assignments || [];

  return (
    <div>
      {/* Welcome Banner */}
      <div className="card-box" style={{ background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)', color: 'white' }}>
        <div className="card-box-body" style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between', gap: '1.5rem' }}>
          <div>
            <div style={{ fontSize: '0.85rem', textTransform: 'uppercase', letterSpacing: '0.08em', opacity: 0.85 }}>Faculty Instructor Portal</div>
            <h2 style={{ fontSize: '1.8rem', color: 'white', marginTop: '0.2rem' }}>Welcome, {teacher.fullName}!</h2>
            <div style={{ fontSize: '0.9rem', opacity: 0.9, marginTop: '0.35rem' }}>
              Employee ID: <strong>{teacher.employeeId}</strong> • Department of <strong>{teacher.departmentName}</strong>
            </div>
          </div>
          <div>
            <button
              className="btn-action secondary"
              style={{ background: 'white', color: 'var(--primary)', fontWeight: 700, padding: '0.85rem 1.75rem' }}
              onClick={() => setCurrentView('mark-attendance')}
            >
              ✍️ Take Attendance Now
            </button>
          </div>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="metrics-row">
        <div className="metric-widget">
          <div>
            <div className="metric-label">Assigned Classes</div>
            <div className="metric-value">{assignments.length}</div>
            <div className="metric-caption">Active Teaching Sections</div>
          </div>
          <div className="metric-icon-bubble">📚</div>
        </div>

        <div className="metric-widget">
          <div>
            <div className="metric-label">Today's Sessions</div>
            <div className="metric-value">{data?.todaySessions || 1}</div>
            <div className="metric-caption">Conducted Today</div>
          </div>
          <div className="metric-icon-bubble emerald">🕒</div>
        </div>

        <div className="metric-widget">
          <div>
            <div className="metric-label">Total Sessions</div>
            <div className="metric-value">{data?.totalSessions || 12}</div>
            <div className="metric-caption">Lifetime Recorded</div>
          </div>
          <div className="metric-icon-bubble cyan">📊</div>
        </div>
      </div>

      {/* Assigned Classes */}
      <div className="card-box">
        <div className="card-box-header">
          <h2>My Assigned Teaching Classes</h2>
          <span className="status-pill pill-tag">Current Semester</span>
        </div>
        <div className="responsive-table-box">
          <table className="custom-table">
            <thead>
              <tr>
                <th>Subject Code</th>
                <th>Subject Title</th>
                <th>Academic Year</th>
                <th>Section</th>
                <th>Department</th>
                <th>Quick Action</th>
              </tr>
            </thead>
            <tbody>
              {assignments.length > 0 ? (
                assignments.map((a) => (
                  <tr key={a.id}>
                    <td><span className="status-pill pill-tag">{a.subjectCode}</span></td>
                    <td><strong>{a.subjectName}</strong></td>
                    <td>{a.academicYearLabel}</td>
                    <td>Section {a.sectionName}</td>
                    <td>{a.departmentName}</td>
                    <td>
                      <button className="btn-action primary sm" onClick={() => handleStartAttendance(a)}>
                        Mark Attendance
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                    No assigned classes found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Recent Submissions */}
      <div className="card-box">
        <div className="card-box-header">
          <h2>Recent Attendance Submissions</h2>
          <button className="btn-action secondary sm" onClick={() => setCurrentView('attendance-history')}>
            View All History
          </button>
        </div>
        <div className="responsive-table-box">
          <table className="custom-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Subject</th>
                <th>Class</th>
                <th>Present</th>
                <th>Absent</th>
                <th>Turnout Rate</th>
              </tr>
            </thead>
            <tbody>
              {data?.recentSessions && data.recentSessions.length > 0 ? (
                data.recentSessions.map((s) => {
                  const rate = s.totalStudents > 0 ? Math.round((s.presentCount * 100) / s.totalStudents) : 0;
                  return (
                    <tr key={s.id}>
                      <td><strong>{s.sessionDate}</strong></td>
                      <td>{s.subjectName} ({s.subjectCode})</td>
                      <td>{s.yearLabel} - Section {s.sectionName}</td>
                      <td><span className="status-pill pill-present">{s.presentCount}</span></td>
                      <td><span className="status-pill pill-absent">{s.absentCount}</span></td>
                      <td>
                        <span className={`turnout-pill ${rate >= 75 ? 'turnout-good' : 'turnout-danger'}`}>
                          {rate}%
                        </span>
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                    No sessions logged yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
