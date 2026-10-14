import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Modal } from '../components/Modal';

export const StudentsManagement = ({ showToast }) => {
  const [students, setStudents] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [years, setYears] = useState([]);
  const [sections, setSections] = useState([]);
  const [search, setSearch] = useState('');
  const [filterDept, setFilterDept] = useState('');
  const [filterYear, setFilterYear] = useState('');
  const [filterSec, setFilterSec] = useState('');
  const [loading, setLoading] = useState(true);

  // Modal State
  const [modalOpen, setModalOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [formData, setFormData] = useState({
    rollNumber: '',
    fullName: '',
    departmentId: '',
    academicYearId: '',
    sectionId: '',
    email: '',
    phone: ''
  });

  async function loadLookups() {
    const [d, y, s] = await Promise.all([
      api.getDepartments(),
      api.getAcademicYears(),
      api.getSections()
    ]);
    setDepartments(d);
    setYears(y);
    setSections(s);
  }

  async function loadStudents() {
    setLoading(true);
    const data = await api.getStudents({
      departmentId: filterDept,
      academicYearId: filterYear,
      sectionId: filterSec
    });
    setStudents(data);
    setLoading(false);
  }

  useEffect(() => {
    loadLookups();
    loadStudents();
  }, [filterDept, filterYear, filterSec]);

  const openAddModal = () => {
    setEditingStudent(null);
    setFormData({
      rollNumber: '',
      fullName: '',
      departmentId: departments[0]?.id || 1,
      academicYearId: years[0]?.id || 1,
      sectionId: sections[0]?.id || 1,
      email: '',
      phone: ''
    });
    setModalOpen(true);
  };

  const openEditModal = (student) => {
    setEditingStudent(student);
    setFormData({
      id: student.id,
      rollNumber: student.rollNumber,
      fullName: student.fullName,
      departmentId: student.departmentId,
      academicYearId: student.academicYearId,
      sectionId: student.sectionId,
      email: student.email || '',
      phone: student.phone || ''
    });
    setModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const res = await api.saveStudent(formData);
    if (res.success) {
      showToast('Student saved successfully!', 'success');
      setModalOpen(false);
      loadStudents();
    } else {
      showToast(res.message || 'Error saving student', 'error');
    }
  };

  const handleDelete = async (id, name) => {
    if (window.confirm(`Deactivate student "${name}"?`)) {
      await api.deleteStudent(id);
      showToast('Student deactivated', 'success');
      loadStudents();
    }
  };

  // Filtered in-memory search
  const filtered = students.filter((s) => {
    const matchSearch = search.trim() === '' ||
      s.rollNumber.toLowerCase().includes(search.toLowerCase()) ||
      s.fullName.toLowerCase().includes(search.toLowerCase());
    const matchDept = !filterDept || s.departmentId == filterDept;
    const matchYear = !filterYear || s.academicYearId == filterYear;
    const matchSec = !filterSec || s.sectionId == filterSec;
    return matchSearch && matchDept && matchYear && matchSec;
  });

  return (
    <div className="card-box">
      <div className="card-box-header">
        <div>
          <h2>Enrolled Students</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
            View, search, register, and update student profiles
          </p>
        </div>
        <button className="btn-action primary sm" onClick={openAddModal}>
          + Register Student
        </button>
      </div>

      {/* Filter Toolbar */}
      <div style={{ padding: '1.25rem 1.75rem', background: '#fbfcfe', borderBottom: '1px solid var(--border-color)', display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'flex-end' }}>
        <div style={{ flex: 1, minWidth: '200px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', display: 'block', marginBottom: '0.35rem' }}>Search Student</label>
          <input
            type="text"
            className="input-field"
            placeholder="Search by roll number or name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div style={{ minWidth: '160px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', display: 'block', marginBottom: '0.35rem' }}>Department</label>
          <select className="input-field" value={filterDept} onChange={(e) => setFilterDept(e.target.value)}>
            <option value="">All Departments</option>
            {departments.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
          </select>
        </div>

        <div style={{ minWidth: '130px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', display: 'block', marginBottom: '0.35rem' }}>Year</label>
          <select className="input-field" value={filterYear} onChange={(e) => setFilterYear(e.target.value)}>
            <option value="">All Years</option>
            {years.map(y => <option key={y.id} value={y.id}>{y.yearLabel}</option>)}
          </select>
        </div>

        <div style={{ minWidth: '120px' }}>
          <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', display: 'block', marginBottom: '0.35rem' }}>Section</label>
          <select className="input-field" value={filterSec} onChange={(e) => setFilterSec(e.target.value)}>
            <option value="">All Sections</option>
            {sections.map(s => <option key={s.id} value={s.id}>Section {s.name}</option>)}
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="responsive-table-box">
        <table className="custom-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Roll Number</th>
              <th>Full Name</th>
              <th>Department</th>
              <th>Year</th>
              <th>Section</th>
              <th>Contact</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="9" style={{ textAlign: 'center', padding: '2.5rem' }}>Loading students...</td></tr>
            ) : filtered.length > 0 ? (
              filtered.map((s, idx) => (
                <tr key={s.id}>
                  <td>{idx + 1}</td>
                  <td><strong>{s.rollNumber}</strong></td>
                  <td><div style={{ fontWeight: 600 }}>{s.fullName}</div></td>
                  <td>{s.departmentName}</td>
                  <td>{s.academicYearLabel}</td>
                  <td>Section {s.sectionName}</td>
                  <td>
                    <div>{s.email || '-'}</div>
                    <small style={{ color: 'var(--text-muted)' }}>{s.phone}</small>
                  </td>
                  <td>
                    <span className={`status-pill ${s.active ? 'pill-active' : 'pill-inactive'}`}>
                      {s.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '0.4rem' }}>
                      <button className="btn-action secondary sm" onClick={() => openEditModal(s)}>Edit</button>
                      {s.active && (
                        <button className="btn-action danger sm" onClick={() => handleDelete(s.id, s.fullName)}>Deactivate</button>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="9" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                  No students found matching current filters.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Add / Edit Student Modal */}
      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editingStudent ? 'Edit Student Details' : 'Register New Student'}>
        <form onSubmit={handleSave}>
          <div className="form-row-grid">
            <div className="form-item">
              <label>Roll Number *</label>
              <input
                type="text"
                className="input-field"
                value={formData.rollNumber}
                onChange={(e) => setFormData({ ...formData, rollNumber: e.target.value })}
                placeholder="e.g. CS2026-009"
                required
              />
            </div>
            <div className="form-item">
              <label>Full Name *</label>
              <input
                type="text"
                className="input-field"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                placeholder="e.g. Sanya Mirza"
                required
              />
            </div>
          </div>

          <div className="form-row-grid">
            <div className="form-item">
              <label>Department *</label>
              <select
                className="input-field"
                value={formData.departmentId}
                onChange={(e) => setFormData({ ...formData, departmentId: e.target.value })}
                required
              >
                {departments.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
              </select>
            </div>
            <div className="form-item">
              <label>Academic Year *</label>
              <select
                className="input-field"
                value={formData.academicYearId}
                onChange={(e) => setFormData({ ...formData, academicYearId: e.target.value })}
                required
              >
                {years.map(y => <option key={y.id} value={y.id}>{y.yearLabel}</option>)}
              </select>
            </div>
            <div className="form-item">
              <label>Section *</label>
              <select
                className="input-field"
                value={formData.sectionId}
                onChange={(e) => setFormData({ ...formData, sectionId: e.target.value })}
                required
              >
                {sections.map(s => <option key={s.id} value={s.id}>Section {s.name}</option>)}
              </select>
            </div>
          </div>

          <div className="form-row-grid">
            <div className="form-item">
              <label>Email Address</label>
              <input
                type="email"
                className="input-field"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="student@institution.edu"
              />
            </div>
            <div className="form-item">
              <label>Phone Number</label>
              <input
                type="text"
                className="input-field"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="+91 9876543210"
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" className="btn-action secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn-action primary">Save Student</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
