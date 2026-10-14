import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

export const Login = () => {
  const { login, loading } = useAuth();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    const res = await login(username, password);
    if (!res.success) {
      setError(res.message);
    }
  };

  const setDemoAccount = (u, p) => {
    setUsername(u);
    setPassword(p);
    setError('');
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-brand">
          <div className="auth-logo">✓</div>
          <h1>Attendify Pro</h1>
          <p>Online Attendance Management System</p>
        </div>

        {error && (
          <div style={{ padding: '0.8rem 1rem', background: '#ffe4e6', color: '#9f1239', borderRadius: '8px', marginBottom: '1.25rem', fontSize: '0.88rem', fontWeight: '500' }}>
            ⚠️ {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-item">
            <label htmlFor="user">Username</label>
            <input
              id="user"
              type="text"
              className="input-field"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              required
            />
          </div>

          <div className="form-item">
            <label htmlFor="pass">Password</label>
            <input
              id="pass"
              type="password"
              className="input-field"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              required
            />
          </div>

          <button
            type="submit"
            className="btn-action primary"
            style={{ width: '100%', padding: '0.85rem', marginTop: '0.5rem', fontSize: '1rem' }}
            disabled={loading}
          >
            {loading ? 'Authenticating...' : 'Sign In to Portal'}
          </button>
        </form>

        <div className="demo-account-pills">
          <h4>Quick Demo Switch</h4>
          <div className="demo-quick-buttons">
            <button
              type="button"
              className="btn-action secondary sm"
              style={{ flex: 1 }}
              onClick={() => setDemoAccount('admin', 'admin123')}
            >
              👑 Login as Admin
            </button>
            <button
              type="button"
              className="btn-action secondary sm"
              style={{ flex: 1 }}
              onClick={() => setDemoAccount('teacher1', 'teacher123')}
            >
              👨‍🏫 Login as Faculty
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
