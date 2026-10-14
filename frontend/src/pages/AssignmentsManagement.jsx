import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Modal } from '../components/Modal';

export const AssignmentsManagement = ({ showToast }) => {
  const [assignments, setAssignments] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [years, setYears] = useState([]);
  const [sections, setSections] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    teacherId: '',
    subjectId: '',
    academicYearId: '',
    sectionId: ''
  });

  async function loadData() {
    setLoading(true);
    const [a, t, s, y, sec] = await Promise.all([
      api.getAssignments(),
      api.getTeachers(),
      api.getSubjects(),
      api.getAcademicYears(),
      api.getSections()
    ]);
    setAssignments(a);
    setTeachers(t.filter(x => x.active));
    setSubjects(s.filter(x => x.active));
    setYears(y);
    setSections(sec);
    setLoading(false);
  }

  useEffect(() => {
    loadData();
  }, []);

  const openAddModal = () => {
    setFormData({
      teacherId: teachers[0]?.id || 1,
      subjectId: subjects[0]?.id || 1,
      academicYearId: years[0]?.id || 1,
      sectionId: sections[0]?.id || 1
    });
    setModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const res = await api.saveAssignment(formData);
    if (res.success) {
      showToast('Teaching assignment saved successfully!', 'success');
      setModalOpen(false);
      loadData();
    } else {
      showToast(res.message || 'Error saving assignment', 'error');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Remove this teaching assignment?')) {
      await api.deleteAssignment(id);
      showToast('Assignment removed', 'success');
      loadData();
    }
  };

  const filtered = assignments.filter((a) => {
    return search.trim() === '' ||
      a.teacherName?.toLowerCase().includes(search.toLowerCase()) ||
      a.subjectName?.toLowerCase().includes(search.toLowerCase()) ||
      a.subjectCode?.toLowerCase().includes(search.toLowerCase());
  });

  return (
    <div className="card-box">
      <div className="card-box-header">
        <div>
          <h2>Teacher - Subject Class Assignments</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
            Map faculty instructors to their assigned subjects and student sections
          </p>
        </div>
        <button className="btn-action primary sm" onClick={openAddModal}>
          + Assign Faculty to Class
        </button>
      </div>

      <div style={{ padding: '1.25rem 1.75rem', background: '#fbfcfe', borderBottom: '1px solid var(--border-color)' }}>
        <input
          type="text"
          className="input-field"
          style={{ maxWidth: '400px' }}
          placeholder="Search by faculty, subject, or code..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="responsive-table-box">
        <table className="custom-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Faculty Teacher</th>
              <th>Subject Title</th>
              <th>Subject Code</th>
              <th>Academic Year</th>
              <th>Section</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="8" style={{ textAlign: 'center', padding: '2.5rem' }}>Loading assignments...</td></tr>
            ) : filtered.length > 0 ? (
              filtered.map((a, idx) => (
                <tr key={a.id}>
                  <td>{idx + 1}</td>
                  <td><strong>{a.teacherName}</strong></td>
                  <td>{a.subjectName}</td>
                  <td><span className="status-pill pill-tag">{a.subjectCode}</span></td>
                  <td>{a.academicYearLabel}</td>
                  <td>Section {a.sectionName}</td>
                  <td>
                    <span className={`status-pill ${a.active ? 'pill-active' : 'pill-inactive'}`}>
                      {a.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <button className="btn-action danger sm" onClick={() => handleDelete(a.id)}>Remove</button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                  No assignments found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Assign Teacher to Class Section">
        <form onSubmit={handleSave}>
          <div className="form-item">
            <label>Select Faculty Teacher *</label>
            <select
              className="input-field"
              value={formData.teacherId}
              onChange={(e) => setFormData({ ...formData, teacherId: parseInt(e.target.value) })}
              required
            >
              {teachers.map(t => <option key={t.id} value={t.id}>{t.fullName} ({t.employeeId})</option>)}
            </select>
          </div>

          <div className="form-item">
            <label>Select Subject *</label>
            <select
              className="input-field"
              value={formData.subjectId}
              onChange={(e) => setFormData({ ...formData, subjectId: parseInt(e.target.value) })}
              required
            >
              {subjects.map(s => <option key={s.id} value={s.id}>{s.name} ({s.code})</option>)}
            </select>
          </div>

          <div className="form-row-grid">
            <div className="form-item">
              <label>Academic Year *</label>
              <select
                className="input-field"
                value={formData.academicYearId}
                onChange={(e) => setFormData({ ...formData, academicYearId: parseInt(e.target.value) })}
                required
              >
                {years.map(y => <option key={y.id} value={y.id}>{y.yearLabel}</option>)}
              </select>
            </div>
            <div className="form-item">
              <label>Class Section *</label>
              <select
                className="input-field"
                value={formData.sectionId}
                onChange={(e) => setFormData({ ...formData, sectionId: parseInt(e.target.value) })}
                required
              >
                {sections.map(s => <option key={s.id} value={s.id}>Section {s.name}</option>)}
              </select>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" className="btn-action secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn-action primary">Save Assignment</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
