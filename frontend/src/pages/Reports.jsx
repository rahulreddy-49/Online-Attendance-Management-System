import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const Reports = ({ showToast }) => {
  const [departments, setDepartments] = useState([]);
  const [years, setYears] = useState([]);
  const [sections, setSections] = useState([]);
  const [subjects, setSubjects] = useState([]);

  const [selectedDept, setSelectedDept] = useState('');
  const [selectedYear, setSelectedYear] = useState('');
  const [selectedSec, setSelectedSec] = useState('');
  const [selectedSub, setSelectedSub] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');

  const [reportData, setReportData] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);

  async function loadLookups() {
    const [d, y, s, sub] = await Promise.all([
      api.getDepartments(),
      api.getAcademicYears(),
      api.getSections(),
      api.getSubjects()
    ]);
    setDepartments(d);
    setYears(y);
    setSections(s);
    setSubjects(sub);

    if (d.length > 0) setSelectedDept(d[0].id);
    if (y.length > 0) setSelectedYear(y[0].id);
    if (s.length > 0) setSelectedSec(s[0].id);
  }

  useEffect(() => {
    loadLookups();
  }, []);

  const handleGenerate = async (e) => {
    if (e) e.preventDefault();
    if (!selectedDept || !selectedYear || !selectedSec) {
      showToast('Department, Year, and Section are required', 'error');
      return;
    }

    setLoading(true);
    const data = await api.getReports({
      departmentId: selectedDept,
      academicYearId: selectedYear,
      sectionId: selectedSec,
      subjectId: selectedSub,
      fromDate,
      toDate
    });
    setReportData(data);
    setLoading(false);
  };

  const exportCSV = () => {
    if (reportData.length === 0) {
      showToast('No report data to export', 'error');
      return;
    }

    let csvContent = 'Roll Number,Student Name,Total Sessions,Present Count,Absent Count,Attendance Percentage,Eligibility Status\n';
    reportData.forEach(r => {
      const status = r.percentage >= 75 ? 'Eligible' : (r.percentage >= 65 ? 'Shortage Warning' : 'Critical Defaulter');
      csvContent += `"${r.rollNumber}","${r.fullName}",${r.totalSessions},${r.presentCount},${r.absentCount},${r.percentage}%,${status}\n`;
    });

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `attendance_report_${Date.now()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showToast('Report downloaded as CSV!', 'success');
  };

  const filtered = reportData.filter((r) => {
    return search.trim() === '' ||
      r.rollNumber.toLowerCase().includes(search.toLowerCase()) ||
      r.fullName.toLowerCase().includes(search.toLowerCase());
  });

  return (
    <div className="card-box">
      <div className="card-box-header">
        <div>
          <h2>Attendance Intelligence & Defaulter Reports</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
            Calculate individual student percentages, detect attendance shortage, and export audit sheets
          </p>
        </div>
        {reportData.length > 0 && (
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button className="btn-action secondary sm" onClick={() => window.print()}>
              🖨️ Print Report
            </button>
            <button className="btn-action primary sm" onClick={exportCSV}>
              📥 Export CSV
            </button>
          </div>
        )}
      </div>

      {/* Filter Form */}
      <form onSubmit={handleGenerate} style={{ padding: '1.5rem 1.75rem', background: '#fbfcfe', borderBottom: '1px solid var(--border-color)', display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'flex-end' }}>
        <div style={{ flex: 1, minWidth: '180px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>Department *</label>
          <select className="input-field" value={selectedDept} onChange={(e) => setSelectedDept(e.target.value)} required>
            {departments.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
          </select>
        </div>

        <div style={{ minWidth: '140px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>Academic Year *</label>
          <select className="input-field" value={selectedYear} onChange={(e) => setSelectedYear(e.target.value)} required>
            {years.map(y => <option key={y.id} value={y.id}>{y.yearLabel}</option>)}
          </select>
        </div>

        <div style={{ minWidth: '120px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>Section *</label>
          <select className="input-field" value={selectedSec} onChange={(e) => setSelectedSec(e.target.value)} required>
            {sections.map(s => <option key={s.id} value={s.id}>Section {s.name}</option>)}
          </select>
        </div>

        <div style={{ minWidth: '180px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>Subject (Optional)</label>
          <select className="input-field" value={selectedSub} onChange={(e) => setSelectedSub(e.target.value)}>
            <option value="">All Subjects Combined</option>
            {subjects.map(s => <option key={s.id} value={s.id}>{s.name} ({s.code})</option>)}
          </select>
        </div>

        <div style={{ minWidth: '135px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>From Date</label>
          <input type="date" className="input-field" value={fromDate} onChange={(e) => setFromDate(e.target.value)} />
        </div>

        <div style={{ minWidth: '135px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>To Date</label>
          <input type="date" className="input-field" value={toDate} onChange={(e) => setToDate(e.target.value)} />
        </div>

        <button type="submit" className="btn-action primary" style={{ height: '42px' }}>
          {loading ? 'Calculating...' : 'Generate Report'}
        </button>
      </form>

      {/* Report Table Display */}
      {reportData.length > 0 ? (
        <div>
          <div style={{ padding: '1rem 1.75rem', background: '#f8fafc', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ fontSize: '0.88rem', color: 'var(--text-muted)' }}>
              Showing attendance report for <strong>{reportData.length}</strong> students. Minimum institutional requirement: <strong>75%</strong>
            </div>
            <input
              type="text"
              className="input-field"
              style={{ maxWidth: '280px', padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}
              placeholder="Search student in report..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div className="responsive-table-box">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Roll Number</th>
                  <th>Student Name</th>
                  <th>Total Sessions</th>
                  <th>Present</th>
                  <th>Absent</th>
                  <th>Attendance %</th>
                  <th>Shortage Status</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((r, idx) => (
                  <tr key={idx}>
                    <td>{idx + 1}</td>
                    <td><strong>{r.rollNumber}</strong></td>
                    <td><div style={{ fontWeight: 600 }}>{r.fullName}</div></td>
                    <td>{r.totalSessions}</td>
                    <td><span className="status-pill pill-present">{r.presentCount}</span></td>
                    <td><span className="status-pill pill-absent">{r.absentCount}</span></td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <div style={{ flex: 1, maxWidth: '90px', height: '8px', background: '#e2e8f0', borderRadius: '999px', overflow: 'hidden' }}>
                          <div style={{
                            width: `${r.percentage}%`,
                            height: '100%',
                            background: r.percentage >= 75 ? 'var(--success)' : (r.percentage >= 65 ? 'var(--warning)' : 'var(--danger)')
                          }} />
                        </div>
                        <span style={{ fontWeight: 700 }}>{r.percentage}%</span>
                      </div>
                    </td>
                    <td>
                      {r.percentage >= 75 ? (
                        <span className="turnout-pill turnout-good">Eligible</span>
                      ) : r.percentage >= 65 ? (
                        <span className="turnout-pill turnout-warning">Shortage Warning</span>
                      ) : (
                        <span className="turnout-pill turnout-danger">Defaulter Alert</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div style={{ textAlign: 'center', padding: '4.5rem 1.5rem' }}>
          <div style={{ fontSize: '2.8rem', marginBottom: '0.75rem' }}>📊</div>
          <h3 style={{ fontSize: '1.25rem', marginBottom: '0.35rem' }}>Institutional Report Ready</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', maxWidth: '450px', margin: '0 auto' }}>
            Choose Department, Academic Year, and Class Section above and click <strong>"Generate Report"</strong> to calculate real-time attendance percentages.
          </p>
        </div>
      )}
    </div>
  );
};
