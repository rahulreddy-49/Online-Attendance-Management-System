import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Modal } from '../components/Modal';

export const SubjectsManagement = ({ showToast }) => {
  const [subjects, setSubjects] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [years, setYears] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingSubject, setEditingSubject] = useState(null);
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    departmentId: '',
    academicYearId: '',
    credits: 3
  });

  async function loadData() {
    setLoading(true);
    const [s, d, y] = await Promise.all([api.getSubjects(), api.getDepartments(), api.getAcademicYears()]);
    setSubjects(s);
    setDepartments(d);
    setYears(y);
    setLoading(false);
  }

  useEffect(() => {
    loadData();
  }, []);

  const openAddModal = () => {
    setEditingSubject(null);
    setFormData({
      code: '',
      name: '',
      departmentId: departments[0]?.id || 1,
      academicYearId: years[0]?.id || 1,
      credits: 3
    });
    setModalOpen(true);
  };

  const openEditModal = (sub) => {
    setEditingSubject(sub);
    setFormData({
      id: sub.id,
      code: sub.code,
      name: sub.name,
      departmentId: sub.departmentId,
      academicYearId: sub.academicYearId,
      credits: sub.credits || 3
    });
    setModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const res = await api.saveSubject(formData);
    if (res.success) {
      showToast('Subject saved successfully!', 'success');
      setModalOpen(false);
      loadData();
    } else {
      showToast(res.message || 'Error saving subject', 'error');
    }
  };

  const handleDelete = async (id, name) => {
    if (window.confirm(`Deactivate subject "${name}"?`)) {
      await api.deleteSubject(id);
      showToast('Subject deactivated', 'success');
      loadData();
    }
  };

  const filtered = subjects.filter((s) => {
    return search.trim() === '' ||
      s.code.toLowerCase().includes(search.toLowerCase()) ||
      s.name.toLowerCase().includes(search.toLowerCase());
  });

  return (
    <div className="card-box">
      <div className="card-box-header">
        <div>
          <h2>Curriculum & Course Subjects</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
            Manage curriculum catalog, academic credit weights, and course affiliations
          </p>
        </div>
        <button className="btn-action primary sm" onClick={openAddModal}>
          + Create Subject
        </button>
      </div>

      <div style={{ padding: '1.25rem 1.75rem', background: '#fbfcfe', borderBottom: '1px solid var(--border-color)' }}>
        <input
          type="text"
          className="input-field"
          style={{ maxWidth: '400px' }}
          placeholder="Search by subject code or title..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="responsive-table-box">
        <table className="custom-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Code</th>
              <th>Subject Name</th>
              <th>Department</th>
              <th>Year</th>
              <th>Credits</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="8" style={{ textAlign: 'center', padding: '2.5rem' }}>Loading curriculum...</td></tr>
            ) : filtered.length > 0 ? (
              filtered.map((s, idx) => (
                <tr key={s.id}>
                  <td>{idx + 1}</td>
                  <td><span className="status-pill pill-tag">{s.code}</span></td>
                  <td><strong>{s.name}</strong></td>
                  <td>{s.departmentName}</td>
                  <td>{s.academicYearLabel}</td>
                  <td>{s.credits} Credits</td>
                  <td>
                    <span className={`status-pill ${s.active ? 'pill-active' : 'pill-inactive'}`}>
                      {s.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '0.4rem' }}>
                      <button className="btn-action secondary sm" onClick={() => openEditModal(s)}>Edit</button>
                      {s.active && (
                        <button className="btn-action danger sm" onClick={() => handleDelete(s.id, s.name)}>Deactivate</button>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                  No subjects found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editingSubject ? 'Edit Subject' : 'Add Curriculum Subject'}>
        <form onSubmit={handleSave}>
          <div className="form-row-grid">
            <div className="form-item">
              <label>Subject Code *</label>
              <input
                type="text"
                className="input-field"
                value={formData.code}
                onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                placeholder="e.g. CS304"
                required
              />
            </div>
            <div className="form-item">
              <label>Subject Name *</label>
              <input
                type="text"
                className="input-field"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="e.g. Computer Networks"
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
              <label>Credits</label>
              <input
                type="number"
                min="1"
                max="10"
                className="input-field"
                value={formData.credits}
                onChange={(e) => setFormData({ ...formData, credits: parseInt(e.target.value) || 3 })}
                required
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" className="btn-action secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn-action primary">Save Subject</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
