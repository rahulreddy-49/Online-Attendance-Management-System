import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Modal } from '../components/Modal';

export const AttendanceHistory = ({ setCurrentView }) => {
  const [sessions, setSessions] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  // Inspection Modal
  const [modalOpen, setModalOpen] = useState(false);
  const [activeSession, setActiveSession] = useState(null);
  const [sessionRecords, setSessionRecords] = useState([]);

  async function loadHistory() {
    setLoading(true);
    const data = await api.getAttendanceHistory();
    setSessions(data);
    setLoading(false);
  }

  useEffect(() => {
    loadHistory();
  }, []);

  const handleInspect = async (session) => {
    setActiveSession(session);
    const res = await api.getSessionDetails(session.id);
    setSessionRecords(res.records || []);
    setModalOpen(true);
  };

  const filtered = sessions.filter((s) => {
    return search.trim() === '' ||
      s.sessionDate?.toLowerCase().includes(search.toLowerCase()) ||
      s.subjectName?.toLowerCase().includes(search.toLowerCase()) ||
      s.subjectCode?.toLowerCase().includes(search.toLowerCase()) ||
      s.teacherName?.toLowerCase().includes(search.toLowerCase());
  });

  return (
    <div className="card-box">
      <div className="card-box-header">
        <div>
          <h2>Attendance Sessions History</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
            Browse and review historical attendance records
          </p>
        </div>
        <button className="btn-action primary sm" onClick={() => setCurrentView('mark-attendance')}>
          + Mark New Session
        </button>
      </div>

      <div style={{ padding: '1.25rem 1.75rem', background: '#fbfcfe', borderBottom: '1px solid var(--border-color)' }}>
        <input
          type="text"
          className="input-field"
          style={{ maxWidth: '400px' }}
          placeholder="Search by date, subject, or faculty..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="responsive-table-box">
        <table className="custom-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Date</th>
              <th>Subject</th>
              <th>Class Section</th>
              <th>Faculty</th>
              <th>Total Roster</th>
              <th>Present</th>
              <th>Absent</th>
              <th>Turnout Rate</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="10" style={{ textAlign: 'center', padding: '2.5rem' }}>Loading sessions...</td></tr>
            ) : filtered.length > 0 ? (
              filtered.map((s, idx) => {
                const rate = s.totalStudents > 0 ? Math.round((s.presentCount * 100) / s.totalStudents) : 0;
                return (
                  <tr key={s.id}>
                    <td>{idx + 1}</td>
                    <td><strong>{s.sessionDate}</strong></td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{s.subjectName}</div>
                      <small style={{ color: 'var(--text-muted)' }}>{s.subjectCode}</small>
                    </td>
                    <td>{s.yearLabel} - Sec {s.sectionName}</td>
                    <td>{s.teacherName}</td>
                    <td>{s.totalStudents}</td>
                    <td><span className="status-pill pill-present">{s.presentCount}</span></td>
                    <td><span className="status-pill pill-absent">{s.absentCount}</span></td>
                    <td>
                      <span className={`turnout-pill ${rate >= 75 ? 'turnout-good' : 'turnout-danger'}`}>
                        {rate}%
                      </span>
                    </td>
                    <td>
                      <button className="btn-action secondary sm" onClick={() => handleInspect(s)}>
                        Inspect Roll
                      </button>
                    </td>
                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan="10" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                  No attendance history found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Session Details Inspection Modal */}
      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Session Attendance Roll">
        {activeSession && (
          <div>
            <div style={{ padding: '1rem', background: '#f8fafc', borderRadius: '12px', border: '1px solid var(--border-color)', marginBottom: '1.25rem', display: 'flex', flexWrap: 'wrap', gap: '1.5rem', justifyContent: 'space-between' }}>
              <div>
                <small style={{ color: 'var(--text-muted)', fontWeight: 600 }}>SUBJECT</small>
                <div style={{ fontWeight: 700 }}>{activeSession.subjectName} ({activeSession.subjectCode})</div>
              </div>
              <div>
                <small style={{ color: 'var(--text-muted)', fontWeight: 600 }}>DATE</small>
                <div style={{ fontWeight: 700 }}>{activeSession.sessionDate}</div>
              </div>
              <div>
                <small style={{ color: 'var(--text-muted)', fontWeight: 600 }}>CLASS</small>
                <div style={{ fontWeight: 700 }}>{activeSession.yearLabel} - Sec {activeSession.sectionName}</div>
              </div>
              <div>
                <small style={{ color: 'var(--text-muted)', fontWeight: 600 }}>TURNOUT</small>
                <div style={{ fontWeight: 800, color: 'var(--primary)' }}>
                  {activeSession.presentCount} / {activeSession.totalStudents} ({activeSession.totalStudents > 0 ? Math.round((activeSession.presentCount * 100) / activeSession.totalStudents) : 0}%)
                </div>
              </div>
            </div>

            <div className="responsive-table-box" style={{ maxHeight: '350px', overflowY: 'auto' }}>
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Roll Number</th>
                    <th>Student Name</th>
                    <th>Status</th>
                    <th>Remarks</th>
                  </tr>
                </thead>
                <tbody>
                  {sessionRecords.map((r, idx) => (
                    <tr key={idx}>
                      <td>{idx + 1}</td>
                      <td><strong>{r.rollNumber}</strong></td>
                      <td>{r.studentName}</td>
                      <td>
                        <span className={`status-pill ${r.status === 'PRESENT' ? 'pill-present' : 'pill-absent'}`}>
                          {r.status}
                        </span>
                      </td>
                      <td>{r.remarks || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
              <button type="button" className="btn-action secondary" onClick={() => window.print()}>
                🖨️ Print Sheet
              </button>
              <button type="button" className="btn-action primary" onClick={() => setModalOpen(false)}>
                Close
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
