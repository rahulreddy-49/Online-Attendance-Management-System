import React from 'react';
import { useAuth } from '../context/AuthContext';

export const Sidebar = ({ currentView, setCurrentView }) => {
  const { user, isAdmin } = useAuth();

  return (
    <aside className="app-sidebar">
      <div className="sidebar-header">
        <div className="brand-badge">✓</div>
        <div className="brand-titles">
          <h2>Attendify</h2>
          <span>Enterprise Pro</span>
        </div>
      </div>

      <nav className="sidebar-menu">
        {isAdmin ? (
          <>
            <div className="menu-category">Main Administration</div>
            <button
              className={`menu-btn ${currentView === 'admin-dashboard' ? 'active' : ''}`}
              onClick={() => setCurrentView('admin-dashboard')}
            >
              <span className="menu-icon">📊</span>
              <span>Dashboard</span>
            </button>
            <button
              className={`menu-btn ${currentView === 'students' ? 'active' : ''}`}
              onClick={() => setCurrentView('students')}
            >
              <span className="menu-icon">🎓</span>
              <span>Students</span>
            </button>
            <button
              className={`menu-btn ${currentView === 'teachers' ? 'active' : ''}`}
              onClick={() => setCurrentView('teachers')}
            >
              <span className="menu-icon">👨‍🏫</span>
              <span>Faculty & Teachers</span>
            </button>
            <button
              className={`menu-btn ${currentView === 'subjects' ? 'active' : ''}`}
              onClick={() => setCurrentView('subjects')}
            >
              <span className="menu-icon">📚</span>
              <span>Curriculum Subjects</span>
            </button>
            <button
              className={`menu-btn ${currentView === 'assignments' ? 'active' : ''}`}
              onClick={() => setCurrentView('assignments')}
            >
              <span className="menu-icon">📋</span>
              <span>Class Assignments</span>
            </button>

            <div className="menu-category">Intelligence & Reports</div>
            <button
              className={`menu-btn ${currentView === 'reports' ? 'active' : ''}`}
              onClick={() => setCurrentView('reports')}
            >
              <span className="menu-icon">📈</span>
              <span>Attendance Reports</span>
            </button>
          </>
        ) : (
          <>
            <div className="menu-category">Faculty Workspace</div>
            <button
              className={`menu-btn ${currentView === 'teacher-dashboard' ? 'active' : ''}`}
              onClick={() => setCurrentView('teacher-dashboard')}
            >
              <span className="menu-icon">📊</span>
              <span>My Dashboard</span>
            </button>
            <button
              className={`menu-btn ${currentView === 'mark-attendance' ? 'active' : ''}`}
              onClick={() => setCurrentView('mark-attendance')}
            >
              <span className="menu-icon">✍️</span>
              <span>Mark Attendance</span>
            </button>
            <button
              className={`menu-btn ${currentView === 'attendance-history' ? 'active' : ''}`}
              onClick={() => setCurrentView('attendance-history')}
            >
              <span className="menu-icon">🕒</span>
              <span>Attendance History</span>
            </button>
            <button
              className={`menu-btn ${currentView === 'reports' ? 'active' : ''}`}
              onClick={() => setCurrentView('reports')}
            >
              <span className="menu-icon">📑</span>
              <span>Class Reports</span>
            </button>
          </>
        )}
      </nav>

      <div className="sidebar-profile">
        <div className="profile-avatar">
          {user?.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
        </div>
        <div className="profile-meta">
          <div className="profile-name">{user?.fullName || 'User'}</div>
          <div className="profile-role">{user?.role || 'Guest'}</div>
        </div>
      </div>
    </aside>
  );
};
