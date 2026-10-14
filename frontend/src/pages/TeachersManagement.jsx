import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Modal } from '../components/Modal';

export const TeachersManagement = ({ showToast }) => {
  const [teachers, setTeachers] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState(null);
  const [formData, setFormData] = useState({
    fullName: '',
    employeeId: '',
    username: '',
    password: '',
    departmentId: '',
    email: '',
    phone: '',
    qualification: ''
  });

  async function loadData() {
    setLoading(true);
    const [t, d] = await Promise.all([api.getTeachers(), api.getDepartments()]);
    setTeachers(t);
    setDepartments(d);
    setLoading(false);
  }

  useEffect(() => {
    loadData();
  }, []);

  const openAddModal = () => {
    setEditingTeacher(null);
    setFormData({
      fullName: '',
      employeeId: '',
      username: '',
      password: '',
      departmentId: departments[0]?.id || 1,
      email: '',
      phone: '',
      qualification: ''
    });
    setModalOpen(true);
  };

  const openEditModal = (t) => {
    setEditingTeacher(t);
    setFormData({
      id: t.id,
      fullName: t.fullName,
      employeeId: t.employeeId,
      departmentId: t.departmentId,
      email: t.email || '',
      phone: t.phone || '',
      qualification: t.qualification || ''
    });
    setModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const res = await api.saveTeacher(formData);
    if (res.success) {
      showToast('Faculty member saved successfully!', 'success');
      setModalOpen(false);
      loadData();
    } else {
      showToast(res.message || 'Error saving teacher', 'error');
    }
  };

  const handleDelete = async (id, name) => {
    if (window.confirm(`Deactivate faculty "${name}"?`)) {
      await api.deleteTeacher(id);
      showToast('Faculty deactivated', 'success');
      loadData();
    }
  };

  const filtered = teachers.filter((t) => {
    return search.trim() === '' ||
      t.fullName.toLowerCase().includes(search.toLowerCase()) ||
      t.employeeId.toLowerCase().includes(search.toLowerCase());
  });

  return (
    <div className="card-box">
      <div className="card-box-header">
        <div>
          <h2>Faculty & Teacher Roster</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
            Manage instructors, credentials, qualifications, and department affiliations
          </p>
        </div>
        <button className="btn-action primary sm" onClick={openAddModal}>
          + Register Faculty Member
        </button>
      </div>

      <div style={{ padding: '1.25rem 1.75rem', background: '#fbfcfe', borderBottom: '1px solid var(--border-color)' }}>
        <input
          type="text"
          className="input-field"
          style={{ maxWidth: '400px' }}
          placeholder="Search by teacher name or employee ID..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="responsive-table-box">
        <table className="custom-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Employee ID</th>
              <th>Faculty Name</th>
              <th>Department</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Qualification</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="9" style={{ textAlign: 'center', padding: '2.5rem' }}>Loading faculty...</td></tr>
            ) : filtered.length > 0 ? (
              filtered.map((t, idx) => (
                <tr key={t.id}>
                  <td>{idx + 1}</td>
                  <td><strong>{t.employeeId}</strong></td>
                  <td><div style={{ fontWeight: 600 }}>{t.fullName}</div></td>
                  <td>{t.departmentName}</td>
                  <td>{t.email || '-'}</td>
                  <td>{t.phone || '-'}</td>
                  <td>{t.qualification || '-'}</td>
                  <td>
                    <span className={`status-pill ${t.active ? 'pill-active' : 'pill-inactive'}`}>
                      {t.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '0.4rem' }}>
                      <button className="btn-action secondary sm" onClick={() => openEditModal(t)}>Edit</button>
                      {t.active && (
                        <button className="btn-action danger sm" onClick={() => handleDelete(t.id, t.fullName)}>Deactivate</button>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="9" style={{ textAlign: 'center', padding: '2.5rem', color: 'var(--text-muted)' }}>
                  No teachers found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editingTeacher ? 'Edit Faculty Details' : 'Register New Faculty Member'}>
        <form onSubmit={handleSave}>
          <div className="form-row-grid">
            <div className="form-item">
              <label>Full Name *</label>
              <input
                type="text"
                className="input-field"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                placeholder="Dr. John von Neumann"
                required
              />
            </div>
            <div className="form-item">
              <label>Employee ID *</label>
              <input
                type="text"
                className="input-field"
                value={formData.employeeId}
                onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
                placeholder="TCH-003"
                required
              />
            </div>
          </div>

          {!editingTeacher && (
            <div className="form-row-grid">
              <div className="form-item">
                <label>Login Username *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.username}
                  onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  placeholder="john.neumann"
                  required
                />
              </div>
              <div className="form-item">
                <label>Password *</label>
                <input
                  type="password"
                  className="input-field"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  placeholder="Min. 6 chars"
                  required
                />
              </div>
            </div>
          )}

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
              <label>Qualification / Title</label>
              <input
                type="text"
                className="input-field"
                value={formData.qualification}
                onChange={(e) => setFormData({ ...formData, qualification: e.target.value })}
                placeholder="M.Tech, Ph.D"
              />
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
                placeholder="faculty@institution.edu"
              />
            </div>
            <div className="form-item">
              <label>Phone Number</label>
              <input
                type="text"
                className="input-field"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="+91 9876500003"
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" className="btn-action secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button type="submit" className="btn-action primary">Save Faculty Member</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
