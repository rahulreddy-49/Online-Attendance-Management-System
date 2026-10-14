import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const MarkAttendance = ({ showToast, setCurrentView, initialAssignment }) => {
  const [assignments, setAssignments] = useState([]);
  const [selectedAssignmentId, setSelectedAssignmentId] = useState(initialAssignment?.id || '');
  const [sessionDate, setSessionDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [students, setStudents] = useState([]);
  const [attendanceMap, setAttendanceMap] = useState({}); // { studentId: 'PRESENT' | 'ABSENT' }
  const [remarksMap, setRemarksMap] = useState({}); // { studentId: string }
  const [existingSession, setExistingSession] = useState(false);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  async function loadAssignments() {
    const list = await api.getAssignments();
    setAssignments(list);
    if (!selectedAssignmentId && list.length > 0) {
      setSelectedAssignmentId(list[0].id);
    }
  }

  useEffect(() => {
    loadAssignments();
  }, []);

  const handleFetchStudents = async () => {
    if (!selectedAssignmentId) {
      showToast('Please select a class assignment', 'error');
      return;
    }
    if (!sessionDate) {
      showToast('Please pick a session date', 'error');
      return;
    }

    setLoading(true);
    const res = await api.loadStudentsForAttendance(selectedAssignmentId, sessionDate);
    setLoading(false);

    if (res.success) {
      setStudents(res.students);
      setExistingSession(res.existingSession);

      // Pre-fill statuses
      const initialMap = {};
      const initialRemarks = {};

      if (res.existingRecords && res.existingRecords.length > 0) {
        res.existingRecords.forEach(r => {
          initialMap[r.studentId] = r.status;
          initialRemarks[r.studentId] = r.remarks || '';
        });
      } else {
        res.students.forEach(s => {
          initialMap[s.id] = 'PRESENT'; // default Present
          initialRemarks[s.id] = '';
        });
      }

      setAttendanceMap(initialMap);
      setRemarksMap(initialRemarks);
    } else {
      showToast(res.message || 'Failed to load students', 'error');
    }
  };

  const toggleStatus = (studentId, status) => {
    setAttendanceMap(prev => ({ ...prev, [studentId]: status }));
  };

  const markAll = (status) => {
    const updated = {};
    students.forEach(s => {
      updated[s.id] = status;
    });
    setAttendanceMap(updated);
  };

  const handleSaveAttendance = async (e) => {
    e.preventDefault();
    if (students.length === 0) {
      showToast('No student roster loaded to submit', 'error');
      return;
    }

    setSaving(true);
    const records = students.map(s => ({
      studentId: s.id,
      status: attendanceMap[s.id] || 'PRESENT',
      remarks: remarksMap[s.id] || ''
    }));

    const res = await api.saveAttendance(selectedAssignmentId, sessionDate, records);
    setSaving(false);

    if (res.success) {
      showToast('Attendance recorded and committed successfully!', 'success');
      setCurrentView('attendance-history');
    } else {
      showToast(res.message || 'Failed to save attendance', 'error');
    }
  };

  // Calculate live tallies
  const totalStudents = students.length;
  const presentCount = Object.values(attendanceMap).filter(st => st === 'PRESENT').length;
  const absentCount = totalStudents - presentCount;
  const turnoutRate = totalStudents > 0 ? Math.round((presentCount * 100) / totalStudents) : 0;

  return (
    <div className="card-box">
      <div className="card-box-header">
        <div>
          <h2>Record Attendance Session</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
            Select class group and session date to open interactive roll sheet
          </p>
        </div>
        <button className="btn-action secondary sm" onClick={() => setCurrentView('attendance-history')}>
          Attendance History
        </button>
      </div>

      {/* Class & Date Selector Bar */}
      <div style={{ padding: '1.5rem 1.75rem', background: '#fbfcfe', borderBottom: '1px solid var(--border-color)', display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'flex-end' }}>
        <div style={{ flex: 2, minWidth: '260px' }}>
          <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: '0.4rem' }}>Assigned Class Section *</label>
          <select
            className="input-field"
            value={selectedAssignmentId}
            onChange={(e) => setSelectedAssignmentId(parseInt(e.target.value))}
          >
            <option value="">-- Choose Class / Subject --</option>
            {assignments.map(a => (
              <option key={a.id} value={a.id}>
                {a.subjectName} ({a.subjectCode}) - {a.academicYearLabel} Sec {a.sectionName} [{a.departmentName}]
              </option>
            ))}
          </select>
        </div>

        <div style={{ flex: 1, minWidth: '170px' }}>
          <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: '0.4rem' }}>Session Date *</label>
          <input
            type="date"
            className="input-field"
            value={sessionDate}
            onChange={(e) => setSessionDate(e.target.value)}
          />
        </div>

        <div>
          <button
            type="button"
            className="btn-action primary"
            style={{ height: '42px' }}
            onClick={handleFetchStudents}
            disabled={loading}
          >
            {loading ? 'Fetching...' : 'Fetch Students'}
          </button>
        </div>
      </div>

      {/* Existing Session Alert */}
      {existingSession && (
        <div style={{ margin: '1.5rem 1.75rem 0 1.75rem', padding: '0.9rem 1.25rem', background: '#fef3c7', color: '#92400e', borderRadius: '12px', borderLeft: '4px solid #f59e0b', fontSize: '0.88rem', fontWeight: '600' }}>
          ℹ️ Notice: Attendance for this class on {sessionDate} was previously saved. Submitting now will safely update records.
        </div>
      )}

      {/* Attendance Roster & Fast Controls */}
      {students.length > 0 ? (
        <form onSubmit={handleSaveAttendance}>
          {/* Live Metrics Tally & Batch Controls */}
          <div style={{ padding: '1.25rem 1.75rem', borderBottom: '1px solid var(--border-color)', display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', background: 'white' }}>
            <div style={{ display: 'flex', gap: '2rem', alignItems: 'center' }}>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 700 }}>TOTAL ROSTER</span>
                <div style={{ fontSize: '1.4rem', fontWeight: 800, fontFamily: 'var(--font-heading)' }}>{totalStudents}</div>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--success)', fontWeight: 700 }}>PRESENT</span>
                <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--success)', fontFamily: 'var(--font-heading)' }}>{presentCount}</div>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--danger)', fontWeight: 700 }}>ABSENT</span>
                <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--danger)', fontFamily: 'var(--font-heading)' }}>{absentCount}</div>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--primary)', fontWeight: 700 }}>TURNOUT RATE</span>
                <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--primary)', fontFamily: 'var(--font-heading)' }}>{turnoutRate}%</div>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button
                type="button"
                className="btn-action secondary sm"
                style={{ color: 'var(--success)', fontWeight: 700 }}
                onClick={() => markAll('PRESENT')}
              >
                ✓ Mark All Present
              </button>
              <button
                type="button"
                className="btn-action secondary sm"
                style={{ color: 'var(--danger)', fontWeight: 700 }}
                onClick={() => markAll('ABSENT')}
              >
                ✕ Mark All Absent
              </button>
            </div>
          </div>

          {/* Student Roll Table */}
          <div className="responsive-table-box">
            <table className="custom-table">
              <thead>
                <tr>
                  <th style={{ width: '60px' }}>#</th>
                  <th style={{ width: '160px' }}>Roll Number</th>
                  <th>Student Full Name</th>
                  <th style={{ width: '220px' }}>Attendance Status</th>
                  <th>Remarks (Optional)</th>
                </tr>
              </thead>
              <tbody>
                {students.map((s, idx) => {
                  const status = attendanceMap[s.id] || 'PRESENT';
                  return (
                    <tr key={s.id}>
                      <td>{idx + 1}</td>
                      <td><strong>{s.rollNumber}</strong></td>
                      <td><div style={{ fontWeight: 600 }}>{s.fullName}</div></td>
                      <td>
                        <div className="attendance-toggle-switch">
                          <button
                            type="button"
                            className={status === 'PRESENT' ? 'active-present' : ''}
                            onClick={() => toggleStatus(s.id, 'PRESENT')}
                          >
                            Present
                          </button>
                          <button
                            type="button"
                            className={status === 'ABSENT' ? 'active-absent' : ''}
                            onClick={() => toggleStatus(s.id, 'ABSENT')}
                          >
                            Absent
                          </button>
                        </div>
                      </td>
                      <td>
                        <input
                          type="text"
                          className="input-field"
                          style={{ padding: '0.4rem 0.75rem', fontSize: '0.85rem' }}
                          placeholder="Note (e.g. excused, late)"
                          value={remarksMap[s.id] || ''}
                          onChange={(e) => setRemarksMap({ ...remarksMap, [s.id]: e.target.value })}
                        />
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {/* Submit Footer */}
          <div style={{ padding: '1.5rem 1.75rem', background: '#f8fafc', borderTop: '1px solid var(--border-color)', display: 'flex', justifyContent: 'flex-end' }}>
            <button
              type="submit"
              className="btn-action primary"
              style={{ padding: '0.8rem 2.5rem', fontSize: '1rem' }}
              disabled={saving}
            >
              {saving ? 'Saving Attendance...' : 'Save Attendance Session'}
            </button>
          </div>
        </form>
      ) : (
        <div style={{ textAlign: 'center', padding: '4.5rem 1.5rem' }}>
          <div style={{ fontSize: '2.8rem', marginBottom: '0.75rem' }}>✍️</div>
          <h3 style={{ fontSize: '1.25rem', marginBottom: '0.35rem' }}>Attendance Sheet Ready</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', maxWidth: '450px', margin: '0 auto' }}>
            Select your assigned class and session date above, then click <strong>"Fetch Students"</strong> to open the attendance roll.
          </p>
        </div>
      )}
    </div>
  );
};
