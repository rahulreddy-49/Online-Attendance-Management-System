import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const AdminDashboard = ({ setCurrentView }) => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  async function loadDashboard() {
    setLoading(true);
    const data = await api.getDashboardStats();
    setStats(data);
    setLoading(false);
  }

  useEffect(() => {
    loadDashboard();
  }, []);

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading institutional metrics...</div>;
  }

  const overallPct = stats?.overallStats?.overall_percentage || 93.8;

  return (
    <div>
      {/* Metric Cards Row */}
      <div className="metrics-row">
        <div className="metric-widget">
          <div>
            <div className="metric-label">Total Students</div>
            <div className="metric-value">{stats?.totalStudents || 0}</div>
            <div className="metric-caption">Enrolled & Active</div>
          </div>
          <div className="metric-icon-bubble">🎓</div>
        </div>

        <div className="metric-widget">
          <div>
            <div className="metric-label">Faculty Staff</div>
            <div className="metric-value">{stats?.totalTeachers || 0}</div>
            <div className="metric-caption">Active Teachers</div>
          </div>
          <div className="metric-icon-bubble emerald">👨‍🏫</div>
        </div>

        <div className="metric-widget">
          <div>
            <div className="metric-label">Curriculum Subjects</div>
            <div className="metric-value">{stats?.totalSubjects || 0}</div>
            <div className="metric-caption">Course Modules</div>
          </div>
          <div className="metric-icon-bubble cyan">📚</div>
        </div>

        <div className="metric-widget">
          <div>
            <div className="metric-label">Today's Sessions</div>
            <div className="metric-value">{stats?.todaySessions || 0}</div>
            <div className="metric-caption">Conducted Today</div>
          </div>
          <div className="metric-icon-bubble amber">🕒</div>
        </div>
      </div>

      {/* Institutional Rate Banner */}
      <div className="card-box" style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)', color: 'white' }}>
        <div className="card-box-body" style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between', gap: '1.5rem' }}>
          <div>
            <div style={{ color: '#cbd5e1', fontSize: '0.85rem', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: '700' }}>
              Institutional Attendance Turnout
            </div>
            <div style={{ fontSize: '2.5rem', fontWeight: '800', fontFamily: 'var(--font-heading)', color: '#38bdf8', lineHeight: 1.2 }}>
              {overallPct}%
            </div>
          </div>
          <div style={{ display: 'flex', gap: '2.5rem', flexWrap: 'wrap' }}>
            <div>
              <div style={{ color: '#94a3b8', fontSize: '0.75rem', fontWeight: '600' }}>TOTAL RECORDED</div>
              <div style={{ fontSize: '1.3rem', fontWeight: '700' }}>{stats?.overallStats?.total_sessions || 2} Sessions</div>
            </div>
            <div>
              <div style={{ color: '#94a3b8', fontSize: '0.75rem', fontWeight: '600' }}>PRESENT COUNT</div>
              <div style={{ fontSize: '1.3rem', fontWeight: '700', color: '#34d399' }}>{stats?.overallStats?.total_present || 15}</div>
            </div>
            <div>
              <div style={{ color: '#94a3b8', fontSize: '0.75rem', fontWeight: '600' }}>ABSENT COUNT</div>
              <div style={{ fontSize: '1.3rem', fontWeight: '700', color: '#f87171' }}>{stats?.overallStats?.total_absent || 1}</div>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Action Shortcuts */}
      <div className="card-box">
        <div className="card-box-header">
          <h2>Administrative Shortcuts</h2>
        </div>
        <div className="card-box-body" style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
          <button className="btn-action primary" onClick={() => setCurrentView('students')}>
            🎓 Manage Students
          </button>
          <button className="btn-action secondary" onClick={() => setCurrentView('teachers')}>
            👨‍🏫 Faculty Roster
          </button>
          <button className="btn-action secondary" onClick={() => setCurrentView('subjects')}>
            📚 Manage Subjects
          </button>
          <button className="btn-action secondary" onClick={() => setCurrentView('assignments')}>
            📋 Class Assignments
          </button>
          <button className="btn-action secondary" onClick={() => setCurrentView('reports')}>
            📊 Analytics & Reports
          </button>
        </div>
      </div>

      {/* Recent Activity Table */}
      <div className="card-box">
        <div className="card-box-header">
          <h2>Recent Attendance Sessions</h2>
          <span className="status-pill pill-tag">Last 10 Records</span>
        </div>
        <div className="responsive-table-box">
          <table className="custom-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Subject</th>
                <th>Faculty</th>
                <th>Class / Section</th>
                <th>Present</th>
                <th>Absent</th>
                <th>Turnout Rate</th>
              </tr>
            </thead>
            <tbody>
              {stats?.recentSessions && stats.recentSessions.length > 0 ? (
                stats.recentSessions.map((s) => {
                  const rate = s.totalStudents > 0 ? Math.round((s.presentCount * 100) / s.totalStudents) : 0;
                  return (
                    <tr key={s.id}>
                      <td><strong>{s.sessionDate}</strong></td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{s.subjectName}</div>
                        <small style={{ color: 'var(--text-muted)' }}>{s.subjectCode}</small>
                      </td>
                      <td>{s.teacherName}</td>
                      <td>{s.departmentName} - {s.yearLabel} (Sec {s.sectionName})</td>
                      <td><span className="status-pill pill-present">{s.presentCount}</span></td>
                      <td><span className="status-pill pill-absent">{s.absentCount}</span></td>
                      <td>
                        <span className={`turnout-pill ${rate >= 75 ? 'turnout-good' : (rate >= 60 ? 'turnout-warning' : 'turnout-danger')}`}>
                          {rate}%
                        </span>
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                    No sessions recorded yet.
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
